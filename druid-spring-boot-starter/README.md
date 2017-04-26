# Druid Spring Boot Starter
### 简介

使用Spring Boot构建应用已成为Java开发者的首选，Spring 官方提供众多Starter供开发者使用，以便加快应用构建速度。
Druid作为最好的数据库连接池并未获得Spring 官方的默认支持(目前Spring Boot中默认支持的连接池有dbcp,dbcp2, tomcat, hikari三种连接池)导致Druid连接池配置并不能生效，故开发Druid Spring Boot Starter供使用，使在Spring Boot中使用Druid数据库连接池和监控更为简单。

### 如何使用
(1) ```git clone``` (2) ```mvn:install``` (3) 引入```Maven```依赖 (4) 按需配置```application.properties```，使用默认配置忽略此步

注：待上传中央仓库提供\<dependency/\>
### 配置项
Druid Spring Boot Starter 配置项参数名称是完全遵照Druid的，默认值也是Druid的官方默认值，如果你进行了配置那么默认值将被覆盖。
```
# 数据源基础配置，参考Spring Boot文档该部分
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=
# Druid配置
spring.datasource.druid.StatViewServlet.enabled= #是否启用StatViewServlet默认值true
spring.datasource.druid.StatFilter.enabled= #是否启用StatFilter默认值true
spring.datasource.druid.filters= #默认值为stat，配置多个请以英文逗号分隔，如stat,wall,log4j
spring.datasource.druid.aop-patterns= #配置AOP切入点来启用Spring监控，配置多个以英文逗号分隔，如x.y.z.dao.*,x.y.z.service.*
#下面配置项说明请参考Druid Wiki，配置_DruidDataSource参考配置
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
#下面配置项说明请参考Druid Wiki，配置_配置WebStatFilter
spring.datasource.druid.StatFilter.urlPattern=
spring.datasource.druid.StatFilter.exclusions=
spring.datasource.druid.StatFilter.sessionStatMaxCount=
spring.datasource.druid.StatFilter.sessionStatEnable=
spring.datasource.druid.StatFilter.principalSessionName=
spring.datasource.druid.StatFilter.principalCookieName=
spring.datasource.druid.StatFilter.profileEnable=
#下面配置项说明请参考Druid Wiki，配置_StatViewServlet配置
spring.datasource.druid.StatViewServlet.urlPattern=
spring.datasource.druid.StatViewServlet.resetEnable=
spring.datasource.druid.StatViewServlet.loginUsername=
spring.datasource.druid.StatViewServlet.loginPassword=
spring.datasource.druid.StatViewServlet.allow=
spring.datasource.druid.StatViewServlet.deny=

#如果spring.datasource.druid.aop-patterns要代理的类没有定义interface请设置spring.aop.proxy-target-class=true

```
### 演示
克隆项目，运行```test```包内的```Application.java```
### 参考
[Druid Wiki](https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5)

[Spring Boot Reference](http://docs.spring.io/spring-boot/docs/1.5.1.RELEASE/reference/html/)


