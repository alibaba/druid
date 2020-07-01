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
package com.alibaba.druid.bvt.sql.odps;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

import junit.framework.TestCase;

public class OdpsSelectTest_mapjoin extends TestCase {

    public void test_column_comment() throws Exception {
        String sql = " select /* + mapjoin(a) */"
                + "\na.shop_name,"
                + "\nb.customer_id,"
                + "\nb.total_price"
                + "\nfrom shop a join sale_detail b"
                + "\non a.shop_name = b.shop_name and b.ds = '20150101';";
        Assert.assertEquals("SELECT /*+ mapjoin(a) */ a.shop_name, b.customer_id, b.total_price\n" +
                "FROM shop a\n" +
                "JOIN sale_detail b\n" +
                "ON a.shop_name = b.shop_name\n" +
                "\tAND b.ds = '20150101';", SQLUtils.formatOdps(sql));
    }


}
