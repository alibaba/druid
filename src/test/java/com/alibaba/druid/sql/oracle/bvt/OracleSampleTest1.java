package com.alibaba.druid.sql.oracle.bvt;

import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

public class OracleSampleTest1  extends TestCase {
    public void test_0 () throws Exception {
        
    }

    private void output(List<SQLStatement> stmtList) {
        for (SQLStatement stmt : stmtList) {
            stmt.accept(new MySqlOutputVisitor(System.out));
            System.out.println(";");
            System.out.println();
        }
    }
    
}
