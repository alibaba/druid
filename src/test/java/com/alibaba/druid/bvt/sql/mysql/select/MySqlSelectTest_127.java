package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_127 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/*+engine=mpp*/SELECT min(pay_byr_rate_90d) FROM (/*+engine=mpp*/SELECT pay_byr_rate_90d FROM caspian.ads_itm_hpcj_all_df WHERE item_pools_tags = '1116' AND pay_byr_rate_90d >= 0 ORDER BY pay_byr_rate_90d DESC LIMIT 49) LIMIT 500";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=mpp*/\n" +
                "SELECT min(pay_byr_rate_90d)\n" +
                "FROM (\n" +
                "\t/*+engine=mpp*/\n" +
                "\tSELECT pay_byr_rate_90d\n" +
                "\tFROM caspian.ads_itm_hpcj_all_df\n" +
                "\tWHERE item_pools_tags = '1116'\n" +
                "\t\tAND pay_byr_rate_90d >= 0\n" +
                "\tORDER BY pay_byr_rate_90d DESC\n" +
                "\tLIMIT 49\n" +
                ")\n" +
                "LIMIT 500", stmt.toString());
    }


}