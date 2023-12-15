package com.alibaba.druid.bvt.sql.eval;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;
import org.junit.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class SQLEvalVisitorUtilsTest extends TestCase {
    public void test_instance() throws Exception {
        new SQLEvalVisitorUtils();
    }

    public void test_evalExpr() throws Exception {
        Assert.assertEquals(5, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", 2, 3));
        Assert.assertEquals(6, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? * ?", 2, 3));
        Assert.assertEquals(-1, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", 2, 3));
        Assert.assertEquals(2, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? / ?", 6, 3));
    }

    public void test_evalExpr_2() throws Exception {
        Assert.assertEquals(5, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(2, 3)));
        Assert.assertEquals(6, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? * ?", Arrays.<Object>asList(2, 3)));
        Assert.assertEquals(-1, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(2, 3)));
        Assert.assertEquals(2, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? / ?", Arrays.<Object>asList(6, 3)));
    }

    public void test_evalExpr_3() throws Exception {
        Assert.assertEquals(5.0f, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", "2.0", 3));
        Assert.assertEquals(6.0f, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? * ?", "2.0", 3));
        Assert.assertEquals(-1.0f, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", "2.0", 3));
        Assert.assertEquals(2.0f, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? / ?", "6.0", 3));
    }

    public void test_add() throws Exception {
        Assert.assertEquals(5, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(2, 3)));
        Assert.assertEquals(6, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(3, 3)));
        Assert.assertEquals(7, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(4, 3)));
    }

    public void test_add_1() throws Exception {
        Assert.assertEquals(5.0, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(2.0, 3)));
        Assert.assertEquals(6.0, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(3.0, 3)));
        Assert.assertEquals(7.0, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(4.0, 3)));
    }

    public void test_add_2() throws Exception {
        Assert.assertEquals(5.1, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(2.1, 3)));
        Assert.assertEquals(6.2, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(3.2, 3)));
        Assert.assertEquals(7.3, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(4.3, 3)));
    }

    public void test_add_3() throws Exception {
        Assert.assertEquals(5.1D,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(2.1D, 3)));
        Assert.assertEquals(6.2D,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(3.2D, 3)));
        Assert.assertEquals(7.3D,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(4.3D, 3)));
    }

    public void test_add_4() throws Exception {
        Assert.assertEquals(5.0, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(2, 3.0)));
        Assert.assertEquals(6.0, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(3, 3.0)));
        Assert.assertEquals(7.0, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?", Arrays.<Object>asList(4, 3.0)));
    }

    public void test_add_5() throws Exception {
        Assert.assertEquals(new BigInteger("5"),
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?",
                        Arrays.<Object>asList(2, new BigInteger("3"))));
        Assert.assertEquals(new BigInteger("6"),
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?",
                        Arrays.<Object>asList(3, new BigInteger("3"))));
        Assert.assertEquals(new BigInteger("7"),
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?",
                        Arrays.<Object>asList(4, new BigInteger("3"))));
    }

    public void test_add_6() throws Exception {
        Assert.assertEquals(new BigDecimal("5"),
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?",
                        Arrays.<Object>asList(2, new BigDecimal("3"))));
        Assert.assertEquals(new BigDecimal("6"),
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?",
                        Arrays.<Object>asList(3, new BigDecimal("3"))));
        Assert.assertEquals(new BigDecimal("7"),
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? + ?",
                        Arrays.<Object>asList(4, new BigDecimal("3"))));
    }

    public void test_sub() throws Exception {
        Assert.assertEquals(-1, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(2, 3)));
        Assert.assertEquals(0, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(3, 3)));
        Assert.assertEquals(1, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(4, 3)));
    }

    public void test_sub_1() throws Exception {
        Assert.assertEquals(-1.0,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(2.0, 3)));
        Assert.assertEquals(0.0, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(3.0, 3)));
        Assert.assertEquals(1.0, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(4.0, 3)));
    }

    public void test_sub_2() throws Exception {
        Assert.assertEquals(2.1 - 3,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(2.1, 3)));
        Assert.assertEquals(3.2 - 3,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(3.2, 3)));
        Assert.assertEquals(4.3 - 3,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(4.3, 3)));
    }

    public void test_sub_3() throws Exception {
        Assert.assertEquals(2.1D - 3,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(2.1D, 3)));
        Assert.assertEquals(3.2D - 3,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(3.2D, 3)));
        Assert.assertEquals(4.3D - 3,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? - ?", Arrays.<Object>asList(4.3D, 3)));
    }

    public void test_lt() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? < ?", Arrays.<Object>asList(2, 3)));
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? < ?", Arrays.<Object>asList(3, 3)));
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? < ?", Arrays.<Object>asList(4, 3)));
    }

    public void test_lt_1() throws Exception {
        Assert.assertEquals(true,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? < ?", Arrays.<Object>asList(2.0, 3)));
        Assert.assertEquals(false,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? < ?", Arrays.<Object>asList(3.0, 3)));
        Assert.assertEquals(false,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? < ?", Arrays.<Object>asList(4.0, 3)));
    }

    public void test_ltEq() throws Exception {
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? <= ?", Arrays.<Object>asList(2, 3)));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? <= ?", Arrays.<Object>asList(3, 3)));
        Assert.assertEquals(false,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? <= ?", Arrays.<Object>asList(4, 3)));
    }

    public void test_ltEq_1() throws Exception {
        Assert.assertEquals(true,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? <= ?", Arrays.<Object>asList(2.0, 3)));
        Assert.assertEquals(true,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? <= ?", Arrays.<Object>asList(3.0, 3)));
        Assert.assertEquals(false,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? <= ?", Arrays.<Object>asList(4.0, 3)));
    }

    public void test_gt() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? > ?", Arrays.<Object>asList(2, 3)));
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? > ?", Arrays.<Object>asList(3, 3)));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? > ?", Arrays.<Object>asList(4, 3)));
    }

    public void test_gtEq() throws Exception {
        Assert.assertEquals(false,
                SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? >= ?", Arrays.<Object>asList(2, 3)));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? >= ?", Arrays.<Object>asList(3, 3)));
        Assert.assertEquals(true, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "? >= ?", Arrays.<Object>asList(4, 3)));
    }

    public void test_binary() throws Exception {
        Assert.assertEquals(1L, SQLEvalVisitorUtils.eval(JdbcUtils.MYSQL, new SQLBinaryExpr("01"), new ArrayList<Object>()));
        Assert.assertEquals(2L, SQLEvalVisitorUtils.eval(JdbcUtils.MYSQL, new SQLBinaryExpr("10"), new ArrayList<Object>()));
        Assert.assertEquals(3L, SQLEvalVisitorUtils.eval(JdbcUtils.MYSQL, new SQLBinaryExpr("11"), new ArrayList<Object>()));
        Assert.assertEquals(4L, SQLEvalVisitorUtils.eval(JdbcUtils.MYSQL, new SQLBinaryExpr("100"), new ArrayList<Object>()));
        Assert.assertEquals(new BigInteger("36893488147419103231"), SQLEvalVisitorUtils.eval(JdbcUtils.MYSQL, new SQLBinaryExpr("11111111111111111111111111111111111111111111111111111111111111111"), new ArrayList<Object>()));
    }

    public void test_LessThanOrGreater() throws Exception {
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "1<>1"));
        Assert.assertEquals(false, SQLEvalVisitorUtils.evalExpr(JdbcUtils.MYSQL, "?<>?", Arrays.<Object>asList(1, 1)));
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_lpad">MySQL lapd</a>
     * @throws Exception
     */
    public void test_string_lpad() throws Exception {
        String sql = "LPAD('hi',4,'??')";
        Object val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("??hi", val);
        sql = "LPAD('hi',1,'??')";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("h", val);
        sql = "LPAD('hi',4,'abc')";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("abhi", val);

        sql = "LPAD('hi',7,'abc')";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("abcabhi", val);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_rpad">MySQL rapd</a>
     * @throws Exception
     */
    public void test_string_rpad() throws Exception {
        String sql = "RPAD('hi',5,'?')";
        Object val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("hi???", val);

        sql = "RPAD('hi',1,'?')";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("h", val);


        sql = "RPAD('hi',5,'ab')";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("hiaba", val);


        sql = "RPAD('hi',7,'abc')";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("hiabcab", val);



    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_right">MySQL right</a>
     * @throws Exception
     */
    public void test_string_right() throws Exception {
        String sql = "RIGHT('foobarbar', 4)";
        Object val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("rbar", val);
        sql = "RIGHT('ar', 4)";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("ar", val);

    }
    public void test_string_replace() throws Exception {
        String sql = "replace('abcb','b','B')";
        Object val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("aBcB", val);
        sql = "replace('abc',null,'B')";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("abc", val);
        sql = "replace('abc','b',null)";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("abc", val);

        sql = "replace('abcbbccbc','bc','QWER')";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("aQWERbQWERcQWER", val);

        sql = "replace('www.mysql.com','w','Ww')";
        val = SQLEvalVisitorUtils.evalExpr(DbType.mysql, sql);
        System.out.println(sql + ": " + val);
        assertEquals("WwWwWw.mysql.com", val);
    }
}
