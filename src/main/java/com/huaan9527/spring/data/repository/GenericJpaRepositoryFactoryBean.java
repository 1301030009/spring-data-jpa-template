/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.huaan9527.spring.data.repository;

import com.huaan9527.spring.data.TemplateQueryLookupStrategy;
import com.huaan9527.spring.data.annotation.SearchableQuery;
import com.huaan9527.spring.data.search.callback.SearchCallback;
import com.huaan9527.utils.ApplicationContextHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 基础Repostory简单实现 factory bean
 * 请参考 spring-data-jpa-reference [1.4.2. Adding custom behaviour to all repositories]
 *
 * @author Wu Tianqiang
 */
public class GenericJpaRepositoryFactoryBean<R extends JpaRepository<M, ID>, M, ID extends Serializable> extends JpaRepositoryFactoryBean<R, M, ID> implements ApplicationContextAware {

    /**
     * Creates a new {@link JpaRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public GenericJpaRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new GenericJpaRepositoryFactory(entityManager);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.setAppContext(applicationContext);
    }
}

class GenericJpaRepositoryFactory<M, ID extends Serializable> extends JpaRepositoryFactory {

    private EntityManager entityManager;
    private final PersistenceProvider extractor;

    public GenericJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        this.extractor = PersistenceProvider.fromEntityManager(entityManager);
    }

    @SuppressWarnings("unchecked")
    protected Object getTargetRepository(RepositoryInformation information) {
        Class<?> repositoryInterface = information.getRepositoryInterface();

        if (isGenericJpaRepository(repositoryInterface)) {

            JpaEntityInformation<M, ID> entityInformation = getEntityInformation((Class<M>) information.getDomainType());
            GenericJpaRepositoryImpl repository = new GenericJpaRepositoryImpl<>(entityInformation, entityManager);

            SearchableQuery searchableQuery = AnnotationUtils.findAnnotation(repositoryInterface, SearchableQuery.class);
            if (searchableQuery != null) {
                String countAllQL = searchableQuery.countAllQuery();
                if (!StringUtils.isEmpty(countAllQL)) {
                    repository.setCountAllQL(countAllQL);
                }
                String findAllQL = searchableQuery.findAllQuery();
                if (!StringUtils.isEmpty(findAllQL)) {
                    repository.setFindAllQL(findAllQL);
                }
                Class<? extends SearchCallback> callbackClass = searchableQuery.callbackClass();
                if (callbackClass != SearchCallback.class) {
                    repository.setSearchCallback(BeanUtils.instantiate(callbackClass));
                }
            }

            return repository;
        }
        return super.getTargetRepository(information);
    }

    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (isGenericJpaRepository(metadata.getRepositoryInterface())) {
            return GenericJpaRepositoryImpl.class;
        }
        return super.getRepositoryBaseClass(metadata);
    }

    private boolean isGenericJpaRepository(Class<?> repositoryInterface) {
        return GenericJpaRepository.class.isAssignableFrom(repositoryInterface);
    }

    @Override
    protected QueryLookupStrategy getQueryLookupStrategy(QueryLookupStrategy.Key key,
                                                         EvaluationContextProvider evaluationContextProvider) {
        return TemplateQueryLookupStrategy.create(entityManager, key, extractor, evaluationContextProvider);
    }

}
