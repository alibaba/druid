package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerExprParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.util.JdbcUtils;


public class SQLParserUtils {
    public static SQLStatementParser createSQLStatementParser(String sql, String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType)) {
            return new OracleStatementParser(sql);
        }
        
        if (JdbcUtils.MYSQL.equals(dbType)) {
            return new MySqlStatementParser(sql);
        }
        
        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return new PGSQLStatementParser(sql);
        }
        
        if (JdbcUtils.SQL_SERVER.equals(dbType)) {
            return new SQLServerStatementParser(sql);
        }
        
        return new SQLStatementParser(sql);
    }
    
    public static SQLExprParser createExprParser(String sql, String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType)) {
            return new OracleExprParser(sql);
        }
        
        if (JdbcUtils.MYSQL.equals(dbType)) {
            return new MySqlExprParser(sql);
        }
        
        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return new PGExprParser(sql);
        }
        
        if (JdbcUtils.SQL_SERVER.equals(dbType)) {
            return new SQLServerExprParser(sql);
        }
        
        return new SQLExprParser(sql);
    }
}
