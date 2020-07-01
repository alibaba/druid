package com.alibaba.druid.bvt.sql.odps.udf;

import org.junit.Assert;

import com.alibaba.druid.support.opds.udf.ExportSelectListColumns;

import junit.framework.TestCase;

public class ExportSelectListColumnsTest2 extends TestCase {

    private ExportSelectListColumns udf = new ExportSelectListColumns();

    public void test_0() throws Exception {
        String sql = "SELECT * "
                + "\n FROM fund_base_cv_ad_auction_ocr_pv_tfs a"
                + "\n WHERE ds=20150819";

        String text = udf.evaluate(sql, "odps");
        
        Assert.assertEquals("fund_base_cv_ad_auction_ocr_pv_tfs.*", text);


    }
}
