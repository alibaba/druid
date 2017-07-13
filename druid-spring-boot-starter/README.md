# Druid Spring Boot Starter

Spring Boot with Druid support,help you simplify Druid config in Spring Boot.

### 如何使用
在 Spring Boot 项目的```pom.xml```中添加以下依赖：

```xml
  <dependency>
       <groupId>com.alibaba</groupId>
       <artifactId>druid-spring-boot-starter</artifactId>
       <version>1.1.1</version>
  </dependency>
```
### 配置项
Druid Spring Boot Starter 配置项参数名称完全遵照Druid，你可以通过自定义配置去覆盖默认值。
```xml
# JDBC配置
spring.datasource.druid.url= # 或spring.datasource.url= 
spring.datasource.druid.username= # 或spring.datasource.username=
spring.datasource.druid.password= # 或spring.datasource.password=
spring.datasource.druid.driver-class-name= #或 spring.datasource.driver-class-name=

# 连接池配置，说明请参考Druid Wiki，DruidDataSource配置属性列表
spring.datasource.druid.initial-size=
spring.datasource.druid.max-active=
spring.datasource.druid.min-idle=
spring.datasource.druid.max-wait=
spring.datasource.druid.pool-prepared-statements=
spring.datasource.druid.max-pool-prepared-statement-per-connection-size= 
spring.datasource.druid.max-open-prepared-statements= #等价于上面的max-pool-prepared-statement-per-connection-size
spring.datasource.druid.validation-query=
spring.datasource.druid.validation-query-timeout=
spring.datasource.druid.test-on-borrow=
spring.datasource.druid.test-on-return=
spring.datasource.druid.test-while-idle=
spring.datasource.druid.time-between-eviction-runs-millis=
spring.datasource.druid.min-evictable-idle-time-millis=
spring.datasource.druid.max-evictable-idle-time-millis=
spring.datasource.druid.filters= #默认值stat，配置多个英文逗号分隔

# WebStatFilter配置，说明请参考Druid Wiki，配置_配置WebStatFilter
spring.datasource.druid.web-stat-filter.enabled= #是否启用StatFilter默认值true
spring.datasource.druid.web-stat-filter.urlPattern=
spring.datasource.druid.web-stat-filter.exclusions=
spring.datasource.druid.web-stat-filter.sessionStatMaxCount=
spring.datasource.druid.web-stat-filter.sessionStatEnable=
spring.datasource.druid.web-stat-filter.principalSessionName=
spring.datasource.druid.web-stat-filter.principalCookieName=
spring.datasource.druid.web-stat-filter.profileEnable=

# StatViewServlet配置，说明请参考Druid Wiki，配置_StatViewServlet配置
spring.datasource.druid.stat-view-servlet.enabled= #是否启用StatViewServlet默认值true
spring.datasource.druid.stat-view-servlet.urlPattern=
spring.datasource.druid.stat-view-servlet.resetEnable=
spring.datasource.druid.stat-view-servlet.loginUsername=
spring.datasource.druid.stat-view-servlet.loginPassword=
spring.datasource.druid.stat-view-servlet.allow=
spring.datasource.druid.stat-view-servlet.deny=

# Spring监控配置，说明请参考Druid Github Wiki，配置_Druid和Spring关联监控配置
spring.datasource.druid.aop-patterns= # Spring监控AOP切入点，如x.y.z.service.*,配置多个英文逗号分隔
# 如果spring.datasource.druid.aop-patterns要代理的类没有定义interface请设置spring.aop.proxy-target-class=true
```
### 如何配置多数据源
1. 添加数据源相关配置
```xml
spring.datasource.druid.one.url=
spring.datasource.druid.one.username=
spring.datasource.druid.one.password=
spring.datasource.druid.one.driver-class-name=
spring.datasource.druid.one.max-active=
...

spring.datasource.druid.two.url=
spring.datasource.druid.two.username=
spring.datasource.druid.two.password=
spring.datasource.druid.two.driver-class-name=
spring.datasource.druid.two.max-active=
...
```
2. 使用```DruidDataSourceBuilder```创建数据源
```java
@Bean
@Primary
public DataSource dataSourceOne(Environment env){
   return DruidDataSourceBuilder
           .create()
           .build(env, "spring.datasource.druid.one.");
}
@Bean
public DataSource dataSourceTwo(Environment env){
   return DruidDataSourceBuilder
           .create()
           .build(env, "spring.datasource.druid.two.");
}
```

### 演示
克隆项目，运行```test```包内的```DemoApplication```。
### 参考
[Druid Wiki](https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5)

[Spring Boot Reference](http://docs.spring.io/spring-boot/docs/1.5.1.RELEASE/reference/html/)