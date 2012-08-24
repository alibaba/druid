/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.filter.wall;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * AllTests
 *
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class AllWallTests extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllWallTests.class.getName());
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
