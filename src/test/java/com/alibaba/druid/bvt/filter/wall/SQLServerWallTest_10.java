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
public class SQLServerWallTest_10 extends TestCase {

    public void test_true() throws Exception {
        WallProvider provider = new SQLServerWallProvider();

        String sql = "select top 50 * FROM [V_Goods_WithAvailableStockQuantity] where Status='����' and (Code like '%mu%' ESCAPE '\' or Model like '%mu%' ESCAPE '\' or Spec like '%mu%' ESCAPE '\' or BarCode like '%mu%' ESCAPE '\' or ProductName like '%mu%' ESCAPE '\' or dbo.F_GetPY(ProductName) like '%mu%' ESCAPE '\') ";

        Assert.assertTrue(provider.checkValid(sql));

    }

}
