package com.alibaba.druid.bvt.console;

import com.alibaba.druid.support.console.OptionParseException;
import com.alibaba.druid.support.console.Option;

import junit.framework.TestCase;
import junit.framework.Assert;

public class OptionTest extends TestCase {


    public void test_parseOptions() throws Exception {
        String[] cmdArray = {"-sql","-ds", "200"};
        Option opt = Option.parseOptions(cmdArray);
        Assert.assertNotNull(opt);
        Assert.assertTrue(opt.printSqlData());
        Assert.assertTrue(opt.printDataSourceData());
        Assert.assertFalse(opt.printActiveConn());
        Assert.assertEquals(opt.getVmid(), 200);

        cmdArray = new String[] {"-act", "738"};
        opt = Option.parseOptions(cmdArray);
        Assert.assertNotNull(opt);
        Assert.assertFalse(opt.printSqlData());
        Assert.assertFalse(opt.printDataSourceData());
        Assert.assertTrue(opt.printActiveConn());
        Assert.assertEquals(opt.getStyle(), Option.PrintStyle.HORIZONTAL);
        Assert.assertEquals(opt.getVmid(), 738);

        cmdArray = new String[] {"-ds", "-s2", "1319"};
        opt = Option.parseOptions(cmdArray);
        Assert.assertNotNull(opt);
        Assert.assertFalse(opt.printSqlData());
        Assert.assertTrue(opt.printDataSourceData());
        Assert.assertFalse(opt.printActiveConn());
        Assert.assertEquals(opt.getStyle(), Option.PrintStyle.VERTICAL);
        Assert.assertEquals(opt.getVmid(), 1319);
        
        cmdArray = new String[] {"-sql","-id","5","200"};
        opt = Option.parseOptions(cmdArray);
        Assert.assertNotNull(opt);
        Assert.assertEquals(opt.getId(), 5);
        Assert.assertEquals(opt.getVmid(), 200);



        //not enough arguments
        cmdArray = new String[] {};
        try {
            opt = Option.parseOptions(cmdArray);
        } catch (OptionParseException e) {
            System.out.println(e.getMessage());
            Assert.assertNotNull(e);
        }

        //need vmid
        cmdArray = new String[] {"-ds"};
        try {
            opt = Option.parseOptions(cmdArray);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertNotNull(e);
        }
    }


    public void test_printHelp() throws Exception {
        Option.printHelp();
    }
}
