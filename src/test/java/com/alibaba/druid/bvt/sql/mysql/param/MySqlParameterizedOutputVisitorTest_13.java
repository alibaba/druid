package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_13 extends MySQLParameterizedTest {
    public void test_for_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "select * from `user_0000` `user`";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        String expected = "SELECT *\n" +
                "FROM user `user`";
        assertEquals(expected, psql);

        paramaterizeAST(sql, "SELECT *\n" +
                "FROM `user_0000` `user`");
    }
}
