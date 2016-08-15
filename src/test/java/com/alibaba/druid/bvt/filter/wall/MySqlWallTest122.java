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

public class MySqlWallTest122 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setCommentAllow(false);

        String sql = "SELECT name, '******' password, createTime from user where name like 'admin%' AND 4667=(SELECT UPPER(XMLType(CHR(60)||CHR(58)||CHR(115)||CHR(114)||CHR(110)||CHR(58)||(SELECT (CASE WHEN (4667=4667) THEN 1 ELSE 0 END) FROM DUAL)||CHR(58)||CHR(106)||CHR(112)||CHR(122)||CHR(58)||CHR(62))) FROM DUAL) AND '%'=''";

        Assert.assertFalse(provider.checkValid(sql));
    }

}
