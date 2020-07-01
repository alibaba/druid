package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import junit.framework.TestCase;

/**
 * Created by wenshao on 13/08/2017.
 */
public class ShardingUnwrapTest extends TestCase {
    SQLASTOutputVisitor visitor = new SQLASTOutputVisitor(new StringBuffer());

    public void test_sharding_unwrap() throws Exception {

        assertEquals("t_like_count", visitor.unwrapShardingTable("t_like_count0057"));
        assertEquals("t_like_count", visitor.unwrapShardingTable("`t_like_count0057`"));
        assertEquals("t_like_count", visitor.unwrapShardingTable("\"t_like_count0057\""));
    }

    public void test_sharding_unwrap_2() throws Exception {
        assertEquals("t_like_count", visitor.unwrapShardingTable("t_like_count_0057"));
        assertEquals("t_like_count", visitor.unwrapShardingTable("`t_like_count_0057`"));
        assertEquals("t_like_count", visitor.unwrapShardingTable("\"t_like_count_0057\""));
    }

    public void test_sharding_unwrap_3() throws Exception {
        assertEquals("fc_sms", visitor.unwrapShardingTable("fc_sms_0011_201704"));
    }

    public void test_sharding_unwrap_4() throws Exception {
        assertEquals("ads_tb_sycm_eff_slr_itm_1d_s015_p", visitor.unwrapShardingTable("ads_tb_sycm_eff_slr_itm_1d_s015_p033"));
    }

    public void test_sharding_unwrap_5() throws Exception {
        assertEquals("t", visitor.unwrapShardingTable("t_00"));
        assertEquals("t", visitor.unwrapShardingTable("t_1"));
    }
    //
}
