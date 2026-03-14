# CLAUDE.md — Druid Project Context

## Project Overview

Druid is Alibaba's open-source high-performance JDBC connection pool and SQL parser for Java. Version: 1.2.29-SNAPSHOT.

## Build & Test

```bash
mvn clean install              # Full build with tests
mvn test -pl core              # Core module tests only
mvn test -pl core -Dtest=<TestClass>  # Single test class
```

**Requirements:** Java 8+ JDK, Apache Maven 3.6+

## Project Structure

- `core/` — Main library: connection pool, SQL parser, filter chain, wall security, monitoring
- `druid-spring-boot-starter/` — Spring Boot 2.x auto-configuration
- `druid-spring-boot-3-starter/` — Spring Boot 3.x auto-configuration
- `druid-spring-boot-4-starter/` — Spring Boot 4.x auto-configuration
- `druid-admin/` — Cluster monitoring admin
- `druid-wrapper/` — Wrapper utilities
- `openspec/` — Architecture specs and change documentation

## Key Source Paths

- `core/src/main/java/com/alibaba/druid/pool/` — Connection pool (`DruidDataSource`)
- `core/src/main/java/com/alibaba/druid/sql/dialect/` — 31 SQL dialect parsers
- `core/src/main/java/com/alibaba/druid/sql/parser/` — Core parser infrastructure
- `core/src/main/java/com/alibaba/druid/sql/ast/` — AST node classes
- `core/src/main/java/com/alibaba/druid/filter/` — Filter chain implementations
- `core/src/main/java/com/alibaba/druid/wall/` — SQL firewall (WallFilter)
- `core/src/main/java/com/alibaba/druid/stat/` — Monitoring statistics

## Testing Conventions

- Tests are in `core/src/test/java/com/alibaba/druid/bvt/` (Black-box Verification Test)
- SQL dialect tests use resource files: `core/src/test/resources/bvt/parser/<dialect>/*.txt`
- Testing framework: JUnit 5 (migrated from JUnit 4)
- Surefire pattern: `**/bvt/**/*.java`

## SQL Dialect Pattern

Each dialect in `sql/dialect/<name>/` follows this structure:
- `<Name>Lexer` — Lexical analyzer
- `<Name>ExprParser` — Expression parser
- `<Name>StatementParser` — Statement parser
- `<Name>OutputVisitor` — SQL output visitor
- `<Name>ASTVisitor` — AST visitor interface
- `<Name>SchemaStatVisitor` — Schema statistics visitor
- Config: `META-INF/druid/parser/<name>/dialect.properties` and `builtin_datatypes`

## Code Style

- Java 8 compatible (source level 8)
- Checkstyle enforced: `src/checkstyle/druid-checks.xml`
- Visitor pattern for AST traversal
- Filter-Chain (Chain of Responsibility) for JDBC interception
