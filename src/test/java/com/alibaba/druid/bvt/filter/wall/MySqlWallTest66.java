/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest66 extends TestCase {

    public void test_true() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setSchemaCheck(true);

        Assert.assertTrue(provider.checkValid(//
        "SELECT LOGFILE_GROUP_NAME, FILE_NAME, TOTAL_EXTENTS, INITIAL_SIZE, ENGINE, EXTRA " + //
                "FROM INFORMATION_SCHEMA.FILES WHERE FILE_TYPE = 'UNDO LOG' AND FILE_NAME IS NOT NULL " + //
                "AND LOGFILE_GROUP_NAME IN (" + //
                "   SELECT DISTINCT LOGFILE_GROUP_NAME FROM INFORMATION_SCHEMA.FILES " + //
                "       WHERE FILE_TYPE = 'DATAFILE' " + //
                "           AND TABLESPACE_NAME IN (" + //
                "               SELECT DISTINCT TABLESPACE_NAME FROM INFORMATION_SCHEMA.PARTITIONS " + //
                "               WHERE TABLE_SCHEMA IN ('stat'))" + //
                ") " + //
                "GROUP BY LOGFILE_GROUP_NAME, FILE_NAME, ENGINE " + //
                "ORDER BY LOGFILE_GROUP_NAME"));

        Assert.assertEquals(2, provider.getTableStats().size());
    }

}
