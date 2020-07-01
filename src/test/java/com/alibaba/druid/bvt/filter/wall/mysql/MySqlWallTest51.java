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
package com.alibaba.druid.bvt.filter.wall.mysql;

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
public class MySqlWallTest51 extends TestCase {

    public void test_true() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        Assert.assertTrue(provider.checkValid(//
        "REPLACE /* SearchMySQL::update Wikiadmin */ INTO `wiki_searchindex` (si_page,si_title,si_text) VALUES ('15',' u8e5858d u8e8b4a3 u8e5a3b0 u8e6988e')"));

        Assert.assertEquals(1, provider.getTableStats().size());
    }
}
