package com.alibaba.druid.sql;

import java.util.List;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParseException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

public class SQLUtils {
    private final static Log LOG = LogFactory.getLog(SQLUtils.class);

    public static String toSQLString(SQLObject sqlObject, String dbType) {
        if (JdbcUtils.MYSQL.equals(dbType)) {
            return toMySqlString(sqlObject);
        }
        
        if (JdbcUtils.H2.equals(dbType)) {
            return toMySqlString(sqlObject);
        }

        if (JdbcUtils.ORACLE.equals(dbType)) {
            return toOracleString(sqlObject);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return toPGString(sqlObject);
        }

        return toSQLServerString(sqlObject);
    }

    public static String toSQLString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new SQLASTOutputVisitor(out));

        String sql = out.toString();
        return sql;
    }

    public static String toMySqlString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new MySqlOutputVisitor(out));

        String sql = out.toString();
        return sql;
    }

    public static SQLExpr toMySqlExpr(String sql) {
        return toSQLExpr(sql, JdbcUtils.MYSQL);
    }

    public static String formatMySql(String sql) {
        return format(sql, JdbcUtils.MYSQL);
    }

    public static String formatOracle(String sql) {
        return format(sql, JdbcUtils.ORACLE);
    }

    public static String toOracleString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new OracleOutputVisitor(out));

        String sql = out.toString();
        return sql;
    }

    public static String toPGString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new PGOutputVisitor(out));

        String sql = out.toString();
        return sql;
    }

    public static String toSQLServerString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new SQLServerOutputVisitor(out));

        String sql = out.toString();
        return sql;
    }

    public static String formatPGSql(String sql) {
        return format(sql, JdbcUtils.POSTGRESQL);
    }

    public static SQLExpr toSQLExpr(String sql, String dbType) {
        SQLExprParser parser = SQLParserUtils.createExprParser(sql, dbType);
        SQLExpr expr = parser.expr();

        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("illegal sql expr : " + sql);
        }

        return expr;
    }

    public static List<SQLStatement> toStatementList(String sql, String dbType) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        return parser.parseStatementList();
    }

    public static SQLExpr toSQLExpr(String sql) {
        return toSQLExpr(sql, null);
    }

    public static String format(String sql, String dbType) {
        try {
            List<SQLStatement> statementList = toStatementList(sql, dbType);

            StringBuilder out = new StringBuilder();
            SQLASTOutputVisitor visitor = createFormatOutputVisitor(out, statementList, dbType);

            for (SQLStatement stmt : statementList) {
                stmt.accept(visitor);
            }

            return out.toString();
        } catch (SQLParseException ex) {
            LOG.warn("format error", ex);
            return sql;
        } catch (ParserException ex) {
            LOG.warn("format error", ex);
            return sql;
        }
    }

    public static SQLASTOutputVisitor createFormatOutputVisitor(Appendable out, List<SQLStatement> statementList,
                                                                String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType)) {
            if (statementList.size() == 1) {
                return new OracleOutputVisitor(out, false);
            } else {
                return new OracleOutputVisitor(out, true);
            }
        }

        if (JdbcUtils.MYSQL.equals(dbType)) {
            return new MySqlOutputVisitor(out);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return new PGOutputVisitor(out);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType)) {
            return new SQLServerOutputVisitor(out);
        }

        if (JdbcUtils.H2.equals(dbType)) {
            return new MySqlOutputVisitor(out);
        }

        return new SQLASTOutputVisitor(out);
    }

    public static SchemaStatVisitor createSchemaStatVisitor(List<SQLStatement> statementList, String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType)) {
            if (statementList.size() == 1) {
                return new OracleSchemaStatVisitor();
            } else {
                return new OracleSchemaStatVisitor();
            }
        }

        if (JdbcUtils.MYSQL.equals(dbType)) {
            return new MySqlSchemaStatVisitor();
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return new PGSchemaStatVisitor();
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType)) {
            return new SQLServerSchemaStatVisitor();
        }

        if (JdbcUtils.H2.equals(dbType)) {
            return new MySqlSchemaStatVisitor();
        }

        return new SchemaStatVisitor();
    }

    public static List<SQLStatement> parseStatements(String sql, String dbType) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        if (parser.getLexer().token() != Token.EOF) {
            throw new DruidRuntimeException("syntax error : " + sql);
        }
        return stmtList;
    }
}
