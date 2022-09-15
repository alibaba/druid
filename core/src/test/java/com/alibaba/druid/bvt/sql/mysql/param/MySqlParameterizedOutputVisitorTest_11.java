package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_11 extends com.alibaba.druid.bvt.sql.mysql.param.MySQLParameterizedTest {
    public void test_for_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "/* 72582af814768580067726386d39b6/0// */ select id,uid from mytable";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        String expected = "SELECT id, uid\n" +
                "FROM mytable";
        assertEquals(expected, psql);

        paramaterizeAST(sql, "/* 72582af814768580067726386d39b6/0// */\n" +
                "SELECT id, uid\n" +
                "FROM mytable");
    }
}
