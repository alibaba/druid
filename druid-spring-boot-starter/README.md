# Druid Spring Boot Starter
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.alibaba/druid-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.alibaba/druid-spring-boot-starter/)

## 中文 | [English](https://github.com/alibaba/druid/blob/master/druid-spring-boot-starter/README_EN.md)  
Druid Spring Boot Starter 用于帮助你在Spring Boot项目中轻松集成Druid数据库连接池和监控。

## 如何使用
1. 在 Spring Boot 项目中加入```druid-spring-boot-starter```依赖

    ```Maven```
    ```xml
    <dependency>
       <groupId>com.alibaba</groupId>
       <artifactId>druid-spring-boot-starter</artifactId>
       <version>1.1.10</version>
    </dependency>
    ```
    ```Gradle```
    ```xml
    compile 'com.alibaba:druid-spring-boot-starter:1.1.10'
    
    ```
2. 添加配置
    ```xml
    spring.datasource.url= 
    spring.datasource.username=
    spring.datasource.password=
    # ...其他配置（可选，不是必须的，使用内嵌数据库的话上述三项也可省略不填）
    ```

## 配置属性
Druid Spring Boot Starter 配置属性的名称完全遵照 Druid，你可以通过 Spring Boot 配置文件来配置Druid数据库连接池和监控，如果没有配置则使用默认值。

