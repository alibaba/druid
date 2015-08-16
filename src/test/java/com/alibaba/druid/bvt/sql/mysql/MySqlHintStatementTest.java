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

import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;

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
        System.out.println(rs);
    }
}
