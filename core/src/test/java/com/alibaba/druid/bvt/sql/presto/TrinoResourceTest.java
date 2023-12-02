package com.alibaba.druid.bvt.sql.presto;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TrinoResourceTest {
    @Test
    public void test_1() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q01.sql");
    }

    @Test
    public void test_6() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q06.sql");
    }

    @Test
    public void test_13() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q13.sql");
    }

    @Test
    public void test_21() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q21.sql");
    }

    @Test
    public void test_23() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q23_1.sql");
    }

    @Test
    public void test_23_2() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q23_2.sql");
    }

    @Test
    public void test_24() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q24_1.sql");
    }

    @Test
    public void test_24_2() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q24_2.sql");
    }

    @Test
    public void test_30() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q30.sql");
    }

    @Test
    public void test_32() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q32.sql");
    }

    @Test
    public void test_34() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q34.sql");
    }

    @Test
    public void test_39_2() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q39_2.sql");
    }

    @Test
    public void test_40() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q40.sql");
    }

    @Test
    public void test_44() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q44.sql");
    }

    @Test
    public void test_47() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q47.sql");
    }

    @Test
    public void test_48() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q48.sql");
    }

    @Test
    public void test_53() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q53.sql");
    }

    @Test
    public void test_57() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q57.sql");
    }

    @Test
    public void test_58() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q58.sql");
    }

    @Test
    public void test_63() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q63.sql");
    }

    @Test
    public void test_75() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q75.sql");
    }

    @Test
    public void test_81() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q81.sql");
    }

    @Test
    public void test_83() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q83.sql");
    }

    @Test
    public void test_85() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q85.sql");
    }

    @Test
    public void test_89() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q89.sql");
    }

    @Test
    public void test_92() throws Exception {
        exec_test("bvt/parser/trino/tpcds/q92.sql");
    }

    public void exec_test(String resource) throws Exception {
//        System.out.println(resource);
        InputStream is = null;

        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        Reader reader = new InputStreamReader(is, "UTF-8");
        String input = Utils.read(reader);
        JdbcUtils.close(reader);
        String[] items = input.split("---------------------------");
        String sql = items[0].trim();
        String expect = null;

        if (items.length > 1) {
            expect = items[1].trim();
            if (expect != null) {
                expect = expect.replaceAll("\\r\\n", "\n");
            }
        }

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.trino);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = new SchemaStatVisitor();
        stmt.accept(visitor);

        if (expect != null && !expect.isEmpty()) {
            assertEquals(expect, stmt.toString());
        }

        System.out.println(sql);
//        System.out.println(stmt.toString());
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//
//        System.out.println();
//        System.out.println("---------------------------");
        System.out.println(SQLUtils.toSQLString(stmt, DbType.trino));
    }
}
