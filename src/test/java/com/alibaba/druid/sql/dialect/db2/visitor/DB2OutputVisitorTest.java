package com.alibaba.druid.sql.dialect.db2.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author abomb4 2022-06-19
 */
public class DB2OutputVisitorTest {

    @Test
    public void test() {

        List<TestCase> testCases = Arrays.asList(
                new TestCase("interval 表达式应该带括号 select",
                        "SELECT A, B, C FROM D WHERE C < CURRENT TIMESTAMP + (10 * 60) SECONDS",
                        "SELECT A, B, C FROM D WHERE C < CURRENT TIMESTAMP + (10 * 60) SECONDS"),
                new TestCase("interval 表达式应该带括号 insert",
                        "INSERT INTO TEST VALUES (1, CURRENT TIMESTAMP + ((10 + 60) SECONDS))",
                        "INSERT INTO TEST VALUES (1, CURRENT TIMESTAMP + (10 + 60) SECONDS)"),
                new TestCase("interval 表达式应该带括号 update",
                        "UPDATE test SET created = CURRENT TIMESTAMP + 10 SECONDS WHERE created < CURRENT TIMESTAMP + (10 * 60) SECONDS",
                        "UPDATE test SET created = CURRENT TIMESTAMP + (10) SECONDS WHERE created < CURRENT TIMESTAMP + (10 * 60) SECONDS")
        );

        for (TestCase testCase : testCases) {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(testCase.origin, DbType.db2);
            StringBuilder builder = new StringBuilder();
            DB2OutputVisitor visitor = new DB2OutputVisitor(builder);
            visitor.setUppCase(false);
            visitor.setPrettyFormat(false);
            visitor.setParameterized(false);
            SQLStatement stmt = parser.parseStatement();
            stmt.accept(visitor);
            String result = builder.toString();
            Assert.assertEquals(testCase.name, testCase.expected, result);
        }
        System.out.println("DB2 Interval test ok.");
    }

    private static class TestCase {
        String name;
        String origin;
        String expected;

        public TestCase(String name, String origin, String expected) {
            this.name = name;
            this.origin = origin;
            this.expected = expected;
        }
    }
}
