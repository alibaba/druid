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

import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;

/**
 * @author wenshao
 */
public class WallCommentTest extends TestCase {

    public void testORACLE() throws Exception {
        String sql = "SELECT F1, F2 FROM ABC --test";

        OracleWallProvider provider = new OracleWallProvider();
        Assert.assertFalse(provider.checkValid(sql));

        Assert.assertEquals(1, provider.getCommentDenyStat().getDenyCount());
    }
    
    public void testmysql() throws Exception {
        String sql = "SELECT F1, F2 FROM ABC --test";

        MySqlWallProvider provider = new MySqlWallProvider();
        Assert.assertFalse(provider.checkValid(sql));

        Assert.assertEquals(1, provider.getCommentDenyStat().getDenyCount());
    }
    
    public void testsqlserver() throws Exception {
        String sql = "SELECT F1, F2 FROM ABC --test";

        SQLServerWallProvider provider = new SQLServerWallProvider();
        Assert.assertFalse(provider.checkValid(sql));

        Assert.assertEquals(1, provider.getCommentDenyStat().getDenyCount());
    }
}
