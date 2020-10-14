package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class MySqlParameterizedOutputVisitorTest_80 extends TestCase {
    public void test1()  {

        String sql = "select ((0='x6') & 31) ^ (ROW(76, 4) NOT IN (ROW(1, 2 ),ROW(3, 4)) );";


        SQLStatement stmt = ParameterizedOutputVisitorUtils.parameterizeOf(sql, DbType.mysql);
        assertEquals("SELECT ((? = ?) & ?) ^ (ROW(?, ?) NOT IN (ROW(?, ?), ROW(?, ?)));", stmt.toString());


        List<Object> outParameters = new ArrayList<Object>();
        SQLStatement stmt2 = ParameterizedOutputVisitorUtils.parameterizeOf(sql, outParameters, DbType.mysql);

        assertEquals("SELECT ((? = ?) & ?) ^ (ROW(?, ?) NOT IN (ROW(?, ?), ROW(?, ?)));", stmt2.toString());

        assertEquals(9, outParameters.size());

        assertEquals(0, outParameters.get(0));
        assertEquals("x6", outParameters.get(1));
        assertEquals(31, outParameters.get(2));
        assertEquals(76, outParameters.get(3));
        assertEquals(4, outParameters.get(4));
        assertEquals(1, outParameters.get(5));
        assertEquals(2, outParameters.get(6));
        assertEquals(3, outParameters.get(7));
        assertEquals(4, outParameters.get(8));
    }

    public void test2()  {

        String sql = "select a from t group by 1 order by 1;";

        SQLStatement stmt = ParameterizedOutputVisitorUtils.parameterizeOf(sql, DbType.mysql);
        assertEquals("SELECT a\n" +
                "FROM t\n" +
                "GROUP BY 1\n" +
                "ORDER BY 1;", stmt.toString());
    }

    public void test3()  {

        String sql = "/*test*/ select * from test ;";

        assertEquals("/*test*/\n" +
                "SELECT *\n" +
                "FROM test;", ParameterizedOutputVisitorUtils.parameterizeOf(sql, DbType.mysql)
                .toString());

        assertEquals("/*test*/\n" +
                "SELECT *\n" +
                "FROM test;", ParameterizedOutputVisitorUtils.parameterizeOf(sql, DbType.oracle)
                .toString());
    }
}
