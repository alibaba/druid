druid-spring-boot-start
=======================
Spring boot with druid support。

### 如何使用
在Spring Boot项目的pom.xml中添加以下依赖:

```xml
      <dependency>
          <groupId>com.alibaba.boot</groupId>
          <artifactId>druid-spring-boot-starter</artifactId>
          <version>1.0.0-SNAPSHOT</version>
      </dependency>
```
Druid的连接池配置采用Spring Boot标准的数据库连接池配置方式，配置按照标准设置即可。

```properties                    
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/druid
spring.datasource.username=root
spring.datasource.password=123456
 ```

如果你想设置druid的具体参数，请参考 spring.datasource.druid.* 下的具体配置项。


### Todo

* stat view servlet调整到management port上，这样可以解决安全问题
* stat拦截中未考虑SEO的情况，如果在SEO的情况下，会导致URL数量暴增，如aliexpress的商品详情URL

### 参考文档

* Spring Boot多数据源：http://docs.spring.io/spring-boot/docs/current/reference/html/howto-data-access.html
* Druid Wiki: https://github.com/alibaba/druid/wiki