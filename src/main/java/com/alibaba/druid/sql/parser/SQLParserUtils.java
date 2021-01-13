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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.ads.parser.AdsStatementParser;
import com.alibaba.druid.sql.dialect.antspark.parser.AntsparkLexer;
import com.alibaba.druid.sql.dialect.antspark.parser.AntsparkStatementParser;
import com.alibaba.druid.sql.dialect.blink.parser.BlinkStatementParser;
import com.alibaba.druid.sql.dialect.clickhouse.parser.ClickhouseStatementParser;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.parser.DB2ExprParser;
import com.alibaba.druid.sql.dialect.db2.parser.DB2Lexer;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.h2.parser.H2ExprParser;
import com.alibaba.druid.sql.dialect.h2.parser.H2Lexer;
import com.alibaba.druid.sql.dialect.h2.parser.H2StatementParser;
import com.alibaba.druid.sql.dialect.hive.parser.HiveExprParser;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
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
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerExprParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.StringUtils;

public class SQLParserUtils {

    public static SQLStatementParser createSQLStatementParser(String sql, DbType dbType) {
        SQLParserFeature[] features;
        if (DbType.odps == dbType || DbType.mysql == dbType) {
            features = new SQLParserFeature[] {SQLParserFeature.KeepComments};
        } else {
            features = new SQLParserFeature[] {};
        }
        return createSQLStatementParser(sql, dbType, features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, DbType dbType, boolean keepComments) {
        SQLParserFeature[] features;
        if (keepComments) {
            features = new SQLParserFeature[] {SQLParserFeature.KeepComments};
        } else {
            features = new SQLParserFeature[] {};
        }

        return createSQLStatementParser(sql, dbType, features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType, SQLParserFeature... features) {
        return createSQLStatementParser(sql, dbType == null ? null : DbType.valueOf(dbType), features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, DbType dbType, SQLParserFeature... features) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case oracle:
            case oceanbase_oracle:
                return new OracleStatementParser(sql, features);
            case mysql:
            case mariadb:
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
            case edb:
                return new PGSQLStatementParser(sql, features);
            case sqlserver:
            case jtds:
                return new SQLServerStatementParser(sql);
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
                return new PrestoStatementParser(sql);
            case ads:
                return new AdsStatementParser(sql);
            case antspark:
                return new AntsparkStatementParser(sql);
            case clickhouse:
                return new ClickhouseStatementParser(sql);
            default:
                return new SQLStatementParser(sql, dbType);
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
            case edb:
                return new PGExprParser(sql, features);
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
                return new PrestoExprParser(sql, features);
            case hive:
                return new HiveExprParser(sql, features);
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
                return new OracleLexer(sql);
            case mysql:
            case mariadb:
                return new MySqlLexer(sql);
            case elastic_search: {
                MySqlLexer lexer = new MySqlLexer(sql);
                lexer.dbType = dbType;
                return lexer;
            }
            case h2:
                return new H2Lexer(sql);
            case postgresql:
            case edb:
                return new PGLexer(sql);
            case db2:
                return new DB2Lexer(sql);
            case odps:
                return new OdpsLexer(sql);
            case phoenix:
                return new PhoenixLexer(sql);
            case presto:
                return new PrestoLexer(sql);
            case antspark:
                return new AntsparkLexer(sql);
            default:
                return new Lexer(sql, null, dbType);
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
                return new PGSelectQueryBlock();
            case odps:
                return new OdpsSelectQueryBlock();
            case sqlserver:
                return new SQLServerSelectQueryBlock();
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
         for (;;) {
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
        for (;;) {
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
        for (;;) {
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
        for (;;) {
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
        for (;;) {
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
        StringBuffer buf = new StringBuffer(sql.length() + 20);
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
}
