/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.wall.spi.WallVisitorUtils;

public class LikeTest extends TestCase {

    public void test_isTrue() throws Exception {
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("f1 like '%'")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("f1 like '%%'")));
        Assert.assertEquals(null, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("a1 = b1 AND f1 like '%%'")));
        Assert.assertEquals(Boolean.TRUE, WallVisitorUtils.getValue(SQLUtils.toSQLExpr("a1 = b1 OR f1 like '%%'")));

    }
}
