package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsAlterTableSetCommentTest extends TestCase {

    public void test_if() throws Exception {
        String sql = "alter table sale_detail set comment 'new coments for table sale_detail';";
        Assert.assertEquals("ALTER TABLE sale_detail" //
                            + "\n\tSET COMMENT 'new coments for table sale_detail';", SQLUtils.formatOdps(sql));
    }
}
