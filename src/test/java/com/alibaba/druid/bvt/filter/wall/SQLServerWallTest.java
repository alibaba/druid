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
    

    public static Test suite() {
        TestSuite suite = new TestSuite(SQLServerWallTest.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(MySqlWallLimitTest.class);
        suite.addTestSuite(MySqlWallLoadDataInfileTest.class);
        suite.addTestSuite(MySqlWallPermitFunctionTest.class);
        suite.addTestSuite(MySqlWallPermitTableTest.class);
        suite.addTestSuite(MySqlWallPermitVariantTest.class);
        suite.addTestSuite(MySqlWallTest.class);
        suite.addTestSuite(OracleWallPermitFunctionTest.class);
        suite.addTestSuite(OracleWallPermitObjectTest.class);
        suite.addTestSuite(OracleWallPermitSchemaTest.class);
        suite.addTestSuite(OracleWallPermitTableTest.class);
        suite.addTestSuite(OracleWallPermitVariantTest.class);
        suite.addTestSuite(OracleWallPermitVariantTest2.class);
        suite.addTestSuite(OracleWallTest.class);
        suite.addTestSuite(SQLServerWallPermitFunctionTest.class);
        suite.addTestSuite(SQLServerWallPermitObjectTest.class);
        suite.addTestSuite(SQLServerWallPermitSchemaTest.class);
        suite.addTestSuite(SQLServerWallPermitTableTest.class);
        suite.addTestSuite(SQLServerWallTest.class);
        suite.addTestSuite(WallDeleteTest.class);
        suite.addTestSuite(WallDeleteWhereTest.class);
        suite.addTestSuite(WallDeleteWhereTest1.class);
        suite.addTestSuite(WallDropTest.class);
        suite.addTestSuite(WallDropTest1.class);
        suite.addTestSuite(WallHavingTest.class);
        suite.addTestSuite(WallInsertTest.class);
        suite.addTestSuite(WallInsertTest2.class);
        suite.addTestSuite(WallLineCommentTest.class);
        suite.addTestSuite(WallMultiLineCommentTest.class);
        suite.addTestSuite(WallMultiStatementTest.class);
        suite.addTestSuite(WallReadOnlyTest.class);
        suite.addTestSuite(WallSelectIntoTest.class);
        suite.addTestSuite(WallSelectIntoTest1.class);
        suite.addTestSuite(WallSelectWhereTest.class);
        suite.addTestSuite(WallSelectWhereTest0.class);
        suite.addTestSuite(WallSelectWhereTest1.class);
        suite.addTestSuite(WallSelectWhereTest2.class);
        suite.addTestSuite(WallSelectWhereTest3.class);
        suite.addTestSuite(WallSelectWhereTest4.class);
        suite.addTestSuite(WallSelectWhereTest5.class);
        suite.addTestSuite(WallTruncateTest.class);
        suite.addTestSuite(WallTruncateTest1.class);
        suite.addTestSuite(WallUnionTest.class);
        suite.addTestSuite(WallUnionTest2.class);
        suite.addTestSuite(WallUnionTest3.class);
        suite.addTestSuite(WallUnionTest4.class);
        suite.addTestSuite(WallUpdateTest.class);
        suite.addTestSuite(WallUpdateTest1.class);
        suite.addTestSuite(WallUpdateTest2.class);
        suite.addTestSuite(WallUpdateTest3.class);
        suite.addTestSuite(WallUpdateTest4.class);
        suite.addTestSuite(WallUpdateWhereTest.class);
        suite.addTestSuite(WallVisitorUtilsTest.class);
        //$JUnit-END$
        return suite;
    }
}