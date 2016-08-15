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
public class MySqlWallTest67 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setSchemaCheck(true);

        Assert.assertTrue(provider.checkValid(//
        "SELECT c.table_name, column_name, column_type, is_nullable, column_key" + //
                "   , column_default, extra, collation_name, character_set_name, column_comment " + //
                "FROM information_schema.columns c " + //
                "INNER JOIN (" + //
                "   SELECT table_schema, table_name " + //
                "   FROM information_schema.tables " + //
                "   WHERE LOWER(table_schema) = LOWER('sp5035d3d0b2d4a')" + //
                ") t ON t.table_name COLLATE utf8_bin = c.table_name COLLATE utf8_bin " + //
                "WHERE LOWER(c.table_schema) = LOWER('sp5035d3d0b2d4a') " + //
                "   AND ('Y' = '' OR LOWER(c.table_name) IN ('leader01_weibo')) " + //
                "ORDER BY t.table_name"));

        Assert.assertEquals(2, provider.getTableStats().size());
    }

}
