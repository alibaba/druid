# Contributing to Druid | 贡献指南

[English](#english) | [中文](#中文)

---

## English

Thank you for your interest in contributing to Druid! This guide will help you get started.

### Code of Conduct

Please be respectful and constructive in all interactions. We are committed to providing a welcoming and inclusive experience for everyone.

### How to Contribute

#### Reporting Bugs

1. Search [existing issues](https://github.com/alibaba/druid/issues) to check if the bug has already been reported
2. If not, [create a new issue](https://github.com/alibaba/druid/issues/new) with:
   - A clear, descriptive title
   - Steps to reproduce the problem
   - Expected behavior vs actual behavior
   - Druid version, Java version, and database type
   - Relevant logs or stack traces

#### Suggesting Features

Open a [GitHub Issue](https://github.com/alibaba/druid/issues/new) describing:
- The use case and motivation
- Expected behavior
- Example code or configuration (if applicable)

#### Submitting Code Changes

1. **Fork** the repository and create your branch from `master`
2. **Write code** following our conventions (see below)
3. **Add tests** for any new functionality
4. **Run the test suite** to verify nothing is broken
5. **Submit a Pull Request** with a clear description

### Development Setup

#### Prerequisites

- Java 8+ JDK
- Apache Maven 3.6+
- Git

#### Building

```bash
git clone https://github.com/alibaba/druid.git
cd druid
mvn clean install
```

#### Running Tests

```bash
# Run all tests
mvn test

# Run tests for a specific module
mvn test -pl core

# Run a specific test class
mvn test -pl core -Dtest=com.alibaba.druid.bvt.sql.mysql.MySqlSelectTest
```

### Code Conventions

- **Language:** Java 8+ compatible
- **Style:** Follow the project's Checkstyle rules (`src/checkstyle/druid-checks.xml`)
- **Testing:** All new features must include tests in the corresponding `bvt` (Black-box Verification Test) package
- **SQL Dialect Tests:** Use the resource-driven test pattern with `.txt` files in `core/src/test/resources/bvt/parser/<dialect>/`

### Project Structure

```
core/src/main/java/com/alibaba/druid/
├── pool/            # Connection pool implementation
├── sql/
│   ├── ast/         # SQL AST node classes
│   ├── dialect/     # Dialect-specific parsers and visitors
│   ├── parser/      # Core parser infrastructure
│   └── visitor/     # AST visitor interfaces
├── filter/          # Filter-Chain implementations
├── stat/            # Monitoring and statistics
├── wall/            # SQL firewall (WallFilter)
└── util/            # Utility classes
```

### Adding a New SQL Dialect

If you want to add support for a new database dialect:

1. Create the dialect package: `sql/dialect/<name>/`
2. Implement these classes:
   - `<Name>Lexer` - Lexical analyzer with dialect-specific keywords
   - `<Name>ExprParser` - Expression parser
   - `<Name>StatementParser` - Statement parser
   - `<Name>SelectParser` - SELECT statement parser (if needed)
   - `<Name>OutputVisitor` - SQL output/generation visitor
   - `<Name>ASTVisitor` - AST visitor interface
   - `<Name>SchemaStatVisitor` - Schema statistics visitor
3. Register the dialect in `SQLParserUtils` and `SQLUtils`
4. Add dialect configuration in `META-INF/druid/parser/<name>/`:
   - `dialect.properties` - Quote characters and other settings
   - `builtin_datatypes` - Supported data types
5. Add comprehensive tests

### Pull Request Guidelines

- Keep PRs focused — one feature or fix per PR
- Include tests for new functionality
- Update documentation if behavior changes
- Write clear commit messages in English
- Reference related issues in the PR description

### Review Process

1. A maintainer will review your PR
2. Address any requested changes
3. Once approved, your PR will be merged

---

## 中文

感谢你对 Druid 项目的关注！本指南将帮助你快速参与到项目开发中。

### 行为准则

请在所有交流中保持尊重和建设性态度。

### 如何贡献

#### 报告 Bug

1. 先搜索[已有 Issue](https://github.com/alibaba/druid/issues)，确认是否已被报告
2. 如果没有，[创建新 Issue](https://github.com/alibaba/druid/issues/new)，包含：
   - 清晰的标题
   - 复现步骤
   - 期望行为与实际行为
   - Druid 版本、Java 版本、数据库类型
   - 相关日志或堆栈信息

#### 建议新功能

提交 [GitHub Issue](https://github.com/alibaba/druid/issues/new)，描述：
- 使用场景和动机
- 期望的行为
- 示例代码或配置（如适用）

#### 提交代码

1. **Fork** 仓库，基于 `master` 创建分支
2. 按照代码规范**编写代码**
3. 为新功能**添加测试**
4. **运行测试**确保没有破坏现有功能
5. **提交 Pull Request**，附上清晰的描述

### 开发环境

#### 前提条件

- Java 8+ JDK
- Apache Maven 3.6+
- Git

#### 构建

```bash
git clone https://github.com/alibaba/druid.git
cd druid
mvn clean install
```

#### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定模块的测试
mvn test -pl core

# 运行特定测试类
mvn test -pl core -Dtest=com.alibaba.druid.bvt.sql.mysql.MySqlSelectTest
```

### 代码规范

- **语言：** 兼容 Java 8+
- **风格：** 遵循项目的 Checkstyle 规则（`src/checkstyle/druid-checks.xml`）
- **测试：** 所有新功能必须在 `bvt`（黑盒验证测试）包中添加对应测试
- **SQL 方言测试：** 使用 `core/src/test/resources/bvt/parser/<dialect>/` 下的 `.txt` 资源驱动测试

### 添加新的 SQL 方言

如果要添加新的数据库方言支持：

1. 创建方言包：`sql/dialect/<name>/`
2. 实现以下类：
   - `<Name>Lexer` - 词法分析器（含方言特有关键字）
   - `<Name>ExprParser` - 表达式解析器
   - `<Name>StatementParser` - 语句解析器
   - `<Name>SelectParser` - SELECT 解析器（如需要）
   - `<Name>OutputVisitor` - SQL 输出访问者
   - `<Name>ASTVisitor` - AST 访问者接口
   - `<Name>SchemaStatVisitor` - Schema 统计访问者
3. 在 `SQLParserUtils` 和 `SQLUtils` 中注册方言
4. 在 `META-INF/druid/parser/<name>/` 添加配置：
   - `dialect.properties` - 引号字符等设置
   - `builtin_datatypes` - 支持的数据类型
5. 添加完整的测试用例

### Pull Request 规范

- 保持 PR 聚焦 — 每个 PR 只包含一个功能或修复
- 新功能必须包含测试
- 行为变更需更新文档
- 使用英文编写清晰的 commit message
- 在 PR 描述中引用相关 Issue

### 审核流程

1. 维护者将审核你的 PR
2. 根据反馈进行修改
3. 审核通过后合并
