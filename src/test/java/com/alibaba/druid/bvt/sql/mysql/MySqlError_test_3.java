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
package com.alibaba.druid.bvt.sql.mysql;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

public class MySqlError_test_3 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT count(*) AS num FROM sdb_products AS P" + //
                     " LEFT JOIN sdb_goods AS G ON G.goods_id = P.goods_id" + //
                     " LEFT JOIN sdb_goods_cat AS C ON C.cat_id = G.cat_id" + //
                     " LEFT JOIN sdb_brand AS B ON B.brand_id = G.brand_id" + //
                     " WHERE P.disabled = ? AND P.op_status = LIMIT ?, ?";
        Exception error = null;

        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            parser.parseStatementList();
        } catch (Exception e) {
            error = e;
        }

        Assert.assertNotNull(error);
//        error.printStackTrace();
        Assert.assertEquals("syntax error, error in :'us = LIMIT ?, ?',expect QUES, actual QUES LIMIT", error.getMessage());
    }
}
