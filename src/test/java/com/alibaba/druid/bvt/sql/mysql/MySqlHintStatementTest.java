/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;

public class MySqlHintStatementTest extends TestCase {

    public void test() {
        String sql = "DROP TABLE IF EXISTS `item_similarity`;"//
                + "\n/*!40101 SET @saved_cs_client     = @@character_set_client */;"//
                + "\n/*!40101 SET character_set_client = utf8 */;" //
                + "\nCREATE TABLE `item_similarity` ("//
                + " `id` bigint(20) unsigned NOT NULL, "//
                + " `sellerId` bigint(20) DEFAULT NULL,"//
                + " PRIMARY KEY (`id`)" //
                + " ) ENGINE=InnoDB DEFAULT CHARSET=utf8;"//
                + " \n/*!40101 SET character_set_client = @saved_cs_client */;";
        String rs = SQLUtils.formatMySql(sql);
        assertEquals("DROP TABLE IF EXISTS `item_similarity`;"
                + "\n"
                + "\n/*!40101 SET @saved_cs_client     = @@character_set_client */;"
                + "\n"
                + "\n/*!40101 SET character_set_client = utf8 */;"
                + "\n"
                + "\nCREATE TABLE `item_similarity` ("
                + "\n\t`id` bigint(20) UNSIGNED NOT NULL,"
                + "\n\t`sellerId` bigint(20) DEFAULT NULL,"
                + "\n\tPRIMARY KEY (`id`)"
                + "\n) ENGINE = InnoDB CHARSET = utf8;"
                + "\n"
                + "\n/*!40101 SET character_set_client = @saved_cs_client */;", rs);
    }

    public void test_1() throws Exception {
        String sql = "update "
                + "/*MS-MRCHISHUB-MH-MESSAGE-UPDATE*/\n"
                + "/*+ INDEX(mh_message primary) */ "
                + "mh_message set gmt_modified = now(),status= '099' where id = 1244918;";
        String rs = SQLUtils.formatMySql(sql);
        assertEquals("UPDATE /*MS-MRCHISHUB-MH-MESSAGE-UPDATE*/\n" +
                "/*+ INDEX(mh_message primary) */ mh_message\n"
                + "SET gmt_modified = now(), status = '099'\n" + "WHERE id = 1244918;", rs);
    }
}
