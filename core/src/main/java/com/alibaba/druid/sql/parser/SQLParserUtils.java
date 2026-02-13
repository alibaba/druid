/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.athena.parser.AthenaExprParser;
import com.alibaba.druid.sql.dialect.athena.parser.AthenaLexer;
import com.alibaba.druid.sql.dialect.athena.parser.AthenaStatementParser;
import com.alibaba.druid.sql.dialect.bigquery.parser.BigQueryExprParser;
import com.alibaba.druid.sql.dialect.bigquery.parser.BigQueryLexer;
import com.alibaba.druid.sql.dialect.bigquery.parser.BigQueryStatementParser;
import com.alibaba.druid.sql.dialect.blink.parser.BlinkStatementParser;
import com.alibaba.druid.sql.dialect.clickhouse.parser.CKExprParser;
import com.alibaba.druid.sql.dialect.clickhouse.parser.CKLexer;
import com.alibaba.druid.sql.dialect.clickhouse.parser.CKStatementParser;
import com.alibaba.druid.sql.dialect.databricks.parser.DatabricksExprParser;
import com.alibaba.druid.sql.dialect.databricks.parser.DatabricksLexer;
import com.alibaba.druid.sql.dialect.databricks.parser.DatabricksStatementParser;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.parser.DB2ExprParser;
import com.alibaba.druid.sql.dialect.db2.parser.DB2Lexer;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.doris.parser.DorisExprParser;
import com.alibaba.druid.sql.dialect.doris.parser.DorisLexer;
import com.alibaba.druid.sql.dialect.doris.parser.DorisStatementParser;
import com.alibaba.druid.sql.dialect.gaussdb.parser.GaussDbExprParser;
import com.alibaba.druid.sql.dialect.gaussdb.parser.GaussDbLexer;
import com.alibaba.druid.sql.dialect.gaussdb.parser.GaussDbStatementParser;
import com.alibaba.druid.sql.dialect.h2.parser.H2ExprParser;
import com.alibaba.druid.sql.dialect.h2.parser.H2Lexer;
import com.alibaba.druid.sql.dialect.h2.parser.H2StatementParser;
import com.alibaba.druid.sql.dialect.hive.parser.HiveExprParser;
import com.alibaba.druid.sql.dialect.hive.parser.HiveLexer;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hologres.parser.HologresExprParser;
import com.alibaba.druid.sql.dialect.hologres.parser.HologresLexer;
import com.alibaba.druid.sql.dialect.hologres.parser.HologresStatementParser;
import com.alibaba.druid.sql.dialect.impala.parser.ImpalaExprParser;
import com.alibaba.druid.sql.dialect.impala.parser.ImpalaLexer;
import com.alibaba.druid.sql.dialect.impala.parser.ImpalaStatementParser;
import com.alibaba.druid.sql.dialect.informix.parser.InformixStatementParser;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsExprParser;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsLexer;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleLexer;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.OscarSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oscar.parser.OscarExprParser;
import com.alibaba.druid.sql.dialect.oscar.parser.OscarLexer;
import com.alibaba.druid.sql.dialect.oscar.visitor.OscarStatementParser;
import com.alibaba.druid.sql.dialect.phoenix.parser.PhoenixExprParser;
import com.alibaba.druid.sql.dialect.phoenix.parser.PhoenixLexer;
import com.alibaba.druid.sql.dialect.phoenix.parser.PhoenixStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGLexer;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoExprParser;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoLexer;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoStatementParser;
import com.alibaba.druid.sql.dialect.redshift.parser.RedshiftExprParser;
import com.alibaba.druid.sql.dialect.redshift.parser.RedshiftLexer;
import com.alibaba.druid.sql.dialect.redshift.parser.RedshiftStatementParser;
import com.alibaba.druid.sql.dialect.snowflake.SnowflakeExprParser;
import com.alibaba.druid.sql.dialect.snowflake.SnowflakeLexer;
import com.alibaba.druid.sql.dialect.snowflake.SnowflakeStatementParser;
import com.alibaba.druid.sql.dialect.spark.parser.SparkExprParser;
import com.alibaba.druid.sql.dialect.spark.parser.SparkLexer;
import com.alibaba.druid.sql.dialect.spark.parser.SparkStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerExprParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksExprParser;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksLexer;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksStatementParser;
import com.alibaba.druid.sql.dialect.supersql.parser.SuperSqlExprParser;
import com.alibaba.druid.sql.dialect.supersql.parser.SuperSqlLexer;
import com.alibaba.druid.sql.dialect.supersql.parser.SuperSqlStatementParser;
import com.alibaba.druid.sql.dialect.synapse.parser.SynapseExprParser;
import com.alibaba.druid.sql.dialect.synapse.parser.SynapseLexer;
import com.alibaba.druid.sql.dialect.synapse.parser.SynapseStatementParser;
import com.alibaba.druid.sql.dialect.teradata.parser.TDExprParser;
import com.alibaba.druid.sql.dialect.teradata.parser.TDLexer;
import com.alibaba.druid.sql.dialect.teradata.parser.TDStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SQLParserUtils {
    private static final ConcurrentMap<String, DialectParserProvider> DIALECT_PARSER_PROVIDERS = new ConcurrentHashMap<>();
    private static final Map<DbType, StatementParserFactory> BUILTIN_STATEMENT_PARSER_FACTORIES = new EnumMap<>(DbType.class);
    private static final Map<DbType, ExprParserFactory> BUILTIN_EXPR_PARSER_FACTORIES = new EnumMap<>(DbType.class);
    private static final Map<DbType, LexerFactory> BUILTIN_LEXER_FACTORIES = new EnumMap<>(DbType.class);

    private interface StatementParserFactory {
        SQLStatementParser create(String sql, DbType dbType, SQLParserFeature... features);
    }

    private interface ExprParserFactory {
        SQLExprParser create(String sql, DbType dbType, SQLParserFeature... features);
    }

    private interface LexerFactory {
        Lexer create(String sql, DbType dbType, SQLParserFeature... features);
    }

    public interface DialectParserProvider {
        SQLStatementParser createSQLStatementParser(String sql, DbType dbType, SQLParserFeature... features);

        SQLExprParser createExprParser(String sql, DbType dbType, SQLParserFeature... features);

        Lexer createLexer(String sql, DbType dbType, SQLParserFeature... features);
    }

    static {
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new OracleStatementParser(sql, features),
                DbType.oracle, DbType.oceanbase_oracle, DbType.polardb2);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new MySqlStatementParser(sql, features),
                DbType.mysql, DbType.tidb, DbType.mariadb, DbType.goldendb, DbType.oceanbase, DbType.drds, DbType.polardbx);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> {
            MySqlStatementParser parser = new MySqlStatementParser(sql, features);
            parser.dbType = dbType;
            parser.exprParser.dbType = dbType;
            return parser;
        }, DbType.elastic_search);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new PGSQLStatementParser(sql, features),
                DbType.postgresql, DbType.greenplum, DbType.edb);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new GaussDbStatementParser(sql, features), DbType.gaussdb);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new HologresStatementParser(sql, features), DbType.hologres);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new RedshiftStatementParser(sql, features), DbType.redshift);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new SQLServerStatementParser(sql, features),
                DbType.sqlserver, DbType.jtds);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new SynapseStatementParser(sql, features), DbType.synapse);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new H2StatementParser(sql, features), DbType.h2, DbType.lealone);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new BlinkStatementParser(sql, features), DbType.blink);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new DB2StatementParser(sql, features), DbType.db2);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new OdpsStatementParser(sql, features), DbType.odps);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new PhoenixStatementParser(sql), DbType.phoenix);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new HiveStatementParser(sql, features), DbType.hive);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new PrestoStatementParser(sql, features), DbType.presto, DbType.trino);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new SuperSqlStatementParser(sql, features), DbType.supersql);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new AthenaStatementParser(sql, features), DbType.athena);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new BigQueryStatementParser(sql, features), DbType.bigquery);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new SnowflakeStatementParser(sql, features), DbType.snowflake);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new SparkStatementParser(sql, features), DbType.spark);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new DatabricksStatementParser(sql, features), DbType.databricks);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new CKStatementParser(sql, features), DbType.clickhouse);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new StarRocksStatementParser(sql, features), DbType.starrocks);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new InformixStatementParser(sql, features), DbType.informix);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new ImpalaStatementParser(sql, features), DbType.impala);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new DorisStatementParser(sql, features), DbType.doris);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new OscarStatementParser(sql, features), DbType.oscar);
        registerBuiltinStatementParserFactory((sql, dbType, features) -> new TDStatementParser(sql, features), DbType.teradata);

        registerBuiltinExprParserFactory((sql, dbType, features) -> new OracleExprParser(sql, features), DbType.oracle);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new MySqlExprParser(sql, features), DbType.mysql, DbType.mariadb);
        registerBuiltinExprParserFactory((sql, dbType, features) -> {
            MySqlExprParser parser = new MySqlExprParser(sql, features);
            parser.dbType = dbType;
            return parser;
        }, DbType.elastic_search);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new H2ExprParser(sql, features), DbType.h2, DbType.lealone);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new PGExprParser(sql, features),
                DbType.postgresql, DbType.greenplum, DbType.edb);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new GaussDbExprParser(sql, features), DbType.gaussdb);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new HologresExprParser(sql, features), DbType.hologres);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new RedshiftExprParser(sql, features), DbType.redshift);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new SQLServerExprParser(sql, features), DbType.sqlserver, DbType.jtds);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new SynapseExprParser(sql, features), DbType.synapse);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new DB2ExprParser(sql, features), DbType.db2);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new OdpsExprParser(sql, features), DbType.odps);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new PhoenixExprParser(sql, features), DbType.phoenix);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new PrestoExprParser(sql, features), DbType.presto, DbType.trino);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new SuperSqlExprParser(sql, features), DbType.supersql);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new AthenaExprParser(sql, features), DbType.athena);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new HiveExprParser(sql, features), DbType.hive);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new SparkExprParser(sql, features), DbType.spark);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new DatabricksExprParser(sql, features), DbType.databricks);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new BigQueryExprParser(sql, features), DbType.bigquery);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new SnowflakeExprParser(sql, features), DbType.snowflake);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new CKExprParser(sql, features), DbType.clickhouse);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new OscarExprParser(sql, features), DbType.oscar);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new StarRocksExprParser(sql, features), DbType.starrocks);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new ImpalaExprParser(sql, features), DbType.impala);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new DorisExprParser(sql, features), DbType.doris);
        registerBuiltinExprParserFactory((sql, dbType, features) -> new TDExprParser(sql, features), DbType.teradata);

        registerBuiltinLexerFactory((sql, dbType, features) -> new OracleLexer(sql, features), DbType.oracle);
        registerBuiltinLexerFactory((sql, dbType, features) -> new MySqlLexer(sql, features), DbType.mysql, DbType.mariadb);
        registerBuiltinLexerFactory((sql, dbType, features) -> {
            MySqlLexer lexer = new MySqlLexer(sql, features);
            lexer.dbType = dbType;
            return lexer;
        }, DbType.elastic_search);
        registerBuiltinLexerFactory((sql, dbType, features) -> new H2Lexer(sql, features), DbType.h2, DbType.lealone);
        registerBuiltinLexerFactory((sql, dbType, features) -> new PGLexer(sql, features),
                DbType.postgresql, DbType.greenplum, DbType.edb);
        registerBuiltinLexerFactory((sql, dbType, features) -> new GaussDbLexer(sql, features), DbType.gaussdb);
        registerBuiltinLexerFactory((sql, dbType, features) -> new HologresLexer(sql, features), DbType.hologres);
        registerBuiltinLexerFactory((sql, dbType, features) -> new RedshiftLexer(sql, features), DbType.redshift);
        registerBuiltinLexerFactory((sql, dbType, features) -> new DB2Lexer(sql, features), DbType.db2);
        registerBuiltinLexerFactory((sql, dbType, features) -> new OdpsLexer(sql, features), DbType.odps);
        registerBuiltinLexerFactory((sql, dbType, features) -> new PhoenixLexer(sql, features), DbType.phoenix);
        registerBuiltinLexerFactory((sql, dbType, features) -> new PrestoLexer(sql, features), DbType.presto, DbType.trino);
        registerBuiltinLexerFactory((sql, dbType, features) -> new SuperSqlLexer(sql, features), DbType.supersql);
        registerBuiltinLexerFactory((sql, dbType, features) -> new AthenaLexer(sql, features), DbType.athena);
        registerBuiltinLexerFactory((sql, dbType, features) -> new SynapseLexer(sql, features), DbType.synapse);
        registerBuiltinLexerFactory((sql, dbType, features) -> new SparkLexer(sql), DbType.spark);
        registerBuiltinLexerFactory((sql, dbType, features) -> new DatabricksLexer(sql), DbType.databricks);
        registerBuiltinLexerFactory((sql, dbType, features) -> new OscarLexer(sql, features), DbType.oscar);
        registerBuiltinLexerFactory((sql, dbType, features) -> new CKLexer(sql, features), DbType.clickhouse);
        registerBuiltinLexerFactory((sql, dbType, features) -> new StarRocksLexer(sql, features), DbType.starrocks);
        registerBuiltinLexerFactory((sql, dbType, features) -> new HiveLexer(sql, features), DbType.hive);
        registerBuiltinLexerFactory((sql, dbType, features) -> new BigQueryLexer(sql, features), DbType.bigquery);
        registerBuiltinLexerFactory((sql, dbType, features) -> new SnowflakeLexer(sql, features), DbType.snowflake);
        registerBuiltinLexerFactory((sql, dbType, features) -> new ImpalaLexer(sql, features), DbType.impala);
        registerBuiltinLexerFactory((sql, dbType, features) -> new DorisLexer(sql, features), DbType.doris);
        registerBuiltinLexerFactory((sql, dbType, features) -> new TDLexer(sql, features), DbType.teradata);
    }

    private static void registerBuiltinStatementParserFactory(StatementParserFactory factory, DbType... dbTypes) {
        for (DbType dbType : dbTypes) {
            BUILTIN_STATEMENT_PARSER_FACTORIES.put(dbType, factory);
        }
    }

    private static void registerBuiltinExprParserFactory(ExprParserFactory factory, DbType... dbTypes) {
        for (DbType dbType : dbTypes) {
            BUILTIN_EXPR_PARSER_FACTORIES.put(dbType, factory);
        }
    }

    private static void registerBuiltinLexerFactory(LexerFactory factory, DbType... dbTypes) {
        for (DbType dbType : dbTypes) {
            BUILTIN_LEXER_FACTORIES.put(dbType, factory);
        }
    }

    public static DialectParserProvider registerDialectParserProvider(String dialectKey, DialectParserProvider provider) {
        String normalizedDialectKey = normalizeDialectKey(dialectKey);
        if (provider == null) {
            throw new IllegalArgumentException("provider must not be null");
        }

        return DIALECT_PARSER_PROVIDERS.put(normalizedDialectKey, provider);
    }

    public static DialectParserProvider unregisterDialectParserProvider(String dialectKey) {
        String normalizedDialectKey = normalizeDialectKey(dialectKey);
        return DIALECT_PARSER_PROVIDERS.remove(normalizedDialectKey);
    }

    public static DialectParserProvider getDialectParserProvider(String dialectKey) {
        String normalizedDialectKey = normalizeDialectKey(dialectKey);
        return DIALECT_PARSER_PROVIDERS.get(normalizedDialectKey);
    }

    private static String normalizeDialectKey(String dialectKey) {
        if (dialectKey == null) {
            throw new IllegalArgumentException("dialectKey must not be null");
        }

        String normalizedDialectKey = dialectKey.trim();
        if (normalizedDialectKey.isEmpty()) {
            throw new IllegalArgumentException("dialectKey must not be blank");
        }

        return normalizedDialectKey.toLowerCase(Locale.ROOT);
    }

    private static DialectParserProvider getDialectParserProvider(DbType dbType) {
        if (dbType == null) {
            return null;
        }
        return DIALECT_PARSER_PROVIDERS.get(dbType.name().toLowerCase(Locale.ROOT));
    }

    public static SQLStatementParser createSQLStatementParser(String sql, DbType dbType) {
        SQLParserFeature[] features;
        if (DbType.odps == dbType || DbType.mysql == dbType) {
            features = new SQLParserFeature[]{SQLParserFeature.KeepComments};
        } else {
            features = new SQLParserFeature[]{};
        }
        return createSQLStatementParser(sql, dbType, features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, DbType dbType, boolean keepComments) {
        SQLParserFeature[] features;
        if (keepComments) {
            features = new SQLParserFeature[]{SQLParserFeature.KeepComments};
        } else {
            features = new SQLParserFeature[]{};
        }

        return createSQLStatementParser(sql, dbType, features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType, SQLParserFeature... features) {
        DialectParserProvider provider = dbType == null ? null : getDialectParserProvider(dbType);
        if (provider != null) {
            DbType parsedDbType = DbType.of(dbType);
            SQLStatementParser parser = provider.createSQLStatementParser(sql, parsedDbType == null ? DbType.other : parsedDbType, features);
            if (parser != null) {
                return parser;
            }
        }
        return createSQLStatementParser(sql, dbType == null ? null : DbType.valueOf(dbType), features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, DbType dbType, SQLParserFeature... features) {
        if (sql.indexOf("\r\n") != -1) {
            // com.alibaba.druid.sql.parser.Lexer only recognizes Linux newline '\n'.
            sql = sql.replace("\r\n", "\n");
        }

        if (dbType == null) {
            dbType = DbType.other;
        }

        DialectParserProvider provider = getDialectParserProvider(dbType);
        if (provider != null) {
            SQLStatementParser parser = provider.createSQLStatementParser(sql, dbType, features);
            if (parser != null) {
                return parser;
            }
        }
        StatementParserFactory factory = BUILTIN_STATEMENT_PARSER_FACTORIES.get(dbType);
        if (factory != null) {
            return factory.create(sql, dbType, features);
        }
        return new SQLStatementParser(sql, dbType, features);
    }

    public static SQLExprParser createExprParser(String sql, DbType dbType, SQLParserFeature... features) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        DialectParserProvider provider = getDialectParserProvider(dbType);
        if (provider != null) {
            SQLExprParser parser = provider.createExprParser(sql, dbType, features);
            if (parser != null) {
                return parser;
            }
        }
        ExprParserFactory factory = BUILTIN_EXPR_PARSER_FACTORIES.get(dbType);
        if (factory != null) {
            return factory.create(sql, dbType, features);
        }
        return new SQLExprParser(sql, dbType, features);
    }

    public static Lexer createLexer(String sql, DbType dbType) {
        return createLexer(sql, dbType, new SQLParserFeature[0]);
    }

    public static Lexer createLexer(String sql, DbType dbType, SQLParserFeature... features) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        DialectParserProvider provider = getDialectParserProvider(dbType);
        if (provider != null) {
            Lexer lexer = provider.createLexer(sql, dbType, features);
            if (lexer != null) {
                return lexer;
            }
        }
        LexerFactory factory = BUILTIN_LEXER_FACTORIES.get(dbType);
        if (factory != null) {
            return factory.create(sql, dbType, features);
        }
        Lexer lexer = new Lexer(sql, null, dbType);
        for (SQLParserFeature feature : features) {
            lexer.config(feature, true);
        }
        return lexer;
    }

    public static SQLSelectQueryBlock createSelectQueryBlock(DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case mysql:
                return new MySqlSelectQueryBlock();
            case oracle:
                return new OracleSelectQueryBlock();
            case db2:
                return new DB2SelectQueryBlock();
            case postgresql:
            case greenplum:
            case edb:
            case hologres:
            case redshift:
                return new PGSelectQueryBlock();
            case odps:
                return new OdpsSelectQueryBlock();
            case sqlserver:
                return new SQLServerSelectQueryBlock();
            case synapse:
                return new SQLServerSelectQueryBlock();
            case oscar:
                return new OscarSelectQueryBlock();
            default:
                return new SQLSelectQueryBlock(dbType);
        }
    }

    public static SQLType getSQLType(String sql, DbType dbType) {
        Lexer lexer = createLexer(sql, dbType);
        return lexer.scanSQLType();
    }

    public static SQLType getSQLTypeV2(String sql, DbType dbType) {
        Lexer lexer = createLexer(sql, dbType);
        return lexer.scanSQLTypeV2();
    }

    public static boolean startsWithHint(String sql, DbType dbType) {
        Lexer lexer = createLexer(sql, dbType);
        lexer.nextToken();
        return lexer.token() == Token.HINT;
    }

    public static boolean containsAny(String sql, DbType dbType, Token token) {
        Lexer lexer = createLexer(sql, dbType);
        for (; ; ) {
            lexer.nextToken();
            final Token tok = lexer.token;
            switch (tok) {
                case EOF:
                case ERROR:
                    return false;
                default:
                    if (tok == token) {
                        return true;
                    }
                    break;
            }
        }
    }

    public static boolean containsAny(String sql, DbType dbType, Token token1, Token token2) {
        Lexer lexer = createLexer(sql, dbType);
        for (; ; ) {
            lexer.nextToken();
            final Token tok = lexer.token;
            switch (tok) {
                case EOF:
                case ERROR:
                    return false;
                default:
                    if (tok == token1 || tok == token2) {
                        return true;
                    }
                    break;
            }
        }
    }

    public static boolean containsAny(String sql, DbType dbType, Token token1, Token token2, Token token3) {
        Lexer lexer = createLexer(sql, dbType);
        for (; ; ) {
            lexer.nextToken();
            final Token tok = lexer.token;
            switch (tok) {
                case EOF:
                case ERROR:
                    return false;
                default:
                    if (tok == token1 || tok == token2 || tok == token3) {
                        return true;
                    }
                    break;
            }
        }
    }

    public static boolean containsAny(String sql, DbType dbType, Token... tokens) {
        if (tokens == null) {
            return false;
        }

        Lexer lexer = createLexer(sql, dbType);
        for (; ; ) {
            lexer.nextToken();
            final Token tok = lexer.token;
            switch (tok) {
                case EOF:
                case ERROR:
                    return false;
                default:
                    for (int i = 0; i < tokens.length; i++) {
                        if (tokens[i] == tok) {
                            return true;
                        }
                    }
                    break;
            }
        }
    }

    public static Object getSimpleSelectValue(String sql, DbType dbType) {
        return getSimpleSelectValue(sql, dbType, null);
    }

    public static Object getSimpleSelectValue(String sql, DbType dbType, SimpleValueEvalHandler handler) {
        Lexer lexer = createLexer(sql, dbType);
        lexer.nextToken();

        if (lexer.token != Token.SELECT && lexer.token != Token.VALUES) {
            return null;
        }

        lexer.nextTokenValue();

        SQLExpr expr = null;
        Object value;
        switch (lexer.token) {
            case LITERAL_INT:
                value = lexer.integerValue();
                break;
            case LITERAL_CHARS:
            case LITERAL_NCHARS:
                value = lexer.stringVal();
                break;
            case LITERAL_FLOAT:
                value = lexer.decimalValue();
                break;
            default:
                if (handler == null) {
                    return null;
                }

                expr = new SQLExprParser(lexer).expr();
                try {
                    value = handler.eval(expr);
                } catch (Exception error) {
                    // skip
                    value = null;
                }
                break;
        }

        lexer.nextToken();

        if (lexer.token == Token.FROM) {
            lexer.nextToken();
            if (lexer.token == Token.DUAL) {
                lexer.nextToken();
            } else {
                return null;
            }
        }
        if (lexer.token != Token.EOF) {
            return null;
        }

        return value;
    }

    public static interface SimpleValueEvalHandler {
        Object eval(SQLExpr expr);
    }

    public static String replaceBackQuote(String sql, DbType dbType) {
        int i = sql.indexOf('`');

        if (i == -1) {
            return sql;
        }

        char[] chars = sql.toCharArray();
        Lexer lexer = SQLParserUtils.createLexer(sql, dbType);

        int len = chars.length;
        int off = 0;

        for_:
        for (; ; ) {
            lexer.nextToken();

            int p0, p1;
            char c0, c1;
            switch (lexer.token) {
                case IDENTIFIER:
                    p0 = lexer.startPos + off;
                    p1 = lexer.pos - 1 + off;
                    c0 = chars[p0];
                    c1 = chars[p1];
                    if (c0 == '`' && c1 == '`') {
                        if (p1 - p0 > 2 && chars[p0 + 1] == '\'' && chars[p1 - 1] == '\'') {
                            System.arraycopy(chars, p0 + 1, chars, p0, p1 - p0 - 1);
                            System.arraycopy(chars, p1 + 1, chars, p1 - 1, chars.length - p1 - 1);
                            len -= 2;
                            off -= 2;
                        } else {
                            chars[p0] = '"';
                            chars[p1] = '"';
                        }

                    }
                    break;
                case EOF:
                case ERROR:
                    break for_;
                default:
                    break;
            }
        }

        return new String(chars, 0, len);
    }

    public static String addBackQuote(String sql, DbType dbType) {
        if (StringUtils.isEmpty(sql)) {
            return sql;
        }
        SQLStatementParser parser = createSQLStatementParser(sql, dbType);
        StringBuilder buf = new StringBuilder(sql.length() + 20);
        SQLASTOutputVisitor out = SQLUtils.createOutputVisitor(buf, DbType.mysql);
        out.config(VisitorFeature.OutputNameQuote, true);

        SQLType sqlType = getSQLType(sql, dbType);
        if (sqlType == SQLType.INSERT) {
            parser.config(SQLParserFeature.InsertReader, true);

            SQLInsertStatement stmt = (SQLInsertStatement) parser.parseStatement();
            int startPos = parser.getLexer().startPos;

            stmt.accept(out);

            if (stmt.getQuery() == null) {
                buf.append(' ');
                buf.append(sql, startPos, sql.length());
            }
        } else {
            SQLStatement stmt = parser.parseStatement();
            stmt.accept(out);
        }

        return buf.toString();
    }

    public static List<String> split(String sql, DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        {
            Lexer lexer = createLexer(sql, dbType);
            lexer.nextToken();

            boolean script = false;
            if (dbType == DbType.odps && lexer.token == Token.VARIANT) {
                script = true;
            }

            if (script) {
                return Collections.singletonList(sql);
            }
        }

        List list = new ArrayList();

        Lexer lexer = createLexer(sql, dbType);
        lexer.config(SQLParserFeature.SkipComments, false);
        lexer.config(SQLParserFeature.KeepComments, true);

        boolean set = false, paiOrJar = false;
        int start = 0;
        Token token = lexer.token;
        for (; lexer.token != Token.EOF; ) {
            if (token == Token.SEMI) {
                int len = lexer.startPos - start;
                if (len > 0) {
                    String lineSql = sql.substring(start, lexer.startPos);
                    lineSql = lineSql.trim();
                    if (!lineSql.isEmpty()) {
                        list.add(lineSql);
                    }
                }
                start = lexer.startPos + 1;
                set = false;
            } else if (token == Token.CREATE) {
                lexer.nextToken();

                if (lexer.token == Token.FUNCTION || lexer.identifierEquals("FUNCTION")) {
                    lexer.nextToken();
                    lexer.nextToken();
                    if (lexer.token == Token.AS) {
                        lexer.nextToken();
                        if (lexer.token == Token.LITERAL_CHARS) {
                            lexer.nextToken();
                            token = lexer.token;
                            continue;
                        }
                    }
                    lexer.startPos = sql.length();
                    break;
                }

                token = lexer.token;
                continue;
            } else if (set && token == Token.EQ && dbType == DbType.odps) {
                lexer.nextTokenForSet();
                token = lexer.token;
                continue;
            }

            if (lexer.identifierEquals("USING")) {
                lexer.nextToken();
                if (lexer.identifierEquals("jar")) {
                    lexer.nextToken();
                }
            }

            if (lexer.token == Token.SET) {
                set = true;
            }

            if (lexer.identifierEquals("ADD") && (dbType == DbType.hive || dbType == DbType.odps || dbType == DbType.spark)) {
                lexer.nextToken();
                if (lexer.identifierEquals("JAR")) {
                    lexer.nextPath();
                }
            } else {
                lexer.nextToken();
            }
            token = lexer.token;
        }

        if (start != sql.length() && token != Token.SEMI) {
            int end = lexer.startPos;
            if (end > sql.length()) {
                end = sql.length();
            }
            String splitSql = sql.substring(start, end).trim();
            if (!paiOrJar) {
                splitSql = removeComment(splitSql, dbType).trim();
            } else {
                if (splitSql.endsWith(";")) {
                    splitSql = splitSql.substring(0, splitSql.length() - 1).trim();
                }
            }
            if (!splitSql.isEmpty()) {
                list.add(splitSql);
            }
        }

        return list;
    }

    public static List<String> splitAndRemoveComment(String sql, DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        boolean containsCommentAndSemi = false;
        {
            Lexer lexer = createLexer(sql, dbType);
            lexer.config(SQLParserFeature.SkipComments, false);
            lexer.config(SQLParserFeature.KeepComments, true);

            while (lexer.token != Token.EOF) {
                if (lexer.token == Token.LINE_COMMENT
                        || lexer.token == Token.MULTI_LINE_COMMENT
                        || lexer.token == Token.SEMI) {
                    containsCommentAndSemi = true;
                    break;
                }
                lexer.nextToken();
            }

            if (!containsCommentAndSemi) {
                return Collections.singletonList(sql);
            }
        }

        {
            Lexer lexer = createLexer(sql, dbType);
            lexer.nextToken();

            boolean script = false;
            if (dbType == DbType.odps && lexer.token == Token.VARIANT) {
                script = true;
            }

            if (script || lexer.identifierEquals("pai") || lexer.identifierEquals("jar") || lexer.identifierEquals("copy")) {
                return Collections.singletonList(sql);
            }
        }

        List list = new ArrayList();

        Lexer lexer = createLexer(sql, dbType);
        lexer.config(SQLParserFeature.SkipComments, false);
        lexer.config(SQLParserFeature.KeepComments, true);
        lexer.nextToken();

        boolean set = false, paiOrJar = false;
        int start = 0;
        Token preToken = null;
        int prePos = 0;
        Token token = lexer.token;
        Token startToken = lexer.token;
        while (token == Token.LINE_COMMENT || token == Token.MULTI_LINE_COMMENT) {
            lexer.nextToken();
            token = lexer.token;
            startToken = token;
            start = lexer.startPos;
        }

        for (int tokens = 1; lexer.token != Token.EOF; ) {
            if (token == Token.SEMI) {
                int len = lexer.startPos - start;
                if (len > 0) {
                    String lineSql = sql.substring(start, lexer.startPos);
                    String splitSql = set
                            ? removeLeftComment(lineSql, dbType)
                            : removeComment(lineSql, dbType
                    ).trim();
                    if (!splitSql.isEmpty()) {
                        list.add(splitSql);
                    }
                }
                lexer.nextToken();
                token = lexer.token;
                start = lexer.startPos;
                startToken = token;
                set = false;
                tokens = token == Token.LINE_COMMENT || token == Token.MULTI_LINE_COMMENT ? 0 : 1;
                continue;
            } else if (token == Token.MULTI_LINE_COMMENT) {
                int len = lexer.startPos - start;
                if (len > 0) {
                    String splitSql = removeComment(
                            sql.substring(start, lexer.startPos),
                            dbType
                    ).trim();
                    if (!splitSql.isEmpty()) {
                        list.add(splitSql);
                    }
                }
                lexer.nextToken();
                token = lexer.token;
                start = lexer.startPos;
                startToken = token;
                tokens = token == Token.LINE_COMMENT || token == Token.MULTI_LINE_COMMENT ? 0 : 1;
                continue;
            } else if (token == Token.CREATE) {
                lexer.nextToken();

                if (lexer.token == Token.FUNCTION || lexer.identifierEquals("FUNCTION")) {
                    lexer.nextToken();
                    lexer.nextToken();
                    if (lexer.token == Token.AS) {
                        lexer.nextToken();
                        if (lexer.token == Token.LITERAL_CHARS) {
                            lexer.nextToken();
                            token = lexer.token;
                            continue;
                        }
                    }
                    lexer.startPos = sql.length();
                    break;
                }

                token = lexer.token;
                continue;
            } else if (set && token == Token.EQ && dbType == DbType.odps) {
                lexer.nextTokenForSet();
                token = lexer.token;
                continue;
            } else if (dbType == DbType.odps
                    && (preToken == null || preToken == Token.LINE_COMMENT || preToken == Token.SEMI)
                    && (lexer.identifierEquals("pai") || lexer.identifierEquals("jar") || lexer.identifierEquals("copy"))) {
                lexer.scanLineArgument();
                paiOrJar = true;
            }

            if (lexer.identifierEquals("USING")) {
                lexer.nextToken();
                if (lexer.identifierEquals("jar")) {
                    lexer.nextToken();
                }
            }

            if (lexer.token == Token.SET) {
                set = true;
            }

            prePos = lexer.pos;
            if (lexer.identifierEquals("ADD") && (dbType == DbType.hive || dbType == DbType.odps || dbType == DbType.spark)) {
                lexer.nextToken();
                if (lexer.identifierEquals("JAR")) {
                    lexer.nextPath();
                }
            } else {
                lexer.nextToken();
            }
            preToken = token;
            token = lexer.token;
            if (token == Token.LINE_COMMENT
                    && tokens == 0) {
                start = lexer.pos;
                startToken = token;
            }

            if (token != Token.LINE_COMMENT && token != Token.MULTI_LINE_COMMENT && token != Token.SEMI) {
                tokens++;
            }
        }

        if (start != sql.length() && token != Token.SEMI) {
            int end = lexer.startPos;
            if (end > sql.length()) {
                end = sql.length();
            }
            String splitSql = sql.substring(start, end).trim();
            if (!paiOrJar) {
                splitSql = removeComment(splitSql, dbType).trim();
            } else {
                if (splitSql.endsWith(";")) {
                    splitSql = splitSql.substring(0, splitSql.length() - 1).trim();
                }
            }
            if (!splitSql.isEmpty()) {
                list.add(splitSql);
            }
        }

        return list;
    }

    public static String removeLeftComment(String sql, DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        sql = sql.trim();
        if (sql.startsWith("jar")) {
            return sql;
        }

        boolean containsComment = false;
        {
            Lexer lexer = createLexer(sql, dbType);
            lexer.config(SQLParserFeature.SkipComments, false);
            lexer.config(SQLParserFeature.KeepComments, true);

            while (lexer.token != Token.EOF) {
                if (lexer.token == Token.LINE_COMMENT || lexer.token == Token.MULTI_LINE_COMMENT) {
                    containsComment = true;
                    break;
                }
                lexer.nextToken();
            }

            if (!containsComment) {
                return sql;
            }
        }

        StringBuilder sb = new StringBuilder();

        Lexer lexer = createLexer(sql, dbType);
        lexer.config(SQLParserFeature.SkipComments, false);
        lexer.config(SQLParserFeature.KeepComments, true);
        lexer.nextToken();

        int start = 0;
        for (; lexer.token != Token.EOF; lexer.nextToken()) {
            if (lexer.token == Token.LINE_COMMENT || lexer.token == Token.MULTI_LINE_COMMENT) {
                continue;
            }
            start = lexer.startPos;
            break;
        }

        if (start != sql.length()) {
            sb.append(sql.substring(start, sql.length()));
        }

        return sb.toString();
    }

    public static String removeComment(String sql, DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        sql = sql.trim();
        if (sql.startsWith("jar") || sql.startsWith("JAR")) {
            return sql;
        }

        if ((sql.startsWith("pai") || sql.startsWith("PAI")) && sql.indexOf(';') == -1) {
            return sql;
        }

        boolean containsComment = false;
        {
            Lexer lexer = createLexer(sql, dbType);
            lexer.config(SQLParserFeature.SkipComments, false);
            lexer.config(SQLParserFeature.KeepComments, true);

            while (lexer.token != Token.EOF) {
                if (lexer.token == Token.LINE_COMMENT || lexer.token == Token.MULTI_LINE_COMMENT) {
                    containsComment = true;
                    break;
                }
                lexer.nextToken();
            }

            if (!containsComment) {
                return sql;
            }
        }

        StringBuilder sb = new StringBuilder();

        Lexer lexer = createLexer(sql, dbType);
        lexer.config(SQLParserFeature.SkipComments, false);
        lexer.config(SQLParserFeature.KeepComments, true);

        int start = 0;
        Token token = lexer.token;
        for (; lexer.token != Token.EOF; ) {
            if (token == Token.LINE_COMMENT) {
                int len = lexer.startPos - start;
                if (len > 0) {
                    sb.append(sql.substring(start, lexer.startPos));
                }
                start = lexer.startPos + lexer.stringVal().length();
                if (lexer.startPos > 1 && lexer.text.charAt(lexer.startPos - 1) == '\n') {
                    while (start + 1 < lexer.text.length() && lexer.text.charAt(start) == '\n') {
                        start = start + 1;
                    }
                }
            } else if (token == Token.MULTI_LINE_COMMENT) {
                int len = lexer.startPos - start;
                if (len > 0) {
                    sb.append(sql.substring(start, lexer.startPos));
                }
                start = lexer.startPos + lexer.stringVal().length();
            }

            if (lexer.identifierEquals("ADD")) {
                lexer.nextToken();
                if (lexer.identifierEquals("JAR")) {
                    lexer.nextPath();
                }
            } else {
                lexer.nextToken();
            }
            token = lexer.token;
        }

        if (start != sql.length() && token != Token.LINE_COMMENT && token != Token.MULTI_LINE_COMMENT) {
            sb.append(sql.substring(start, sql.length()));
        }

        return sb.toString();
    }

    public static List<String> getTables(String sql, DbType dbType) {
        Set<String> tables = new LinkedHashSet<>();

        boolean set = false;
        Lexer lexer = createLexer(sql, dbType);
        lexer.nextToken();

        SQLExprParser exprParser;
        switch (dbType) {
            case odps:
                exprParser = new OdpsExprParser(lexer);
                break;
            case mysql:
                exprParser = new MySqlExprParser(lexer);
                break;
            default:
                exprParser = new SQLExprParser(lexer);
                break;
        }

        for_:
        for (; lexer.token != Token.EOF; ) {
            switch (lexer.token) {
                case CREATE:
                case DROP:
                case ALTER:
                    set = false;
                    lexer.nextToken();

                    if (lexer.token == Token.TABLE) {
                        lexer.nextToken();

                        if (lexer.token == Token.IF) {
                            lexer.nextToken();

                            if (lexer.token == Token.NOT) {
                                lexer.nextToken();
                            }

                            if (lexer.token == Token.EXISTS) {
                                lexer.nextToken();
                            }
                        }

                        SQLName name = exprParser.name();
                        tables.add(name.toString());

                        if (lexer.token == Token.AS) {
                            lexer.nextToken();
                        }
                    }
                    continue for_;
                case FROM:
                case JOIN:
                    lexer.nextToken();
                    if (lexer.token != Token.LPAREN
                            && lexer.token != Token.VALUES
                    ) {
                        SQLName name = exprParser.name();
                        tables.add(name.toString());
                    }
                    continue for_;
                case SEMI:
                    set = false;
                    break;
                case SET:
                    set = true;
                    break;
                case EQ:
                    if (set && dbType == DbType.odps) {
                        lexer.nextTokenForSet();
                        continue for_;
                    }
                    break;
                default:
                    break;
            }

            lexer.nextToken();

        }

        return new ArrayList<>(tables);
    }
}
