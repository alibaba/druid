package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OdpsFormatCommentTest28 {
    @Test
    public void test_drop_function() throws Exception {
        String sql = "SELECT pageid, adid FROM pageAds LATERAL VIEW explode(adid_list) adTable AS adid;";
        assertEquals("SELECT pageid, adid\n" +
                "FROM pageAds\n" +
                "\tLATERAL VIEW EXPLODE(adid_list) adTable AS adid;", SQLUtils.formatOdps(sql));
    }
}
