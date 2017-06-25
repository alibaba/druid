# Druid Spring Boot Starter

Spring Boot with Druid support,help you simplify Druid config in Spring Boot.

### 如何使用
在Spring Boot项目的pom.xml中添加以下依赖:

```xml
  <dependency>
       <groupId>com.alibaba</groupId>
       <artifactId>druid-spring-boot-starter</artifactId>
       <version>1.1.0</version>
  </dependency>
```
### 配置项
Druid Spring Boot Starter 配置项参数名称完全遵照Druid，你可以通过自定义配置去覆盖默认值。
```xml
# JDBC配置
spring.datasource.url= # 或spring.datasource.druid.url= 
spring.datasource.username= # 或spring.datasource.druid.username=
spring.datasource.password= # 或spring.datasource.druid.password=
spring.datasource.driver-class-name= #或 spring.datasource.druid.driver-class-name=

# 连接池配置，说明请参考Druid Wiki，配置_DruidDataSource参考配置
spring.datasource.druid.filters= # 默认值为stat，配置多个请以英文逗号分隔，如stat,wall,log4j
spring.datasource.druid.initialSize=
spring.datasource.druid.minIdle=
spring.datasource.druid.maxActive=
spring.datasource.druid.maxWait=
spring.datasource.druid.timeBetweenEvictionRunsMillis=
spring.datasource.druid.minEvictableIdleTimeMillis=
spring.datasource.druid.validationQuery=
spring.datasource.druid.testWhileIdle=
spring.datasource.druid.testOnBorrow=
spring.datasource.druid.testOnReturn=
spring.datasource.druid.poolPreparedStatements=
spring.datasource.druid.maxPoolPreparedStatementPerConnectionSize=
# Druid Spring Boot Starter不仅限于对以上连接池配置项的支持，DruidDataSource提供set方法的属性都可在此进行配置，但需自行检查是否生效。

# WebStatFilter配置，说明请参考Druid Wiki，配置_配置WebStatFilter
spring.datasource.druid.WebStatFilter.enabled= #是否启用StatFilter默认值true
spring.datasource.druid.WebStatFilter.urlPattern=
spring.datasource.druid.WebStatFilter.exclusions=
spring.datasource.druid.WebStatFilter.sessionStatMaxCount=
spring.datasource.druid.WebStatFilter.sessionStatEnable=
spring.datasource.druid.WebStatFilter.principalSessionName=
spring.datasource.druid.WebStatFilter.principalCookieName=
spring.datasource.druid.WebStatFilter.profileEnable=

# StatViewServlet配置，说明请参考Druid Wiki，配置_StatViewServlet配置
spring.datasource.druid.StatViewServlet.enabled= # 是否启用StatViewServlet默认值true
spring.datasource.druid.StatViewServlet.urlPattern=
spring.datasource.druid.StatViewServlet.resetEnable=
spring.datasource.druid.StatViewServlet.loginUsername=
spring.datasource.druid.StatViewServlet.loginPassword=
spring.datasource.druid.StatViewServlet.allow=
spring.datasource.druid.StatViewServlet.deny=

# Spring监控配置，说明请参考Druid Github Wiki，配置_配置WebStatFilter
spring.datasource.druid.aop-patterns= # Spring监控AOP切入点，如x.y.z.service.*,配置多个英文逗号分隔
# 如果spring.datasource.druid.aop-patterns要代理的类没有定义interface请设置spring.aop.proxy-target-class=true
```
### 如何配置多数据源
添加数据源配置
```xml
spring.datasource.druid.one.url=
spring.datasource.druid.one.username=
spring.datasource.druid.one.password=
spring.datasource.druid.one.driver-class-name=
spring.datasource.druid.one.maxActive=
...

spring.datasource.druid.two.url=
spring.datasource.druid.two.username=
spring.datasource.druid.two.password=
spring.datasource.druid.two.driver-class-name=
spring.datasource.druid.two.maxActive=
...
```
使用```DruidDataSourceBuilder```创建数据源
```java
    @Primary
    @ConfigurationProperties("spring.datasource.druid.one")
    @Bean
    public DataSource dataSourceOne(){
        return DruidDataSourceBuilder.create().build();
    }
    @ConfigurationProperties("spring.datasource.druid.two")
    @Bean
    public DataSource dataSourceTwo(){
        return DruidDataSourceBuilder.create().build();
    }
```

### 演示
克隆项目，运行```test```包内的```DemoApplication```。
### 参考
[Druid Wiki](https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5)

[Spring Boot Reference](http://docs.spring.io/spring-boot/docs/1.5.1.RELEASE/reference/html/)