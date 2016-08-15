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
import com.alibaba.druid.wall.spi.SQLServerWallProvider;

/**
 * SQLServerWallTest
 * 
 * @see
 */
public class SQLServerWallTest_11 extends TestCase {

    public void test_true() throws Exception {
        WallProvider provider = new SQLServerWallProvider();
        provider.getConfig().setCommentAllow(true);

        String sql = "SELECT KL_ArticleContent,KL_ArticleTitle " //
                     + "FROM dbo.KL_Article " //
                     + "WHERE KL_ArticleId =-4731 "//
                     + "UNION ALL "//
                     + "SELECT (SELECT TOP 1 CHAR(58)+CHAR(108)+CHAR(107)+CHAR(103)+CHAR(58)+ISNULL(CAST(name AS NVARCHAR(4000)),CHAR(32))+CHAR(58)+CHAR(109)+CHAR(122)+CHAR(104)+CHAR(58) FROM sys.sql_logins WHERE ISNULL(name,CHAR(32)) NOT IN (SELECT TOP 0 ISNULL(name,CHAR(32)) FROM sys.sql_logins ORDER BY 1) ORDER BY 1),NULL-- ";

        Assert.assertFalse(provider.checkValid(sql));

    }

}
