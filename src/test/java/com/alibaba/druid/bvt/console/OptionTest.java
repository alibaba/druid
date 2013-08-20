package com.alibaba.druid.bvt.console;

import org.junit.Assert;
import junit.framework.TestCase;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.alibaba.druid.support.console.Option;
import com.alibaba.druid.support.console.OptionParseException;

public class OptionTest extends TestCase {


    public void test_parseOptions() throws Exception {
        String[] cmdArray = {"-sql","-ds", "200"};
        Option opt = Option.parseOptions(cmdArray);
        Assert.assertNotNull(opt);
        Assert.assertTrue(opt.printSqlData());
        Assert.assertTrue(opt.printDataSourceData());
        Assert.assertFalse(opt.printActiveConn());
        Assert.assertEquals(opt.getPid(), 200);

        cmdArray = new String[] {"-act", "738"};
        opt = Option.parseOptions(cmdArray);
        Assert.assertNotNull(opt);
        Assert.assertFalse(opt.printSqlData());
        Assert.assertFalse(opt.printDataSourceData());
        Assert.assertTrue(opt.printActiveConn());
        Assert.assertEquals(opt.getPid(), 738);

        cmdArray = new String[] {"-ds", "-detail", "1319"};
        opt = Option.parseOptions(cmdArray);
        Assert.assertNotNull(opt);
        Assert.assertFalse(opt.printSqlData());
        Assert.assertTrue(opt.printDataSourceData());
        Assert.assertFalse(opt.printActiveConn());
        Assert.assertTrue(opt.isDetailPrint());
        Assert.assertEquals(opt.getPid(), 1319);
        
        cmdArray = new String[] {"-sql","-id","5","200"};
        opt = Option.parseOptions(cmdArray);
        Assert.assertNotNull(opt);
        Assert.assertEquals(opt.getId(), 5);
        Assert.assertEquals(opt.getPid(), 200); 

		cmdArray = new String[] {"-sql","-id","5","200", "3"};
        opt = Option.parseOptions(cmdArray);
        Assert.assertNotNull(opt);
        Assert.assertEquals(opt.getId(), 5);
        Assert.assertEquals(opt.getPid(), 200);
        Assert.assertEquals(opt.getInterval(), 3);


        cmdArray = new String[] {"-ds","-id","5", "-detail", "200", "3"};
        opt = Option.parseOptions(cmdArray);
        Assert.assertNotNull(opt);
        Assert.assertEquals(opt.getId(), 5);
        Assert.assertEquals(opt.getPid(), 200);
        Assert.assertEquals(opt.getInterval(), 3);
        Assert.assertEquals(opt.isDetailPrint(), true);

        //not enough arguments
        cmdArray = new String[] {};
        try {
            opt = Option.parseOptions(cmdArray);
        } catch (OptionParseException e) {
            System.out.println(e.getMessage());
            Assert.assertNotNull(e);
        }

        //need pid
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

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(OptionTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}
}
