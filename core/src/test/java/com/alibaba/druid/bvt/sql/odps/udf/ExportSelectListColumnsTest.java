package com.alibaba.druid.bvt.sql.odps.udf;

import com.alibaba.druid.support.opds.udf.ExportSelectListColumns;
import org.junit.jupiter.api.Test;

public class ExportSelectListColumnsTest {
    private ExportSelectListColumns udf = new ExportSelectListColumns();

    @Test
    public void test_0() throws Exception {
        String sql = "SELECT fund_base_cv_creative_ocr_judge_control_words_1422189630695(a.tfs, b.ocr_text, a.cates)"
                + "\n AS (tfs, control_word, ocr_word, control_type, cates)"
                + "\n FROM fund_base_cv_ad_auction_ocr_pv_tfs a"
                + "\n JOIN fund_base_cv_ad_auction_ocr_pv_tfs_ocr b ON a.tfs = b.tfs"
                + "\n WHERE a.ds=20150819 and b.ds=20150819;";

        System.out.println(udf.evaluate(sql, "odps"));
    }
}
