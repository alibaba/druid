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

import java.net.URLDecoder;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class MySqlWallTest129 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setCommentAllow(false);
        
        Assert.assertTrue(provider.checkValid("select * from t where id = 1"));

        String sql = "select * from t where id = ";
        sql += URLDecoder.decode("999999.9%0A%2F*!30000union%0Aall%0Aselect%0A0x31303235343830303536%2Cconcat%280x7e%2C0x27%2C%28Select%0A%40%40version%29%2C0x27%2C0x7e%29%2C0x31303235343830303536%2C0x31303235343830303536%2C0x31303235343830303536%2C0x31303235343830303536%2C0x31303235343830303536%2C0x31303235343830303536%2C0x31303235343830303536%2C0x31303235343830303536%2C0x31303235343830303536%2C0x31303235343830303536*%2F--");

        System.out.println(sql);
        Assert.assertFalse(provider.checkValid(sql));

    }

}
