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
public class MySqlWallTest96 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setCommentAllow(false);

        String sql = "insert into darenai_stat_url SET user='nologin',ip='58.101.223.183',reffer='http://item.taobao.com/item.htm?spm=a230r.1.14.419.KDVewC&amp;id=17052767689',url='/d/jingpinhui?spm=2013.1.0.0.zr4nLz&amp;ac=shop&amp;imageid=1019937265&amp;s=1259538&amp;s=1259538',shopnick='零利润3232',time=NOW()";

        Assert.assertTrue(provider.checkValid(//
        sql));
    }

}
