package com.alibaba.druid.bvt.sql.mysql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;

public class SQLUtilsTest extends TestCase {
    public void test_format() throws Exception {
        String formattedSql = SQLUtils.format("select * from t where id = ?", JdbcConstants.MYSQL,
                Arrays.<Object>asList("abc"));
        Assert.assertEquals("SELECT *" + //
                "\nFROM t" + //
                "\nWHERE id = 'abc'", formattedSql);
    }

    public void test_format_0() throws Exception {
        String sql = "select \"%\"'温'\"%\" FROM dual;";
        String formattedSql = SQLUtils.formatMySql(sql);
        Assert.assertEquals("SELECT '%温%'\n" +
                "FROM dual;", formattedSql);
    }

    public void test_format_1() throws Exception {
        String sql = "select * from t where tname LIKE \"%\"'温'\"%\"";
        String formattedSql = SQLUtils.formatMySql(sql);
        Assert.assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE tname LIKE '%温%'", formattedSql);
    }

    public void test_format_2() throws Exception {
        String sql = "begin\n"// 
                + " if (a=10) then\n" + " null;\n" + " else\n" + " null;\n" + " end if;\n" + "end;";
        Assert.assertEquals("BEGIN"
                + "\n\tIF a = 10 THEN"
                + "\n\t\tNULL;"
                + "\n\tELSE"
                + "\n\t\tNULL;"
                + "\n\tEND IF;"
                + "\nEND;", SQLUtils.formatOracle(sql));
    }

    public void test_format_3() throws Exception {
        String sql = "select lottery_notice_issue,lottery_notice_date,lottery_notice_result from tb_lottery_notice where lottery_type_id=8 and lottery_notice_issue<=2014066 UNION ALL SELECT NULL, NULL, NULL, NULL, NULL, NULL# and lottery_notice_issue>=2014062 order by lottery_notice_issue desc";
        String formattedSql = SQLUtils.formatMySql(sql);
        String expected = "SELECT lottery_notice_issue, lottery_notice_date, lottery_notice_result\n" +
                "FROM tb_lottery_notice\n" +
                "WHERE lottery_type_id = 8\n" +
                "\tAND lottery_notice_issue <= 2014066\n" +
                "UNION ALL\n" +
                "SELECT NULL, NULL, NULL, NULL, NULL\n" +
                "\t, NULL# and lottery_notice_issue>=2014062 order by lottery_notice_issue desc";
        Assert.assertEquals(expected, formattedSql);
    }

    public void testAcceptFunctionTest() {
        List<SQLMethodInvokeExpr> functions = new ArrayList<>();
        SQLUtils.acceptFunction(
                "select count(*) from t",
                DbType.odps,
                e -> functions.add(e),
                e -> true
        );
        assertEquals(1, functions.size());
    }

    public void testAcceptFunctionTest_1() {
        List<SQLMethodInvokeExpr> functions = new ArrayList<>();
        SQLUtils.acceptAggregateFunction(
                "select count(*) from t",
                DbType.odps,
                e -> functions.add(e),
                e -> true
        );
        assertEquals(1, functions.size());
    }

    public void testAcceptFunctionTest_pg() {
        List<SQLMethodInvokeExpr> functions = new ArrayList<>();
        SQLUtils.acceptFunction(
                "select count(*) from t",
                DbType.postgresql,
                e -> functions.add(e),
                e -> true
        );
        assertEquals(1, functions.size());
    }

    public void testAcceptFunctionTest_pg_1() {
        List<SQLMethodInvokeExpr> functions = new ArrayList<>();
        SQLUtils.acceptAggregateFunction(
                "select count(*) from t",
                DbType.postgresql,
                e -> functions.add(e),
                e -> true
        );
        assertEquals(1, functions.size());
    }

    public void testAcceptFunctionTest_oracle() {
        List<SQLMethodInvokeExpr> functions = new ArrayList<>();
        SQLUtils.acceptFunction(
                "select count(*) from t",
                DbType.oracle,
                e -> functions.add(e),
                e -> true
        );
        assertEquals(1, functions.size());
    }

    public void testAcceptFunctionTest_oracle_1() {
        List<SQLMethodInvokeExpr> functions = new ArrayList<>();
        SQLUtils.acceptAggregateFunction(
                "select count(*) from t",
                DbType.oracle,
                e -> functions.add(e),
                e -> true
        );
        assertEquals(1, functions.size());
    }

    public void testAcceptFunctionTest_ck() {
        List<SQLMethodInvokeExpr> functions = new ArrayList<>();
        SQLUtils.acceptFunction(
                "select count(*) from t",
                DbType.clickhouse,
                e -> functions.add(e),
                e -> true
        );
        assertEquals(1, functions.size());
    }

    public void testAcceptFunctionTest_ck_1() {
        List<SQLMethodInvokeExpr> functions = new ArrayList<>();
        SQLUtils.acceptAggregateFunction(
                "select count(*) from t",
                DbType.clickhouse,
                e -> functions.add(e),
                e -> true
        );
        assertEquals(1, functions.size());
    }
}
