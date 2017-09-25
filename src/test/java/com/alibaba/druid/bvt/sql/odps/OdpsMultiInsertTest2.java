package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class OdpsMultiInsertTest2 extends TestCase {
    public void test_for_multi_insert() throws Exception {
        String sql = "from sale_detail\n" +
                "insert overwrite table sale_detail_multi partition (sale_date='2010', region='china' )\n" +
                "select shop_name, customer_id, total_price\n" +
                "insert overwrite table sale_detail_multi partition (sale_date='2011', region='china' )\n" +
                "select shop_name, customer_id, total_price;\n";
        Assert.assertEquals("FROM sale_detail\n" +
                "INSERT OVERWRITE TABLE sale_detail_multi PARTITION (sale_date='2010', region='china')\n" +
                "SELECT shop_name, customer_id, total_price\n" +
                "INSERT OVERWRITE TABLE sale_detail_multi PARTITION (sale_date='2011', region='china')\n" +
                "SELECT shop_name, customer_id, total_price;", SQLUtils.formatOdps(sql));
    }   
}
