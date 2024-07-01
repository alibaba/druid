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
import com.alibaba.druid.sql.dialect.ads.parser.AdsStatementParser;
import com.alibaba.druid.sql.dialect.bigquery.parser.BigQueryExprParser;
import com.alibaba.druid.sql.dialect.bigquery.parser.BigQueryLexer;
import com.alibaba.druid.sql.dialect.bigquery.parser.BigQueryStatementParser;
import com.alibaba.druid.sql.dialect.blink.parser.BlinkStatementParser;
import com.alibaba.druid.sql.dialect.clickhouse.parser.CKExprParser;
import com.alibaba.druid.sql.dialect.clickhouse.parser.CKLexer;
import com.alibaba.druid.sql.dialect.clickhouse.parser.CKStatementParser;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.parser.DB2ExprParser;
import com.alibaba.druid.sql.dialect.db2.parser.DB2Lexer;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.h2.parser.H2ExprParser;
import com.alibaba.druid.sql.dialect.h2.parser.H2Lexer;
import com.alibaba.druid.sql.dialect.h2.parser.H2StatementParser;
import com.alibaba.druid.sql.dialect.hive.parser.HiveExprParser;
import com.alibaba.druid.sql.dialect.hive.parser.HiveLexer;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.holo.parser.HoloExprParser;
import com.alibaba.druid.sql.dialect.holo.parser.HoloLexer;
import com.alibaba.druid.sql.dialect.holo.parser.HoloStatementParser;
import com.alibaba.druid.sql.dialect.infomix.parser.InformixStatementParser;
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
import com.alibaba.druid.sql.dialect.spark.parser.SparkLexer;
import com.alibaba.druid.sql.dialect.spark.parser.SparkStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerExprParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksExprParser;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksLexer;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.StringUtils;

import java.util.*;

public class SQLParserUtils {
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

        switch (dbType) {
            case oracle:
            case oceanbase_oracle:
                return new OracleStatementParser(sql, features);
            case mysql:
            case tidb:
            case mariadb:
            case goldendb:
            case oceanbase:
            case drds: {
                return new MySqlStatementParser(sql, features);
            }
            case elastic_search: {
                MySqlStatementParser parser = new MySqlStatementParser(sql, features);
                parser.dbType = dbType;
                parser.exprParser.dbType = dbType;
                return parser;
            }
            case postgresql:
            case greenplum:
            case edb:
            case gaussdb:
                return new PGSQLStatementParser(sql, features);
            case hologres:
                return new HoloStatementParser(sql, features);
            case sqlserver:
            case jtds:
                return new SQLServerStatementParser(sql, features);
            case h2:
                return new H2StatementParser(sql, features);
            case blink:
                return new BlinkStatementParser(sql, features);
            case db2:
                return new DB2StatementParser(sql, features);
            case odps:
                return new OdpsStatementParser(sql, features);
            case phoenix:
                return new PhoenixStatementParser(sql);
            case hive:
                return new HiveStatementParser(sql, features);
            case presto:
            case trino:
                return new PrestoStatementParser(sql, features);
            case bigquery:
                return new BigQueryStatementParser(sql, features);
            case ads:
                return new AdsStatementParser(sql);
            case spark:
                return new SparkStatementParser(sql);
            case clickhouse:
                return new CKStatementParser(sql);
            case starrocks:
                return new StarRocksStatementParser(sql);
            case informix:
                return new InformixStatementParser(sql, features);
            default:
                return new SQLStatementParser(sql, dbType, features);
        }
    }

    public static SQLExprParser createExprParser(String sql, DbType dbType, SQLParserFeature... features) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case oracle:
                return new OracleExprParser(sql, features);
            case mysql:
            case mariadb:
                return new MySqlExprParser(sql, features);
            case elastic_search: {
                MySqlExprParser parser = new MySqlExprParser(sql, features);
                parser.dbType = dbType;
                return parser;
            }
            case h2:
                return new H2ExprParser(sql, features);
            case postgresql:
            case greenplum:
            case edb:
            case gaussdb:
                return new PGExprParser(sql, features);
            case hologres:
                return new HoloExprParser(sql, features);
            case sqlserver:
            case jtds:
                return new SQLServerExprParser(sql, features);
            case db2:
                return new DB2ExprParser(sql, features);
            case odps:
                return new OdpsExprParser(sql, features);
            case phoenix:
                return new PhoenixExprParser(sql, features);
            case presto:
            case trino:
                return new PrestoExprParser(sql, features);
            case hive:
                return new HiveExprParser(sql, features);
            case bigquery:
                return new BigQueryExprParser(sql, features);
            case clickhouse:
                return new CKExprParser(sql, features);
            case oscar:
                return new OscarExprParser(sql, features);
            case starrocks:
                return new StarRocksExprParser(sql, features);
            default:
                return new SQLExprParser(sql, dbType, features);
        }
    }

    public static Lexer createLexer(String sql, DbType dbType) {
        return createLexer(sql, dbType, new SQLParserFeature[0]);
    }

    public static Lexer createLexer(String sql, DbType dbType, SQLParserFeature... features) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case oracle:
                return new OracleLexer(sql, features);
            case mysql:
            case mariadb:
                return new MySqlLexer(sql, features);
            case elastic_search: {
                MySqlLexer lexer = new MySqlLexer(sql, features);
                lexer.dbType = dbType;
                return lexer;
            }
            case h2:
                return new H2Lexer(sql, features);
            case postgresql:
            case greenplum:
            case edb:
                return new PGLexer(sql, features);
            case hologres:
                return new HoloLexer(sql, features);
            case db2:
                return new DB2Lexer(sql, features);
            case odps:
                return new OdpsLexer(sql, features);
            case phoenix:
                return new PhoenixLexer(sql, features);
            case presto:
            case trino:
                return new PrestoLexer(sql, features);
            case spark:
                return new SparkLexer(sql);
            case oscar:
                return new OscarLexer(sql, features);
            case clickhouse:
                return new CKLexer(sql, features);
            case starrocks:
                return new StarRocksLexer(sql, features);
            case hive:
                return new HiveLexer(sql, features);
            case bigquery:
                return new BigQueryLexer(sql, features);
            default: {
                Lexer lexer = new Lexer(sql, null, dbType);
                for (SQLParserFeature feature : features) {
                    lexer.config(feature, true);
                }
                return lexer;
            }
        }
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
                return new PGSelectQueryBlock();
            case odps:
                return new OdpsSelectQueryBlock();
            case sqlserver:
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

            if (lexer.identifierEquals("ADD") && (dbType == DbType.hive || dbType == DbType.odps)) {
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
            if (lexer.identifierEquals("ADD") && (dbType == DbType.hive || dbType == DbType.odps)) {
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
