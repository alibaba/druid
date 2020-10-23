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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateExternalCatalogTest2 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE EXTERNAL CATALOG shanghao_test.oss_catalog_0\n" +
                "PROPERTIES\n" +
                "(\n" +
                "  connector.name='oss'\n" +
                "  'connection-url'='http://oss-cn-hangzhou-zmf.aliyuncs.com'\n" +
                "  'bucket-name'='oss_test'\n" +
                "  'connection-user' = 'access_id'\n" +
                "  'connection-password' = 'access_key'\n" +
                " )\n" +
                "COMMENT 'This is a sample to create an oss connector catalog';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        assertEquals("CREATE EXTERNAL CATALOG shanghao_test.oss_catalog_0 PROPERTIES (\n" +
                "connector.name='oss'\n" +
                "'bucket-name'='oss_test'\n" +
                "'connection-url'='http://oss-cn-hangzhou-zmf.aliyuncs.com'\n" +
                "'connection-user'='access_id'\n" +
                "'connection-password'='access_key')\n" +
                "COMMENT 'This is a sample to create an oss connector catalog';", stmt.toString());
    }



}
