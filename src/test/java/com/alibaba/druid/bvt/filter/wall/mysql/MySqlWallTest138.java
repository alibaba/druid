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

public class MySqlWallTest138 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "INSERT INTO T01_CHECKIN_CUSTOMER" //
                     + "(WEB_USER_ID,NAME,COUNTRY,PROVINCE,CITY" //
                     + ",POSTCODE,PHONE,FAX,EMAIL,ADDRESS,FIRST_NAME,LAST_NAME,sex) " //
                     + "select 2,null,'4225',null,'beijing','','1','','1223@123.com','beijing','booking','test',null "//
                     + "from dual " //
                     + "where not exists   ("//
                     + "    select EMAIL" //
                     + "    from T01_CHECKIN_CUSTOMER" //
                     + "    where WEB_USER_ID=2 and EMAIL='1223@123.com'" //
                     + ")";
        Assert.assertTrue(provider.checkValid(sql));
    }

}
