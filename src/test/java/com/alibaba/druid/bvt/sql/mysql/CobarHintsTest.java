package com.alibaba.druid.bvt.sql.mysql;

import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcUtils;

public class CobarHintsTest extends TestCase {
    public void test_0 () throws Exception {
        String sql = "/*!cobar: select,4,ireport.dm_mdm_mem_prod_noeff_sdt0.admin_member_seq=45654723*/ " +
        		"select  product_id, noeff_days,total_cnt from (" +
        		"select   product_id," +
        		"             noeff_days," +
        		"             count(*) over()  as total_cnt                        " +
        		"       from   (                   " +
        		"          select   product_id," +
        		"                   noeff_days               " +
        		"                   from ireport.dm_mdm_mem_prod_noeff_sdt0" +
        		"                   where admin_member_seq = 45654723" +
        		") b    Order by       product_id desc  ) a limit 25 offset (1-1)*20";

        String mergedSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.POSTGRESQL);
        System.out.println(mergedSql);
    }
}
