package com.alibaba.druid.bvt.sql.mysql;

import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;

public class CobarHintsTest extends TestCase {
    public void test_0 () throws Exception {
        String sql = "/*!cobar: select,4,ireport.dm_mdm_mem_prod_noeff_sdt0.admin_member_seq=45654723*/ select * from t";
        
        SQLUtils.formatMySql(sql);
    }
}
