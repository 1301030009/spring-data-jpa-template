## spring-data-jpa-template

项目地址：
[https://github.com/silentwu/spring-data-jpa-template.git](https://github.com/silentwu/spring-data-jpa-template.git)

#### 主要解决的问题：
1. 动态的拼接SQL,类似mybaits, 通过freemarker 来实现的
2. 能够返回任何对象，List,Page....
3. 在spring data jpa之上扩展，不影响jpa原有功能

#### How to use ?

##### 1. pom.xml中导入如下包：

    <dependency>
    	<groupId>com.rabbit</groupId>
    	<artifactId>spring-data-jpa-template</artifactId>
    	<version>${version}</version>
    </dependency>


##### 2. 项目中所有的 `Repository` 继承 `GenericJpaRepository`，不要使用原来的 `JpaRepository`

    public interface UserRepository extends GenericJpaRepository<User, Long> {
    }

##### 3. 初始化 `FreemarkerSqlTemplates` 配置 sql 模板的位置，默认是在 `classpath:/sql/`

    @Bean
    public FreemarkerSqlTemplates freemarkerSqlTemplates() {
    	FreemarkerSqlTemplates templates = new FreemarkerSqlTemplates();
    	templates.setSuffix(".sftl");   //指定模板的后缀
    	templates.setTemplateLocation("classpath:/sqltemplates");   //模板文件的位置
    	templates.setTemplateBasePackage("com.**.**.test");  //可选
    	templates.setAutoCheck(sqlTemplateAutoCheck);   //是否需要开启自动检测 sql 文件的改变，建议在开发环境中开启，避免重启
    	return templates;
    }

##### 4. 指定 `repositoryFactoryBeanClass`

    @EnableJpaRepositories(
            basePackages = {"com.**.repository"},
            repositoryFactoryBeanClass = GenericJpaRepositoryFactoryBean.class)

##### 5. 创建模板文件 `user.sftl`, 例如：通过id查询用户

    --findById
    SELECT * FROM t_user WHERE id=:id
	
模板文件使用的是`freemarker`, 所有都是`freemarker`的语法

##### 6. 通过 `@TemplateQuery("findById")` 来指定使用sql 来查询

	
    public interface UserRepository extends GenericJpaRepository<User, Long> {
		@TemplateQuery("findById")
		public User findById(@Params("id") Long id);
    }

`@TemplateQuery` 可以指定sql模板文件中对应的名字，如果没有指定，那么默认使用方法的名字



------------

### 查询返回可以是任何类型


    public interface SampleRepository extends GenericJpaRepository<Sample, Long> {
    
    	@TemplateQuery
        Page<Sample> findByContent(String content, Pageable pageable);  //分页实体对象
    
    	@TemplateQuery
    	List<Sample> findByTemplateQueryObject(SampleQuery sampleQuery, Pageable pageable);  //List 实体对象
    
    	@TemplateQuery
    	Long countContent(String content);
    
    	@TemplateQuery
    	List<SampleDTO> findDtos(); List的 DTO 对象
    
    }
    

Dto中的对应可以是 Java 中的基本类型，Map 对象， 自定义的类， 都是通过别人注入。eg:

    public class User {
        private String name;
        private Integer age;
        private Address address;
        private String personLike;
        private Map<String, Object> attrs;
    
    	//.....
    }

对应的 SQL 模板，

    SELECT
    user.name AS 'name',
    user.age,
    address.name AS 'address.name',
    address.code AS 'address.code',
    user.persion_like,
    user.attr AS 'attrs.attr',
    user.attr2 AS 'attrs.attr2'
    FROM
    t_user user
    LEFT JOIN t_address address on user.address_id = address.id
    ...
    

**别名下划线与驼峰可以自动转换**











