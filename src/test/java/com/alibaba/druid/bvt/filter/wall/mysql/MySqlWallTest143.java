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

public class MySqlWallTest143 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "SELECT" //
                + " `Extent1`.`ID`, " //
                + " `Extent1`.`State`, " //
                + " `Extent1`.`CreateTime`, " //
                + " `Extent1`.`UpdateTime`, " //
                + " `Extent1`.`OpeningBank`, " //
                + " `Extent1`.`BankAccount`, " //
                + " `Extent1`.`BankAccountName`, " //
                + " `Extent1`.`Zone`, " //
                + " `Extent1`.`AccountantMobile`, " //
                + " `Extent1`.`IsPublic`" //
                + " FROM `paybank` AS `Extent1`" //
                + " WHERE (`Extent1`.`State` > -1)" //
                + " AND ((`Extent1`.`Zone`) = (CASE WHEN (1 IS  NULL) THEN (1)  ELSE (1) END))";
        Assert.assertTrue(provider.checkValid(sql));
    }
}
