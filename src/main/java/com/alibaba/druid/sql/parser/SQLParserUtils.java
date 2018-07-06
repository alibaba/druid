/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.parser.DB2ExprParser;
import com.alibaba.druid.sql.dialect.db2.parser.DB2Lexer;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.h2.parser.H2StatementParser;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsExprParser;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsLexer;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleLexer;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.phoenix.parser.PhoenixExprParser;
import com.alibaba.druid.sql.dialect.phoenix.parser.PhoenixLexer;
import com.alibaba.druid.sql.dialect.phoenix.parser.PhoenixStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGLexer;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerExprParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerLexer;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

public class SQLParserUtils {

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType) {
        SQLParserFeature[] features;
        if (JdbcConstants.ODPS.equals(dbType) || JdbcConstants.MYSQL.equals(dbType)) {
            features = new SQLParserFeature[] {SQLParserFeature.KeepComments};
        } else {
            features = new SQLParserFeature[] {};
        }
        return createSQLStatementParser(sql, dbType, features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType, boolean keepComments) {
        SQLParserFeature[] features;
        if (keepComments) {
            features = new SQLParserFeature[] {SQLParserFeature.KeepComments};
        } else {
            features = new SQLParserFeature[] {};
        }

        return createSQLStatementParser(sql, dbType, features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType, SQLParserFeature... features) {
        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            return new OracleStatementParser(sql);
        }

        if (JdbcUtils.MYSQL.equals(dbType) || JdbcUtils.ALIYUN_DRDS.equals(dbType)) {
            return new MySqlStatementParser(sql, features);
        }

        if (JdbcUtils.MARIADB.equals(dbType)) {
            return new MySqlStatementParser(sql, features);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)
                || JdbcUtils.ENTERPRISEDB.equals(dbType)) {
            return new PGSQLStatementParser(sql);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerStatementParser(sql);
        }

        if (JdbcUtils.H2.equals(dbType)) {
            return new H2StatementParser(sql);
        }
        
        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2StatementParser(sql);
        }
        
        if (JdbcUtils.ODPS.equals(dbType)) {
            return new OdpsStatementParser(sql);
        }

        if (JdbcUtils.PHOENIX.equals(dbType)) {
            return new PhoenixStatementParser(sql);
        }

        if (JdbcUtils.HIVE.equals(dbType)) {
            return new HiveStatementParser(sql);
        }

        if (JdbcUtils.ELASTIC_SEARCH.equals(dbType)) {
            return new MySqlStatementParser(sql);
        }

        return new SQLStatementParser(sql, dbType);
    }

    public static SQLExprParser createExprParser(String sql, String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            return new OracleExprParser(sql);
        }

        if (JdbcUtils.MYSQL.equals(dbType) || //
            JdbcUtils.MARIADB.equals(dbType) || //
            JdbcUtils.H2.equals(dbType)) {
            return new MySqlExprParser(sql);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)
                || JdbcUtils.ENTERPRISEDB.equals(dbType)) {
            return new PGExprParser(sql);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerExprParser(sql);
        }
        
        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2ExprParser(sql);
        }
        
        if (JdbcUtils.ODPS.equals(dbType)) {
            return new OdpsExprParser(sql);
        }

        if (JdbcUtils.PHOENIX.equals(dbType)) {
            return new PhoenixExprParser(sql);
        }

        return new SQLExprParser(sql);
    }

    public static Lexer createLexer(String sql, String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            return new OracleLexer(sql);
        }

        if (JdbcUtils.MYSQL.equals(dbType) || //
                JdbcUtils.MARIADB.equals(dbType) || //
                JdbcUtils.H2.equals(dbType)) {
            return new MySqlLexer(sql);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)
                || JdbcUtils.ENTERPRISEDB.equals(dbType)) {
            return new PGLexer(sql);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerLexer(sql);
        }

        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2Lexer(sql);
        }

        if (JdbcUtils.ODPS.equals(dbType)) {
            return new OdpsLexer(sql);
        }

        if (JdbcUtils.PHOENIX.equals(dbType)) {
            return new PhoenixLexer(sql);
        }

        return new Lexer(sql);
    }

    public static SQLSelectQueryBlock createSelectQueryBlock(String dbType) {
        if (JdbcConstants.MYSQL.equals(dbType)) {
            return new MySqlSelectQueryBlock();
        }

        if (JdbcConstants.ORACLE.equals(dbType)) {
            return new OracleSelectQueryBlock();
        }

        if (JdbcConstants.DB2.equals(dbType)) {
            return new DB2SelectQueryBlock();
        }

        if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            return new DB2SelectQueryBlock();
        }

        if (JdbcConstants.ODPS.equals(dbType)) {
            return new DB2SelectQueryBlock();
        }

        if (JdbcConstants.SQL_SERVER.equals(dbType)) {
            return new DB2SelectQueryBlock();
        }

        return new SQLSelectQueryBlock();
     }
}
