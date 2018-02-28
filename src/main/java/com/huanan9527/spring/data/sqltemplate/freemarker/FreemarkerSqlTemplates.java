package com.huanan9527.spring.data.sqltemplate.freemarker;

import com.huanan9527.spring.data.NamedTemplateCallback;
import com.huanan9527.spring.data.NamedTemplateResolver;
import com.huanan9527.spring.data.SftlNamedTemplateResolver;
import com.huanan9527.spring.data.XmlNamedTemplateResolver;
import com.huanan9527.utils.JpaConstants;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FreemarkerSqlTemplates implements ResourceLoaderAware, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

    private static StringTemplateLoader sqlTemplateLoader = new StringTemplateLoader();

    static {
        cfg.setTemplateLoader(sqlTemplateLoader);
    }

    private String encoding = JpaConstants.ENCODING;

    @PersistenceContext
    private EntityManager em;

    private Map<String, Long> lastModifiedCache = new ConcurrentHashMap<>();

    private Map<String, List<Resource>> sqlResources = new ConcurrentHashMap<>();

    private String templateLocation = "classpath:/sqls";

    private String templateBasePackage = "**";

    private ResourceLoader resourceLoader;

    private String suffix = ".xml";

    private Boolean autoCheck = Boolean.TRUE;  //默认开启自动检测SQL文件的更新

    private Map<String, NamedTemplateResolver> suffixResolvers = new HashMap<>();

    {
        suffixResolvers.put(".sftl", new SftlNamedTemplateResolver());
    }

    public String process(String entityName, String methodName, Map<String, Object> model) {
        try {
            if (this.autoCheck && isModified(entityName)) {
                reloadTemplateResource(entityName);
            }
            StringWriter writer = new StringWriter();
            Template template = this.getTemplate(entityName, methodName);
            assert template != null;
            template.process(model, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.error("process template error. Entity name: " + entityName + " methodName:" + methodName, e);
            return StringUtils.EMPTY;
        }
    }

    private Template getTemplate(String entityName, String methodName) {
        String templateKey = getTemplateKey(entityName, methodName);
        try {
            return cfg.getTemplate(templateKey, encoding);
        } catch (IOException e) {
            logger.error("Template not found for name {}", templateKey);
            return null;
        }
    }

    private String getTemplateKey(String entityName, String methodName) {
        return entityName + ":" + methodName;
    }

    private boolean isModified(final String entityName) {
        try {
            Long lastModified = lastModifiedCache.get(entityName);
            List<Resource> resourceList = sqlResources.get(entityName);

            long newLastModified = 0;
            for (Resource resource : resourceList) {
                if (newLastModified == 0) {
                    newLastModified = resource.lastModified();
                } else {
                    //get the last modified.
                    newLastModified = newLastModified > resource.lastModified() ? newLastModified : resource.lastModified();
                }
            }

            //check modified for cache.
            if (lastModified == null || newLastModified > lastModified) {
                lastModifiedCache.put(entityName, newLastModified);
                return true;
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return false;
    }

    private void reloadTemplateResource(String entityName) throws Exception {
        List<Resource> resourceList = sqlResources.get(entityName);
        if (resourceList == null) {
            return;
        }
        //process template.
        for (Resource resource : resourceList) {
            NamedTemplateResolver namedTemplateResolver = suffixResolvers.get(suffix);
            Iterator<Void> iterator = namedTemplateResolver.doInTemplateResource(resource, new NamedTemplateCallback() {
                @Override
                public void process(String templateName, String content) {
                    String key = getTemplateKey(entityName, templateName);
                    Object src = sqlTemplateLoader.findTemplateSource(key);
                    if (src != null) {
                        logger.warn("found duplicate template key, will replace the value, key:" + key);
                    }
                    sqlTemplateLoader.putTemplate(getTemplateKey(entityName, templateName), content);
                }
            });
            while (iterator.hasNext()) {
                iterator.next();
            }
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        XmlNamedTemplateResolver xmlNamedTemplateResolver = new XmlNamedTemplateResolver(resourceLoader);
        xmlNamedTemplateResolver.setEncoding(encoding);
        this.suffixResolvers.put(".xml", xmlNamedTemplateResolver);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<String> entityNames = loadEntityNames();

        resolveSqlResource(entityNames);

        for (String entityName : entityNames) {
            reloadTemplateResource(entityName);
        }

    }

    private void resolveSqlResource(Set<String> names) throws IOException {
        if (names.isEmpty()) {
            return;
        }
        String suffixPattern = "/**/*" + suffix;
        String pattern;
        if (StringUtils.isNotBlank(templateBasePackage)) {
            pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(templateBasePackage) +
                    suffixPattern;
            loadPatternResource(names, pattern);
        }
        if (StringUtils.isNotBlank(templateLocation)) {
            pattern = templateLocation.contains(suffix) ? templateLocation : templateLocation + suffixPattern;
            try {
                loadPatternResource(names, pattern);
            } catch (FileNotFoundException e) {
                if ("classpath:/sqls".equals(templateLocation)) {
                    //warn: default value
                    logger.warn("templateLocation[" + templateLocation + "] not exist!");
                    logger.warn(e.getMessage());
                } else {
                    //throw: custom value.
                    throw e;
                }
            }
        }
    }

    private Set<String> loadEntityNames() {
        Set<String> names = new HashSet<>();
        Set<EntityType<?>> entities = em.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            names.add(entity.getName());
        }
        return names;
    }

    private void loadPatternResource(Set<String> names, String pattern) throws IOException {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        for (Resource resource : resources) {
            String resourceName = resource.getFilename().replace(suffix, "");
            if (names.contains(resourceName)) {
                //allow multi resource.
                List<Resource> resourceList;
                if (sqlResources.containsKey(resourceName)) {
                    resourceList = sqlResources.get(resourceName);
                } else {
                    resourceList = new LinkedList<>();
                    sqlResources.put(resourceName, resourceList);
                }
                resourceList.add(resource);
            }
        }
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public void setTemplateBasePackage(String templateBasePackage) {
        this.templateBasePackage = templateBasePackage;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setAutoCheck(Boolean autoCheck) {
        this.autoCheck = autoCheck;
    }
}
