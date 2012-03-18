package com.alibaba.druid.bvt.filter.wall;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * SQLServerWallTest
 *
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class SQLServerWallTest extends TestCase {

    /**
     * @param name
     */
    public SQLServerWallTest(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void test_stuff() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT @@version"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT 1 — comment"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT /*comment*/1"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("WAITFOR DELAY ’0:0:5′ "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("BULK INSERT mydata FROM ‘c:boot.ini’;"));                  
    }    
}