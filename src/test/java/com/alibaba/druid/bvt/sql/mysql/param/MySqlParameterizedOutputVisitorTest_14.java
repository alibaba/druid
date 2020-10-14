package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_14 extends com.alibaba.druid.bvt.sql.mysql.param.MySQLParameterizedTest {
    public void test_for_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "insert into\n" +
                "\t\tt_temp (processId,resultId,gmt_create,gmt_modified,result_content,result_number)\n" +
                "\t\tvalues\n" +
                "\t\t  \n" +
                "\t\t\t('4254cc14-1c83-4eaf-95ae-59438dd0cc17', '5fd20fa9-7659-4f8b-a4c2-2021a48317d8', now(),now(),null,null)";

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        String s = "INSERT INTO t_temp (processId, resultId, gmt_create, gmt_modified, result_content\n" +
                "\t, result_number)\n" +
                "VALUES (?, ?, now(), now(), ?\n" +
                "\t, ?)";
        assertEquals(s, psql);

        paramaterizeAST(sql, "INSERT INTO t_temp (processId, resultId, gmt_create, gmt_modified, result_content\n" +
                "\t, result_number)\n" +
                "VALUES (?, ?, now(), now(), NULL\n" +
                "\t, NULL)");
    }
}
