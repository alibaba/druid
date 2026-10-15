# Druid Spring Boot 4 Starter

## 中文 | [English](#english)

Druid Spring Boot 4 Starter 用于在 Spring Boot 4.x 项目中轻松集成 Druid 数据库连接池和监控。

**要求：** Spring Boot 4.0+，Java 17+

> **注意：** Spring Boot 4.x 目前处于预发布阶段（Milestone），本 Starter 为实验性支持。
>
> 如果 Spring Boot 版本 < 3.0.0，请使用 [druid-spring-boot-starter](../druid-spring-boot-starter/README.md)。
> 如果 Spring Boot 版本 3.x，请使用 [druid-spring-boot-3-starter](../druid-spring-boot-3-starter/README.md)。

## 如何使用

### 1. 添加依赖

**Maven**

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-4-starter</artifactId>
    <version>1.2.24</version>
</dependency>
```

**Gradle**

```groovy
implementation 'com.alibaba:druid-spring-boot-4-starter:1.2.24'
```

### 2. 配置

配置方式与 `druid-spring-boot-3-starter` 一致。详细配置请参考 [Spring Boot Starter 配置文档](../druid-spring-boot-starter/README.md)。

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    druid:
      initial-size: 5
      max-active: 20
      min-idle: 5
      max-wait: 60000
      validation-query: SELECT 1
      test-while-idle: true
      filter:
        stat:
          enabled: true
        wall:
          enabled: true
      stat-view-servlet:
        enabled: true
        login-username: admin
        login-password: your_password
```

---

## English

Druid Spring Boot 4 Starter simplifies Druid database connection pool integration in Spring Boot 4.x projects.

**Requirements:** Spring Boot 4.0+, Java 17+

> **Note:** Spring Boot 4.x is currently in pre-release (Milestone). This starter provides experimental support.

### Usage

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-4-starter</artifactId>
    <version>1.2.24</version>
</dependency>
```

Configuration is identical to `druid-spring-boot-3-starter`. See [Spring Boot Starter docs](../druid-spring-boot-starter/README.md) for full reference.
