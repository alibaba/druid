# Druid Spring Boot Starter
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.alibaba/druid-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.alibaba/druid-spring-boot-starter/)

## English | [中文](https://github.com/alibaba/druid/blob/master/druid-spring-boot-starter/README.md)
Spring Boot with Druid support, help you simplify Druid config in Spring Boot.

## Usage
1. Add the ```druid-spring-boot-starter``` dependency in Spring Boot project.

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
2. Add configuration properties.
    ```xml
    spring.datasource.url= 
    spring.datasource.username=
    spring.datasource.password=
    # ...other config (optional, use the embedded database, then the above three items can also be omitted)
    ```
## Configuration Properties
Druid Spring Boot Starter properties name in full compliance with Druid configuration, you can configure the Druid database connection pool and monitor the configuration properties through the following configuration properties, using default values if not configured.
- JDBC
```xml
spring.datasource.druid.url= # or spring.datasource.url= 
spring.datasource.druid.username= # or spring.datasource.username=
spring.datasource.druid.password= # or spring.datasource.password=
spring.datasource.druid.driver-class-name= # or spring.datasource.driver-class-name=
```

- Connection pool 
```
spring.datasource.druid.initial-size=
spring.datasource.druid.max-active=
spring.datasource.druid.min-idle=
spring.datasource.druid.max-wait=
spring.datasource.druid.pool-prepared-statements=
spring.datasource.druid.max-pool-prepared-statement-per-connection-size= 
spring.datasource.druid.max-open-prepared-statements= #Equivalent to the above 'max-pool-prepared-statement-per-connection-size'
spring.datasource.druid.validation-query=
spring.datasource.druid.validation-query-timeout=
spring.datasource.druid.test-on-borrow=
spring.datasource.druid.test-on-return=
spring.datasource.druid.test-while-idle=
spring.datasource.druid.time-between-eviction-runs-millis=
spring.datasource.druid.min-evictable-idle-time-millis=
spring.datasource.druid.max-evictable-idle-time-millis=
spring.datasource.druid.filters= #Druid filters, default value stat, multiple separated by comma.
```
- Monitor
```
# WebStatFilter properties, detail see Druid Wiki
spring.datasource.druid.web-stat-filter.enabled= #Enable StatFilter, default value true.
spring.datasource.druid.web-stat-filter.url-pattern=
spring.datasource.druid.web-stat-filter.exclusions=
spring.datasource.druid.web-stat-filter.session-stat-enable=
spring.datasource.druid.web-stat-filter.session-stat-max-count=
spring.datasource.druid.web-stat-filter.principal-session-name=
spring.datasource.druid.web-stat-filter.principal-cookie-name=
spring.datasource.druid.web-stat-filter.profile-enable=

# StatViewServlet properties, detail see Druid Wiki
spring.datasource.druid.stat-view-servlet.enabled= #Enable StatViewServlet, default value true.
spring.datasource.druid.stat-view-servlet.url-pattern=
spring.datasource.druid.stat-view-servlet.reset-enable=
spring.datasource.druid.stat-view-servlet.login-username=
spring.datasource.druid.stat-view-servlet.login-password=
spring.datasource.druid.stat-view-servlet.allow=
spring.datasource.druid.stat-view-servlet.deny=

# With Spring monitoring properties, detail see Druid Wiki
spring.datasource.druid.aop-patterns= # Spring monitoring AOP point, such as x.y.z.service.*, multiple separated by comma.
```
The Druid Spring Boot Starter is not limited to support for the above configuration properties, and the configurable properties that provide the ```setter``` method in [``` DruidDataSource```](https://github.com/alibaba/druid/blob/master/src/main/java/com/alibaba/druid/pool/DruidDataSource.java) will be supported. You can refer to the WIKI document or configure it via the IDE input prompt. The format of the configuration file You can choose ```.properties``` or``` .yml```, the effect is the same, in the configuration of more cases recommend the use of ```.yml```.

## How to Configuration Multiple DataSource
1. Add config
```xml
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=

# DruidDataSurce configuration, extents spring.datasource. * configuration,,  the same will be replaced.
spring.datasource.druid.initial-size=5
spring.datasource.druid.max-active=5
...

# First DruidDataSurce configuration，extents spring.datasource.druid.* configuration, the same will be replaced.
...
spring.datasource.druid.one.max-active=10
spring.datasource.druid.one.max-wait=10000
...

# Second DruidDataSurce configuration，extents spring.datasource.druid.* configuration, the same will be replaced.
...
spring.datasource.druid.two.max-active=20
spring.datasource.druid.two.max-wait=20000
...
```
Warning: Spring Boot 2.X not support extents, please configure one by one. 

2. Create DruidDataSource
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

## How to Configuration  Filter
You can ```spring.datasource.druid.filters = stat, wall, log4j, dtc.``` way to enable the corresponding built-in Filter, but these are the default configuration Filter. If the default configuration can not meet your needs, you can give up this way, through the configuration file to configure the Filter, the following is an example.
```xml
# StatFilter configuration example.
spring.datasource.druid.filter.stat.db-type=h2
spring.datasource.druid.filter.stat.log-slow-sql=true
spring.datasource.druid.filter.stat.slow-sql-millis=2000

# WallFilter configuration example
spring.datasource.druid.filter.wall.enabled=true
spring.datasource.druid.filter.wall.db-type=h2
spring.datasource.druid.filter.wall.config.delete-allow=false
spring.datasource.druid.filter.wall.config.drop-table-allow=false

# Other Filter similar.
```
Currently, configuration support is provided for the following filters. Please refer to the documentation or configure it according to the IDE prompt (```spring.datasource.druid.filter. *```).
- StatFilter
- WallFilter
- ConfigFilter
- EncodingConvertFilter
- Slf4jLogFilter
- Log4jFilter
- Log4j2Filter
- CommonsLogFilter

Druid Spring Boot Starter will enable StatFilter by default, and you can also set its enabled to false.，make the Filter configuration take effect and need to set enabled to true.

## How to get Druid monitoring(stat) data

Druid's monitoring data can be obtained through DruidStatManagerFacade. After obtaining the monitoring data, you can expose it to your monitoring system for use. Druid's default monitoring system data also comes from this. Let's take a simple demonstration. In Spring Boot, how to expose Druid monitoring data in the form of JSON through the HTTP interface. In actual use, you can freely expand the monitoring data and exposure methods according to your needs.
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

## IDE Hints
![](https://raw.githubusercontent.com/lihengming/java-codes/master/shared-resources/github-images/druid-spring-boot-starter-ide-hint.jpg)

## Samples
Clone the project, run ```DemoApplication``` within the ```test``` package.

## Reference
- [Druid Wiki](https://github.com/alibaba/druid/wiki)

- [Spring Boot Reference](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)