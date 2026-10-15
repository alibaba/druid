package com.alibaba.druid.bvt.sql.odps.udf;

import com.alibaba.druid.support.opds.udf.ExportSelectListColumns;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExportSelectListColumnsTest2 {
    private ExportSelectListColumns udf = new ExportSelectListColumns();

    @Test
    public void test_0() throws Exception {
        String sql = "SELECT * "
                + "\n FROM fund_base_cv_ad_auction_ocr_pv_tfs a"
                + "\n WHERE ds=20150819";

        String text = udf.evaluate(sql, "odps");

        assertEquals("fund_base_cv_ad_auction_ocr_pv_tfs.*", text);
    }
}
