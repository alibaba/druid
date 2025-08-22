# Qwen Code Context for Druid Project

This document provides essential context for Qwen Code to understand and assist with the Druid project.

## Project Overview

This is a **Java** project. Druid is a high-performance, scalable JDBC connection pool implementation for Java applications. It's designed to provide efficient database connection management and includes features for monitoring and statistics.

Key aspects:
- **Main Technology:** Java, built with Maven.
- **Core Functionality:** JDBC Connection Pooling (`DruidDataSource`).
- **Additional Features:** SQL monitoring, statistics, and security features like SQL firewall (`WallFilter`).
- **Integration:** Provides Spring Boot starters for easy integration (`druid-spring-boot-starter`, `druid-spring-boot-3-starter`).

## Project Structure

- `pom.xml`: Root Maven project file. Defines parent POM, manages modules, dependencies, and build profiles.
- `core/`: Contains the main Druid library source code.
    - `core/pom.xml`: Maven configuration for the core library.
    - `core/src/main/java/com/alibaba/druid/`: Main source code for the Druid library.
        - `pool/`: Core connection pooling logic (e.g., `DruidDataSource`).
        - `sql/`: SQL parsing and analysis.
        - `stat/`: Statistics collection and management.
        - `wall/`: SQL firewall/filtering logic.
        - `filter/`: Extensible filter mechanism for connections/statements.
- `druid-spring-boot-starter/`: Spring Boot Starter for integrating Druid into Spring Boot 2.x applications.
- `druid-spring-boot-3-starter/`: Spring Boot Starter for integrating Druid into Spring Boot 3.x applications.
- `druid-demo-petclinic/`: A demo application, likely based on Spring PetClinic, showcasing Druid usage.
- `doc/`: Documentation files.
- `README.md`: General project introduction and quick start guide.

## Building and Running

- **Prerequisites:** Java 8+ JDK, Apache Maven.
- **Build Command:** `mvn clean install` (from the project root). This compiles the code, runs tests, and packages the artifacts (JARs).
- **Run Demo:** Navigate to `druid-demo-petclinic` and follow its specific instructions (likely `mvn spring-boot:run`).

## Development Conventions

- **Build Tool:** Apache Maven is used for dependency management and building.
- **Language:** Java.
- **Testing:** Unit and integration tests are likely located within `src/test` directories of respective modules (e.g., `core/src/test`). The `pom.xml` configures the `maven-surefire-plugin` to run tests.
- **Code Style:** Checkstyle is configured (see `pom.xml` and `src/checkstyle/druid-checks.xml`) to enforce coding standards.
- **Modularization:** The project is a multi-module Maven project, separating the core library from Spring Boot integration modules.
- **Versioning:** Version information is managed in the root `pom.xml` and reflected in `core/src/main/java/com/alibaba/druid/VERSION.java`.