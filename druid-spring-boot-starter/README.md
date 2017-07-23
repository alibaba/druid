# Druid Spring Boot Starter
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.alibaba/druid-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.alibaba/druid-spring-boot-starter/)

## 中文 | [English](https://github.com/alibaba/druid/blob/master/druid-spring-boot-starter/README_EN.md)  
Druid Spring Boot Starter 用于帮助你在Spring Boot项目中轻松集成Druid数据库连接池和监控。

## 如何使用
1. 在 Spring Boot 项目中加入```druid-spring-boot-starter```依赖。

Maven
```xml
<dependency>
   <groupId>com.alibaba</groupId>
   <artifactId>druid-spring-boot-starter</artifactId>
   <version>1.1.2</version>
</dependency>
```
Gradle
```xml
compile 'com.alibaba:druid-spring-boot-starter:1.1.2'

```
2. 添加配置
```xml
spring.datasource.url= 
spring.datasource.username=
spring.datasource.password=
```

## 配置属性
Druid Spring Boot Starter 配置属性的名称完全遵照Druid，你可以通过下面这些配置属性来配置Druid数据库连接池和监控，如果没有配置则使用默认值。

- 数据源配置
```xml
# JDBC配置
spring.datasource.druid.url= # 或spring.datasource.url= 
spring.datasource.druid.username= # 或spring.datasource.username=
spring.datasource.druid.password= # 或spring.datasource.password=
spring.datasource.druid.driver-class-name= #或 spring.datasource.driver-class-name=

# 连接池配置
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
spring.datasource.druid.filters= #配置多个英文逗号分隔
....//more
```
更多配置请参考WIKI文档，或查看 ```DruidDataSource``` 内的成员变量（提供set方法即可配置），或根据IDE提示来进行配置。

- 监控配置
```
# WebStatFilter配置，说明请参考Druid Wiki，配置_配置WebStatFilter
spring.datasource.druid.web-stat-filter.enabled= #是否启用StatFilter默认值true
spring.datasource.druid.web-stat-filter.url-pattern=
spring.datasource.druid.web-stat-filter.exclusions=
spring.datasource.druid.web-stat-filter.session-stat-enable=
spring.datasource.druid.web-stat-filter.session-stat-max-count=
spring.datasource.druid.web-stat-filter.principal-session-name=
spring.datasource.druid.web-stat-filter.principal-cookie-name=
spring.datasource.druid.web-stat-filter.profile-enable=

# StatViewServlet配置，说明请参考Druid Wiki，配置_StatViewServlet配置
spring.datasource.druid.stat-view-servlet.enabled= #是否启用StatViewServlet默认值true
spring.datasource.druid.stat-view-servlet.url-pattern=
spring.datasource.druid.stat-view-servlet.reset-enable=
spring.datasource.druid.stat-view-servlet.login-username=
spring.datasource.druid.stat-view-servlet.login-password=
spring.datasource.druid.stat-view-servlet.allow=
spring.datasource.druid.stat-view-servlet.deny=

# Spring监控配置，说明请参考Druid Github Wiki，配置_Druid和Spring关联监控配置
spring.datasource.druid.aop-patterns= # Spring监控AOP切入点，如x.y.z.service.*,配置多个英文逗号分隔
# 如果spring.datasource.druid.aop-patterns要代理的类没有定义interface请设置spring.aop.proxy-target-class=true
```
注：IDE会对所有的配置属性进行输入提示，配置文件的格式你可以选择```.properties```或```.yml```，效果是一样的。


## 如何配置多数据源
1. 添加数据源相关配置
```xml
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=

# Druid 数据源配置，继承spring.datasource.* 配置，相同则覆盖
...
spring.datasource.druid.initial-size=5
spring.datasource.druid.max-active=5
...

# Druid 数据源 1 配置，继承spring.datasource.druid.* 配置，相同则覆盖
...
spring.datasource.druid.one.max-active=10
spring.datasource.druid.one.max-wait=10000
...

# Druid 数据源 2 配置，继承spring.datasource.druid.* 配置，相同则覆盖
...
spring.datasource.druid.two.max-active=20
spring.datasource.druid.two.max-wait=20000
...
```
2. 使用```DruidDataSourceBuilder```创建数据源
```java
@Primary
@Bean
@ConfigurationProperties("spring.datasource.druid.one")
public DataSource dataSourceOne(){
    return DruidDataSourceBuilder.create().build();
}
@Bean
@ConfigurationProperties("spring.datasource.druid.two")
public DataSource dataSourceTwo(){
    return DruidDataSourceBuilder.create().build();
}
```

## 如何配置Filter
如果通过```spring.datasource.druid.filters=stat,wall,log4j...```来启用默认的Filter配置不能满足你的需要，你可以通过配置文件来配置Filter，下面是例子。
```xml
# 自定义StatFilter 配置
spring.datasource.druid.filter.stat.db-type=h2
spring.datasource.druid.filter.stat.log-slow-sql=true
spring.datasource.druid.filter.stat.slow-sql-millis=2000

# 自定义WallFilter 配置,其他 Filter 不再演示
spring.datasource.druid.filter.wall.enabled=true
spring.datasource.druid.filter.wall.db-type=h2
spring.datasource.druid.filter.wall.config.delete-allow=false
spring.datasource.druid.filter.wall.config.drop-table-allow=false
```
目前为以下Filter 提供配置支持，请参考文档或者IDE提示进行配置
- StatFilter
- ConfigFilter
- EncodingConvertFilter
- Slf4jLogFilter
- Log4jFilter
- Log4j2Filter
- CommonsLogFilter
- WallFilter

默认会启用 StatFilter，你也可以将其```enabled```设置为```false```关闭，其他Filter配置生效需将对应的```enabled```设置为```true```。

## 演示
克隆项目，运行```test```包内的```DemoApplication```。

## 参考
[Druid Wiki](https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5)

[Spring Boot Reference](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)