# Druid 架构概览 | Architecture Overview

[English](#english) | [中文](#中文)

---

## 中文

### 整体架构

Druid 由四大核心子系统组成，各子系统通过清晰的 API 边界协作：

```
┌──────────────────────────────────────────────────────────┐
│                     Application Layer                     │
│               (Spring Boot / Direct JDBC)                 │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│                   DruidDataSource                         │
│            (JDBC Connection Pool Core)                    │
│  ┌─────────────┐  ┌──────────┐  ┌──────────────────┐    │
│  │ Connection   │  │PoolLock  │  │KeepAlive/Evict   │    │
│  │ Management   │  │& Fairness│  │   Threads        │    │
│  └─────────────┘  └──────────┘  └──────────────────┘    │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│                    Filter Chain                           │
│  ┌────────┐ ┌──────────┐ ┌──────────┐ ┌─────────────┐   │
│  │  Stat  │→│   Wall   │→│   Log    │→│   Custom    │   │
│  │ Filter │ │  Filter  │ │  Filter  │ │   Filter    │   │
│  └────────┘ └──────────┘ └──────────┘ └─────────────┘   │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│                   SQL Parser Engine                       │
│  ┌────────┐  ┌────────┐  ┌─────────┐  ┌─────────────┐   │
│  │ Lexer  │→ │ Parser │→ │   AST   │→ │  Visitors   │   │
│  └────────┘  └────────┘  └─────────┘  └─────────────┘   │
│  ┌─────────────────────────────────────────────────────┐ │
│  │         Dialect Registry (30 dialects)               │ │
│  └─────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
```

### 1. 连接池子系统（Connection Pool）

连接池是 Druid 最核心的组件，负责管理物理数据库连接的生命周期。

**核心类：** `DruidDataSource`

**关键机制：**

- **连接获取（getConnection）** — 从池中获取空闲连接，如果池为空则等待或创建新连接
- **连接归还（recycle）** — 连接使用完毕后归还到池中，进行有效性检查后重新入池
- **创建线程（CreateConnectionThread）** — 独立线程异步创建物理连接，避免阻塞业务线程
- **销毁线程（DestroyConnectionThread）** — 定期检测并关闭空闲超时的连接
- **KeepAlive** — 对池中空闲连接定期发送心跳，保持连接活跃

**连接状态流转：**

```
创建 → 空闲(idle) → 活跃(active) → 空闲(idle) → ... → 销毁
                ↑                      │
                └──────────────────────┘
                      recycle
```

### 2. Filter-Chain 子系统

Filter-Chain 是 Druid 的拦截器机制，采用责任链模式，在 JDBC 操作的关键节点（获取连接、执行 SQL、关闭连接等）插入自定义逻辑。

**核心接口：** `Filter`

**内置 Filter：**

| Filter | 功能 |
|--------|------|
| `StatFilter` | 采集 SQL 执行统计（执行次数、耗时、慢 SQL 等） |
| `WallFilter` | SQL 安全防护，基于 AST 分析拦截危险 SQL |
| `Slf4jLogFilter` | 通过 SLF4J 输出 SQL 执行日志 |
| `Log4jFilter` / `Log4j2Filter` | 通过 Log4j/Log4j2 输出日志 |
| `CommonsLogFilter` | 通过 Commons Logging 输出日志 |
| `ConfigFilter` | 支持配置文件加密解密 |
| `EncodingConvertFilter` | 字符编码转换 |

**执行流程：**

```
Application → Filter_1.beforeExecute()
            → Filter_2.beforeExecute()
            → ...
            → JDBC Driver (实际执行)
            → ...
            → Filter_2.afterExecute()
            → Filter_1.afterExecute()
            → Application
```

### 3. SQL 解析器子系统

SQL 解析器是 Druid 的另一核心子系统，提供将 SQL 文本解析为结构化 AST 的能力。

**处理流水线：**

```
SQL Text → Lexer(词法分析) → Token Stream → Parser(语法分析) → AST → Visitor(遍历/转换)
```

**各阶段说明：**

1. **Lexer（词法分析器）** — 将 SQL 文本分割为 Token 序列（关键字、标识符、字面量、运算符等）
2. **Parser（语法分析器）** — 按 SQL 语法规则将 Token 流组装为 AST
3. **AST（抽象语法树）** — 以树形结构表示 SQL 语句的语义，核心基类 `SQLStatement`、`SQLExpr`、`SQLTableSource`
4. **Visitor（访问者）** — 通过 Visitor 模式遍历 AST，实现 SQL 输出、Schema 分析、SQL 改写等功能

**方言注册机制：**

```
DbType → SQLParserUtils.createParser() → Dialect-specific Parser
       → SQLUtils.createOutputVisitor() → Dialect-specific Visitor
```

每种方言通过 `META-INF/druid/parser/<dialect>/` 下的配置文件注册关键字、数据类型和特性开关。

### 4. 监控统计子系统

**数据采集：** `StatFilter` 在 Filter-Chain 中拦截所有 JDBC 操作，记录：
- SQL 执行次数、总耗时、最大耗时
- 慢 SQL 日志（超过阈值的 SQL）
- 连接池状态（活跃数、空闲数、等待线程数）
- 事务统计

**数据暴露：**
- `DruidStatManagerFacade` — 编程接口，获取统计数据
- `StatViewServlet` — Web 监控页面（`/druid/index.html`）
- 自定义接口 — 通过 `DruidStatManagerFacade` 输出 JSON 对接外部监控

### 类层次关系

```
DruidDataSource                        # 连接池核心类
├── CreateConnectionThread (内部类)     # 异步创建连接线程
├── DestroyConnectionThread (内部类)    # 连接回收线程
└── LogStatsThread (内部类)             # 定期日志统计线程

DruidConnectionHolder                  # 连接持有者，包装物理连接
DruidPooledConnection                  # 池化连接，应用层使用

Filter (interface)
└── FilterAdapter                  # Filter 适配器基类
    ├── WallFilter                 # 安全防火墙 Filter
    ├── EncodingConvertFilter      # 字符编码转换 Filter
    └── FilterEventAdapter         # 事件适配器
        ├── StatFilter             # 统计 Filter
        └── LogFilter (abstract)   # 日志 Filter 基类
            ├── Slf4jLogFilter
            ├── Log4jFilter
            ├── Log4j2Filter
            └── CommonsLogFilter

SQLStatement (AST root)
├── SQLSelectStatement             # SELECT 语句
├── SQLInsertStatement             # INSERT 语句
├── SQLUpdateStatement             # UPDATE 语句
├── SQLDeleteStatement             # DELETE 语句
├── SQLCreateTableStatement        # CREATE TABLE 语句
└── ... (100+ statement types)
```

---

## English

### Overall Architecture

Druid is composed of four core subsystems that collaborate through well-defined API boundaries:

```
┌──────────────────────────────────────────────────────────┐
│                     Application Layer                     │
│               (Spring Boot / Direct JDBC)                 │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│                   DruidDataSource                         │
│            (JDBC Connection Pool Core)                    │
│  ┌─────────────┐  ┌──────────┐  ┌──────────────────┐    │
│  │ Connection   │  │PoolLock  │  │KeepAlive/Evict   │    │
│  │ Management   │  │& Fairness│  │   Threads        │    │
│  └─────────────┘  └──────────┘  └──────────────────┘    │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│                    Filter Chain                           │
│  ┌────────┐ ┌──────────┐ ┌──────────┐ ┌─────────────┐   │
│  │  Stat  │→│   Wall   │→│   Log    │→│   Custom    │   │
│  │ Filter │ │  Filter  │ │  Filter  │ │   Filter    │   │
│  └────────┘ └──────────┘ └──────────┘ └─────────────┘   │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│                   SQL Parser Engine                       │
│  ┌────────┐  ┌────────┐  ┌─────────┐  ┌─────────────┐   │
│  │ Lexer  │→ │ Parser │→ │   AST   │→ │  Visitors   │   │
│  └────────┘  └────────┘  └─────────┘  └─────────────┘   │
│  ┌─────────────────────────────────────────────────────┐ │
│  │         Dialect Registry (30 dialects)               │ │
│  └─────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
```

### 1. Connection Pool Subsystem

The connection pool is Druid's core component, managing the lifecycle of physical database connections.

**Core class:** `DruidDataSource`

**Key mechanisms:**

- **getConnection** — Acquires an idle connection from the pool; waits or creates new ones if the pool is empty
- **recycle** — Returns connections to the pool after validation
- **CreateConnectionThread** — Dedicated thread for async physical connection creation
- **DestroyConnectionThread** — Periodically detects and closes idle-timeout connections
- **KeepAlive** — Periodically sends heartbeats to idle connections

### 2. Filter-Chain Subsystem

The Filter-Chain implements the Chain of Responsibility pattern, intercepting JDBC operations at key points (connection acquisition, SQL execution, connection close, etc.).

**Core interface:** `Filter`

**Built-in Filters:**

| Filter | Purpose |
|--------|---------|
| `StatFilter` | Collects SQL execution statistics (count, duration, slow SQL) |
| `WallFilter` | SQL injection protection via AST analysis |
| `Slf4jLogFilter` | SLF4J-based SQL execution logging |
| `Log4jFilter` / `Log4j2Filter` | Log4j/Log4j2-based logging |
| `CommonsLogFilter` | Commons Logging-based logging |
| `ConfigFilter` | Configuration encryption/decryption |
| `EncodingConvertFilter` | Character encoding conversion |

### 3. SQL Parser Subsystem

The SQL parser processes SQL text into a structured AST:

```
SQL Text → Lexer → Token Stream → Parser → AST → Visitor
```

- **Lexer** — Tokenizes SQL text into keywords, identifiers, literals, operators
- **Parser** — Assembles token stream into AST following SQL grammar rules
- **AST** — Tree representation of SQL semantics (`SQLStatement`, `SQLExpr`, `SQLTableSource`)
- **Visitor** — Traverses AST for SQL output, schema analysis, SQL rewriting

### 4. Monitoring Subsystem

**Collection:** `StatFilter` intercepts all JDBC operations to record execution counts, durations, slow SQL, pool states, and transaction stats.

**Exposure:** `DruidStatManagerFacade` (programmatic API), `StatViewServlet` (Web UI at `/druid/index.html`), or custom JSON endpoints.
