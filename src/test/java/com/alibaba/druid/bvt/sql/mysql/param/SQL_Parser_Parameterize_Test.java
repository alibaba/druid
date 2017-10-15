package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class SQL_Parser_Parameterize_Test extends TestCase {
    public void test_parameterized() throws Exception {
        final String dbType = JdbcConstants.MYSQL;

        List<Object> outParameters = new ArrayList<Object>();
        String sql = "select * from t where id = 101 and age = 102 or name = 'wenshao'";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType, outParameters);
        assertEquals("SELECT *\n" +
                "FROM t\n" +
                "WHERE id = ?\n" +
                "\tAND age = ?\n" +
                "\tOR name = ?", psql);

        assertEquals(3, outParameters.size());
        assertEquals(101, outParameters.get(0));
        assertEquals(102, outParameters.get(1));
        assertEquals("wenshao", outParameters.get(2));
    }
}
