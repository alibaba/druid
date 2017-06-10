package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest28 extends TestCase {
    public void test_drop_function() throws Exception {
        String sql = "SELECT pageid, adid FROM pageAds LATERAL VIEW explode(adid_list) adTable AS adid;";
        Assert.assertEquals("SELECT pageid, adid\n" +
                "FROM pageAds\n" +
                "\tLATERAL VIEW EXPLODE(adid_list) adTable AS adid;", SQLUtils.formatOdps(sql));
    }   
}
