package com.alibaba.druid.sql.performance;

import java.text.NumberFormat;

import junit.framework.TestCase;

import com.alibaba.druid.sql.parser.SQLStatementParser;

public class TestSelectPerformance extends TestCase {
    private final int COUNT = 1000 * 1;
    private String sql = "SELECT F100, F101, F102, F103, F104 FROM T_001 WHERE F100 = ?";

    public void test_simple() throws Exception {
        for (int i = 0; i < 1; ++i) {
            f();
        }
    }

    private void f() throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; ++i) {
            new SQLStatementParser(sql).parseStatementList();
            //stmtList.toString();
        }
        long time = System.currentTimeMillis() - start;
        System.out.println(NumberFormat.getInstance().format(time));
    }
}
