# Druid Spring Boot 3 Starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.alibaba/druid-spring-boot-3-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.alibaba/druid-spring-boot-3-starter/)

## 中文 | [English](#english)

Druid Spring Boot 3 Starter 用于在 Spring Boot 3.x 项目中轻松集成 Druid 数据库连接池和监控。

**要求：** Spring Boot 3.0+，Java 17+

> 如果 Spring Boot 版本 < 3.0.0，请使用 [druid-spring-boot-starter](../druid-spring-boot-starter/README.md)。
> 如果 Spring Boot 版本 >= 4.0.0，请使用 [druid-spring-boot-4-starter](../druid-spring-boot-4-starter/README.md)。

## 如何使用

### 1. 添加依赖

**Maven**

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-3-starter</artifactId>
    <version>1.2.24</version>
</dependency>
```

**Gradle**

```groovy
implementation 'com.alibaba:druid-spring-boot-3-starter:1.2.24'
```

### 2. 添加配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    druid:
      # 连接池配置
      initial-size: 5
      max-active: 20
      min-idle: 5
      max-wait: 60000

      # 连接检测
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false

      # 监控 Filter
      filter:
        stat:
          enabled: true
          log-slow-sql: true
          slow-sql-millis: 2000
        wall:
          enabled: true

      # Web 监控页面
      stat-view-servlet:
        enabled: true
        login-username: admin
        login-password: your_password

      # Web 请求统计
      web-stat-filter:
        enabled: true
        url-pattern: /*
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"
```

### 3. 完整配置参考

完整配置与 `druid-spring-boot-starter` 一致，请参考 [Spring Boot Starter 配置文档](../druid-spring-boot-starter/README.md)。

### Spring Boot 3.x 注意事项

- Spring Boot 3.x 基于 Spring Framework 6.x 和 Jakarta EE 9+
- Servlet 相关类已迁移到 `jakarta.servlet` 包
- 最低 Java 版本要求为 Java 17

---

## English

Druid Spring Boot 3 Starter simplifies Druid database connection pool integration in Spring Boot 3.x projects.

**Requirements:** Spring Boot 3.0+, Java 17+

> For Spring Boot < 3.0.0, use [druid-spring-boot-starter](../druid-spring-boot-starter/README.md).
> For Spring Boot >= 4.0.0, use [druid-spring-boot-4-starter](../druid-spring-boot-4-starter/README.md).

### Usage

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-3-starter</artifactId>
    <version>1.2.24</version>
</dependency>
```

Configuration properties are identical to `druid-spring-boot-starter`. See [Spring Boot Starter docs](../druid-spring-boot-starter/README_EN.md) for full reference.
