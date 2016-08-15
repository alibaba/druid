package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest4 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "from (select * from xxx) a"
                + "\n -- comment_0" //
                + "\ninsert overwrite table b partition(ds='20150711')" //
                + "\nselect f0, f1, f2" //
                + "\nwhere name rlike 'xxxx'"
                + "\n -- comment_1" //
                + "\ninsert overwrite table c partition(ds='20150711')" //
                + "\nselect f0, f1, f2" //
                + "\nwhere name rlike 'kk';"
                ;//
        Assert.assertEquals("FROM (" //
                + "\n\tSELECT *"//
                + "\n\tFROM xxx"//
                + "\n) a" //
                + "\n-- comment_0"//
                + "\nINSERT OVERWRITE TABLE b PARTITION (ds='20150711')"//
                + "\nSELECT f0"//
                + "\n\t, f1"//
                + "\n\t, f2"//
                + "\nWHERE name RLIKE 'xxxx'" //
                + "\n-- comment_1"//
                + "\nINSERT OVERWRITE TABLE c PARTITION (ds='20150711')"//
                + "\nSELECT f0"//
                + "\n\t, f1"//
                + "\n\t, f2"//
                + "\nWHERE name RLIKE 'kk';", SQLUtils.formatOdps(sql));
    }

}
