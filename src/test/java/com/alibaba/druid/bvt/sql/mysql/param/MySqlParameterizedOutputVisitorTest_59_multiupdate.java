package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_59_multiupdate extends TestCase {
    final String dbType = JdbcConstants.MYSQL;
    public void test_for_parameterize() throws Exception {

        String sql = "update t_order set salary = 101 where id = 101;update t_order set salary = 102 where id = 102";

        List<Object> params = new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType, params);
        assertEquals("UPDATE t_order\n" +
                "SET salary = ?\n" +
                "WHERE id = ?;", psql);
        assertEquals(2, params.size());
        assertEquals(101, params.get(0));
    }
}
