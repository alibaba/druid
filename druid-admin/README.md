# Druid Admin — 集群监控管理

[English](#english) | [中文](#中文)

---

## 中文

### 简介

Druid Admin 是 Druid 的集群监控管理组件。单机部署时可以使用 Druid 内置的 `StatViewServlet` 监控页面，但在集群环境下，各节点的监控数据分散在各自的内置页面中。Druid Admin 解决了这个问题，它从集群中各个应用节点采集 Druid 监控数据并进行汇总展示。

### 支持的注册中心

Druid Admin 通过注册中心发现集群中的应用节点，支持以下注册中心（同时只能启用一种）：

| 注册中心 | 配置项 |
|---------|--------|
| **Nacos** | `spring.cloud.nacos.discovery.enabled=true` |
| **Consul** | `spring.cloud.consul.enabled=true` |
| **Eureka** | `eureka.client.enabled=true` |

### 快速开始

#### 1. 配置 application.yml

```yaml
server:
  port: 19999

spring:
  application:
    name: druid-admin
  main:
    allow-bean-definition-overriding: true
  cloud:
    nacos:
      discovery:
        enabled: true
        server-addr: localhost:8848

monitor:
  applications: app1,app2    # 需要监控的服务名（spring.application.name）
  login-username: admin       # 监控页面登录用户名
  login-password: your_password  # 监控页面登录密码
```

#### 2. 启动访问

```bash
mvn spring-boot:run -pl druid-admin
```

访问 `http://localhost:19999/druid/sql.html` 查看监控数据。

### 配置示例

#### Consul 注册中心

```yaml
spring:
  cloud:
    consul:
      enabled: true
      host: localhost
      port: 8500
      discovery:
        enable: true
        hostname: ${spring.cloud.client.ip-address}
    nacos:
      discovery:
        enabled: false
eureka:
  client:
    enabled: false
```

#### Eureka 注册中心

```yaml
eureka:
  instance:
    preferIpAddress: true
    ipAddress: localhost
    instance-id: ${eureka.instance.ipAddress}:${server.port}:${spring.application.name}
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
    enabled: true
spring:
  cloud:
    consul:
      enabled: false
    nacos:
      discovery:
        enabled: false
```

### 前提条件

被监控的应用需要：

1. 启用 `StatFilter`（`spring.datasource.druid.filter.stat.enabled=true`）
2. 启用 `StatViewServlet`（`spring.datasource.druid.stat-view-servlet.enabled=true`）
3. 在同一注册中心中注册

---

## English

### Overview

Druid Admin is a cluster monitoring component for Druid. While single-node deployments can use the built-in `StatViewServlet`, cluster environments need centralized monitoring. Druid Admin discovers application nodes via service registries and aggregates their Druid monitoring data.

### Supported Registries

| Registry | Config |
|----------|--------|
| **Nacos** | `spring.cloud.nacos.discovery.enabled=true` |
| **Consul** | `spring.cloud.consul.enabled=true` |
| **Eureka** | `eureka.client.enabled=true` |

Only one registry can be enabled at a time.

### Quick Start

```yaml
server:
  port: 19999
spring:
  application:
    name: druid-admin
  cloud:
    nacos:
      discovery:
        enabled: true
        server-addr: localhost:8848
monitor:
  applications: app1,app2
  login-username: admin
  login-password: your_password
```

Access `http://localhost:19999/druid/sql.html` to view monitoring data.

### Prerequisites

Monitored applications must:
1. Enable `StatFilter`
2. Enable `StatViewServlet`
3. Register with the same service registry
