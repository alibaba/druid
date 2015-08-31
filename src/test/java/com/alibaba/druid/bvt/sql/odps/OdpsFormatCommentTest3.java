package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsFormatCommentTest3 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "-- 使用服务模式" //
                + "\nset odps.service.mode=all;" //
                + "\n-- 使用新引擎"//
                + "\nset odps.nvm.enabled=true;"//
                + "\nselect f1 -- aa"//
                + "\nfrom t0;"//
                + "\nselect f2 -- aa"//
                + "\nfrom t1;";//
        Assert.assertEquals("-- 使用服务模式"//
                + "\nSET odps.service.mode = all;"//
                + "\n-- 使用新引擎"//
                + "\nSET odps.nvm.enabled = true;"//
                + "\n" //
                + "\nSELECT f1 -- aa"//
                + "\nFROM t0;"//
                + "\n" //
                + "\nSELECT f2 -- aa"//
                + "\nFROM t1;", SQLUtils.formatOdps(sql));
    }

}
