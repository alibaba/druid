package com.alibaba.druid.sql.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.util.JdbcUtils;

public class PerfTest extends TestCase {

    public void test_perf() throws Exception {
        for (int i = 0; i < 10; ++i) {
            // perf("SELECT * FROM my_table WHERE TRUNC(SYSDATE) = DATE '2002-10-03';");
            //perf("SELECT * FROM T");
            perf(loadSql("bvt/parser/oracle-23.txt"));
        }
    }

    String loadSql(String resource) throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        Reader reader = new InputStreamReader(is, "UTF-8");
        String input = JdbcUtils.read(reader);
        JdbcUtils.close(reader);
        String[] items = input.split("---------------------------");
        String sql = items[1].trim();

        return sql;
    }

    void perf(String sql) {
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; ++i) {
            merge(sql);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println(millis);
    }

    private String merge(String sql) {
        StringBuilder out = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(out);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
            visitor.println();
        }

        return out.toString();
    }

}
