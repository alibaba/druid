package com.alibaba.druid.sql;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;

public class SQLUtils {

    public static String toMySqlString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new MySqlOutputVisitor(out));

        String sql = out.toString();
        return sql;
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
 
}
