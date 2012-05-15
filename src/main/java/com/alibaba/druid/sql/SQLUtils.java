package com.alibaba.druid.sql;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcUtils;

public class SQLUtils {

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
        MySqlLexer lexer = new MySqlLexer(sql);
        lexer.nextToken();

        MySqlExprParser parser = new MySqlExprParser(lexer);
        SQLExpr expr = parser.expr();

        if (lexer.token() != Token.EOF) {
            throw new ParserException("illegal sql expr : " + sql);
        }

        return expr;
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
    
    public static SQLExpr toSQLExpr(String sql) {
        Lexer lexer = new Lexer(sql);
        lexer.nextToken();

        SQLExprParser parser = new SQLExprParser(lexer);
        SQLExpr expr = parser.expr();

        if (lexer.token() != Token.EOF) {
            throw new ParserException("illegal sql expr : " + sql);
        }

        return expr;
    }
    
    public static String format(String sql, String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType)) {
            return formatOracle(sql);
        }
        
        if (JdbcUtils.MYSQL.equals(dbType)) {
            return formatMySql(sql);
        }
        
        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return formatPGSql(sql);
        }
        
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> statementList = parser.parseStatementList();

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = createFormatOutputVisitor(out, statementList, dbType);

        for (SQLStatement stmt : statementList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }
    
    public static SQLASTOutputVisitor createFormatOutputVisitor(Appendable out, List<SQLStatement> statementList, String dbType) {
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
        
        return new SQLASTOutputVisitor(out);
    }
}
