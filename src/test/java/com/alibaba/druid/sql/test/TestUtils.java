package com.alibaba.druid.sql.test;

import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;


public class TestUtils {

    public static String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(out);
    
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
    
        return out.toString();
    }

    public static String output(SQLStatement... stmtList) {
        return output(Arrays.asList(stmtList));
    }
}
