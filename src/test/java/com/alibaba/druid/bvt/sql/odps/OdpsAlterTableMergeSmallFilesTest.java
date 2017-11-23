package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class OdpsAlterTableMergeSmallFilesTest extends TestCase {

    public void test_touch() throws Exception {
        String sql = "ALTER TABLE abc_dev.tdl_mytable_xx MERGE SMALLFILES;";
        Assert.assertEquals("ALTER TABLE abc_dev.tdl_mytable_xx MERGE SMALLFILES;", SQLUtils.formatOdps(sql));
    }
}
