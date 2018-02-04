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
public class MySqlWallTest71 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        provider.getConfig().setCommentAllow(true);

        Assert.assertTrue(provider.checkValid(//
        "update order_return set return_goods_money =0.00 ,return_other_money = 8--149.00, return_total_fee = ifnull(return_shipping,0)+ifnull(return_other_discount,0)+0.00--149.00-0.00,return_goods_amount=1,return_real_money=0.00 where id=1319"));

        Assert.assertEquals(1, provider.getTableStats().size());
    }

    public void test_false1() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        provider.getConfig().setCommentAllow(true);

        Assert.assertFalse(provider.checkValid(//
        "select * from order_return where return_goods_money =0.00 ,return_other_money = 8--149.00, return_total_fee = ifnull(return_shipping,0)+ifnull(return_other_discount,0)+0.00--149.00-0.00,return_goods_amount=1,return_real_money=0.00"));

        Assert.assertEquals(1, provider.getTableStats().size());
    }

}
