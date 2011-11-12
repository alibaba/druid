package com.alibaba.druid.sql.test;

import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SqlServerOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;


public class TestUtils {

    public static String outputOracle(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(out);
    
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
    
        return out.toString();
    }
    
    public static String outputSqlServer(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        SqlServerOutputVisitor visitor = new SqlServerOutputVisitor(out);
        
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        
        return out.toString();
    }

    public static String outputOracle(SQLStatement... stmtList) {
        return outputOracle(Arrays.asList(stmtList));
    }
    
    public static String outputSqlServer(SQLStatement... stmtList) {
        return outputSqlServer(Arrays.asList(stmtList));
    }
    
    public static String output(SQLStatement... stmtList) {
        return output(Arrays.asList(stmtList));
    }
    
    public static String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = new SQLASTOutputVisitor(out);
    
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
    
        return out.toString();
    }
}