- JDBC 配置
```xml
spring.datasource.druid.url= # 或spring.datasource.url= 
spring.datasource.druid.username= # 或spring.datasource.username=
spring.datasource.druid.password= # 或spring.datasource.password=
spring.datasource.druid.driver-class-name= #或 spring.datasource.driver-class-name=
```
- 连接池配置
```
spring.datasource.druid.initial-size=
spring.datasource.druid.max-active=
spring.datasource.druid.min-idle=
spring.datasource.druid.max-wait=
spring.datasource.druid.pool-prepared-statements=
spring.datasource.druid.max-pool-prepared-statement-per-connection-size= 
spring.datasource.druid.max-open-prepared-statements= #和上面的等价
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
```
Druid Spring Boot Starter 不仅限于对以上配置属性提供支持，[```DruidDataSource```](https://github.com/alibaba/druid/blob/master/src/main/java/com/alibaba/druid/pool/DruidDataSource.java) 内提供```setter```方法的可配置属性都将被支持。你可以参考WIKI文档或通过IDE输入提示来进行配置。配置文件的格式你可以选择```.properties```或```.yml```，效果是一样的，在配置较多的情况下推荐使用```.yml```。



## 如何配置多数据源
1. 添加配置
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
**强烈注意**：Spring Boot 2.X 版本不再支持配置继承，多数据源的话每个数据源的所有配置都需要单独配置，否则配置不会生效

2. 创建数据源
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

## 如何配置 Filter
你可以通过 ```spring.datasource.druid.filters=stat,wall,log4j ...``` 的方式来启用相应的内置Filter，不过这些Filter都是默认配置。如果默认配置不能满足你的需求，你可以放弃这种方式，通过配置文件来配置Filter，下面是例子。
```xml
# 配置StatFilter 
spring.datasource.druid.filter.stat.db-type=h2
spring.datasource.druid.filter.stat.log-slow-sql=true
spring.datasource.druid.filter.stat.slow-sql-millis=2000

# 配置WallFilter 
spring.datasource.druid.filter.wall.enabled=true
spring.datasource.druid.filter.wall.db-type=h2
spring.datasource.druid.filter.wall.config.delete-allow=false
spring.datasource.druid.filter.wall.config.drop-table-allow=false

# 其他 Filter 配置不再演示
```
目前为以下 Filter 提供了配置支持，请参考文档或者根据IDE提示（```spring.datasource.druid.filter.*```）进行配置。
- StatFilter
- WallFilter
- ConfigFilter
- EncodingConvertFilter
- Slf4jLogFilter
- Log4jFilter
- Log4j2Filter
- CommonsLogFilter

要想使自定义 Filter 配置生效需要将对应 Filter 的 ```enabled``` 设置为 ```true``` ，Druid Spring Boot Starter 默认会启用 StatFilter，你也可以将其 ```enabled``` 设置为 ```false``` 来禁用它。

## 如何获取 Druid 的监控数据

Druid 的监控数据可以通过 DruidStatManagerFacade 进行获取，获取到监控数据之后你可以将其暴露给你的监控系统进行使用。Druid 默认的监控系统数据也来源于此。下面给做一个简单的演示，在 Spring Boot 中如何通过 HTTP 接口将 Druid 监控数据以 JSON 的形式暴露出去，实际使用中你可以根据你的需要自由地对监控数据、暴露方式进行扩展。

```java
@RestController
public class DruidStatController {
    @GetMapping("/druid/stat")
    public Object druidStat(){
        // DruidStatManagerFacade#getDataSourceStatDataList 该方法可以获取所有数据源的监控数据，除此之外 DruidStatManagerFacade 还提供了一些其他方法，你可以按需选择使用。
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}
```

```json
[
  {
    "Identity": 1583082378,
    "Name": "DataSource-1583082378",
    "DbType": "h2",
    "DriverClassName": "org.h2.Driver",
    "URL": "jdbc:h2:file:./demo-db",
    "UserName": "sa",
    "FilterClassNames": [
      "com.alibaba.druid.filter.stat.StatFilter"
    ],
    "WaitThreadCount": 0,
    "NotEmptyWaitCount": 0,
    "NotEmptyWaitMillis": 0,
    "PoolingCount": 2,
    "PoolingPeak": 2,
    "PoolingPeakTime": 1533782955104,
    "ActiveCount": 0,
    "ActivePeak": 1,
    "ActivePeakTime": 1533782955178,
    "InitialSize": 2,
    "MinIdle": 2,
    "MaxActive": 30,
    "QueryTimeout": 0,
    "TransactionQueryTimeout": 0,
    "LoginTimeout": 0,
    "ValidConnectionCheckerClassName": null,
    "ExceptionSorterClassName": null,
    "TestOnBorrow": true,
    "TestOnReturn": true,
    "TestWhileIdle": true,
    "DefaultAutoCommit": true,
    "DefaultReadOnly": null,
    "DefaultTransactionIsolation": null,
    "LogicConnectCount": 103,
    "LogicCloseCount": 103,
    "LogicConnectErrorCount": 0,
    "PhysicalConnectCount": 2,
    "PhysicalCloseCount": 0,
    "PhysicalConnectErrorCount": 0,
    "ExecuteCount": 102,
    "ErrorCount": 0,
    "CommitCount": 100,
    "RollbackCount": 0,
    "PSCacheAccessCount": 100,
    "PSCacheHitCount": 99,
    "PSCacheMissCount": 1,
    "StartTransactionCount": 100,
    "TransactionHistogram": [
      55,
      44,
      1,
      0,
      0,
      0,
      0
    ],
    "ConnectionHoldTimeHistogram": [
      53,
      47,
      3,
      0,
      0,
      0,
      0,
      0
    ],
    "RemoveAbandoned": false,
    "ClobOpenCount": 0,
    "BlobOpenCount": 0,
    "KeepAliveCheckCount": 0,
    "KeepAlive": false,
    "FailFast": false,
    "MaxWait": 1234,
    "MaxWaitThreadCount": -1,
    "PoolPreparedStatements": true,
    "MaxPoolPreparedStatementPerConnectionSize": 5,
    "MinEvictableIdleTimeMillis": 30001,
    "MaxEvictableIdleTimeMillis": 25200000,
    "LogDifferentThread": true,
    "RecycleErrorCount": 0,
    "PreparedStatementOpenCount": 1,
    "PreparedStatementClosedCount": 0,
    "UseUnfairLock": true,
    "InitGlobalVariants": false,
    "InitVariants": false
  }
]
```

## IDE 提示支持
![](https://raw.githubusercontent.com/lihengming/java-codes/master/shared-resources/github-images/druid-spring-boot-starter-ide-hint.jpg)

## 演示
克隆项目，运行```test```包内的```DemoApplication```。

## 参考
[Druid Wiki](https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5)

[Spring Boot Reference](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)