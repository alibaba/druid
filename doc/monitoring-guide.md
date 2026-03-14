# 监控统计指南 | Monitoring & Statistics Guide

[English](#english) | [中文](#中文)

---

## 中文

Druid 内置了强大的监控统计功能，可以实时采集 SQL 执行统计、连接池状态和 Web 请求信息，并通过 Web 页面或 API 暴露。

### 启用监控

#### 1. 启用 StatFilter

StatFilter 是监控数据的采集源头：

```yaml
spring:
  datasource:
    druid:
      filter:
        stat:
          enabled: true
          log-slow-sql: true        # 记录慢 SQL
          slow-sql-millis: 2000     # 慢 SQL 阈值（毫秒）
          merge-sql: true           # 合并相同结构 SQL 的统计
```

#### 2. 启用 Web 监控页面

```yaml
spring:
  datasource:
    druid:
      stat-view-servlet:
        enabled: true                    # 启用监控页面
        url-pattern: /druid/*            # 访问路径
        login-username: admin            # 登录用户名
        login-password: your_password    # 登录密码
        allow: 127.0.0.1                 # IP 白名单（逗号分隔）
        deny:                            # IP 黑名单
        reset-enable: false              # 是否允许重置统计数据
```

启用后访问 `http://your-host/druid/index.html` 即可查看监控页面。

#### 3. 启用 Web 请求统计

```yaml
spring:
  datasource:
    druid:
      web-stat-filter:
        enabled: true
        url-pattern: /*
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"
        session-stat-enable: true
        session-stat-max-count: 1000
        profile-enable: true             # 启用 profile
```

### 监控数据说明

#### 数据源统计

| 指标 | 说明 |
|------|------|
| `PoolingCount` | 当前连接池中空闲连接数 |
| `PoolingPeak` | 空闲连接数历史峰值 |
| `ActiveCount` | 当前活跃连接数 |
| `ActivePeak` | 活跃连接数历史峰值 |
| `WaitThreadCount` | 当前等待获取连接的线程数 |
| `NotEmptyWaitCount` | 累计等待次数 |
| `NotEmptyWaitMillis` | 累计等待时间 |
| `LogicConnectCount` | 累计逻辑连接获取次数 |
| `LogicCloseCount` | 累计逻辑连接关闭次数 |
| `PhysicalConnectCount` | 累计物理连接创建次数 |
| `PhysicalCloseCount` | 累计物理连接关闭次数 |
| `ExecuteCount` | 累计 SQL 执行次数 |
| `ErrorCount` | 累计执行错误次数 |
| `CommitCount` | 累计事务提交次数 |
| `RollbackCount` | 累计事务回滚次数 |

#### SQL 统计

| 指标 | 说明 |
|------|------|
| `ExecuteCount` | 该 SQL 执行次数 |
| `TotalTime` | 总执行时间 |
| `MaxTimespan` | 最大执行时间 |
| `InTransactionCount` | 事务内执行次数 |
| `ErrorCount` | 执行错误次数 |
| `EffectedRowCount` | 影响行数 |
| `FetchRowCount` | 返回行数 |
| `RunningCount` | 当前正在执行的数量 |
| `ConcurrentMax` | 最大并发执行数 |

### 编程方式获取统计数据

通过 `DruidStatManagerFacade` 可编程获取所有监控数据：

```java
import com.alibaba.druid.stat.DruidStatManagerFacade;

// 获取所有数据源统计
List<Map<String, Object>> dataSourceStats =
    DruidStatManagerFacade.getInstance().getDataSourceStatDataList();

// 获取 SQL 统计
List<Map<String, Object>> sqlStats =
    DruidStatManagerFacade.getInstance().getSqlStatDataList(null);

// 获取活跃连接堆栈
List<Map<String, Object>> activeConnections =
    DruidStatManagerFacade.getInstance().getActiveConnStackTraceList();
```

#### 暴露为 REST API

```java
@RestController
@RequestMapping("/monitor")
public class DruidMonitorController {

    @GetMapping("/datasource")
    public Object dataSourceStat() {
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }

    @GetMapping("/sql")
    public Object sqlStat() {
        return DruidStatManagerFacade.getInstance().getSqlStatDataList(null);
    }

    @GetMapping("/wall")
    public Object wallStat() {
        return DruidStatManagerFacade.getInstance().getWallStatMap();
    }
}
```

### 与外部监控系统集成

#### Prometheus + Grafana

通过定期获取 `DruidStatManagerFacade` 数据并暴露为 Prometheus 指标：

```java
@Component
public class DruidMetricsExporter {

    @Scheduled(fixedRate = 15000)
    public void exportMetrics() {
        List<Map<String, Object>> stats =
            DruidStatManagerFacade.getInstance().getDataSourceStatDataList();

        for (Map<String, Object> stat : stats) {
            // 将统计数据转化为 Prometheus 指标
            int activeCount = (Integer) stat.get("ActiveCount");
            int poolingCount = (Integer) stat.get("PoolingCount");
            // ... 输出到 Prometheus
        }
    }
}
```

### Spring 监控

Druid 可以监控 Spring Bean 的方法执行情况：

```yaml
spring:
  datasource:
    druid:
      aop-patterns: com.yourapp.service.*,com.yourapp.dao.*
```

### 安全建议

1. **务必设置登录密码** — 监控页面包含敏感信息
2. **配置 IP 白名单** — 限制监控页面访问来源
3. **生产环境禁用 reset** — 设置 `reset-enable: false`
4. **定期清理统计** — 长期运行后统计数据会积累，注意内存占用

---

## English

### Overview

Druid provides built-in monitoring and statistics capabilities that collect SQL execution stats, connection pool states, and web request information in real-time.

### Quick Setup

```yaml
spring:
  datasource:
    druid:
      filter:
        stat:
          enabled: true
          log-slow-sql: true
          slow-sql-millis: 2000
      stat-view-servlet:
        enabled: true
        login-username: admin
        login-password: your_password
      web-stat-filter:
        enabled: true
        url-pattern: /*
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"
```

Access the monitoring console at `http://your-host/druid/index.html`.

### Programmatic Access

```java
// Get datasource statistics
DruidStatManagerFacade.getInstance().getDataSourceStatDataList();

// Get SQL statistics
DruidStatManagerFacade.getInstance().getSqlStatDataList(null);

// Get active connection stack traces
DruidStatManagerFacade.getInstance().getActiveConnStackTraceList();
```

### Key Metrics

- **Connection Pool:** PoolingCount, ActiveCount, WaitThreadCount, PhysicalConnectCount
- **SQL Execution:** ExecuteCount, TotalTime, MaxTimespan, ErrorCount, SlowSQL count
- **Transactions:** CommitCount, RollbackCount, StartTransactionCount

### Security

- Always set login credentials for the monitoring console
- Configure IP whitelist (`allow` property)
- Disable reset in production (`reset-enable: false`)
