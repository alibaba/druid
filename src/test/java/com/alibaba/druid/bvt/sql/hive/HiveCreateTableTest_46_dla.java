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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class HiveCreateTableTest_46_dla extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE EXTERNAL TABLE parquet_tbl LIKE MAPPING('oss://user/etl/destination/datafile1.dat')" +
                " TBLPROPERTIES('target.table.location'='oss://user/etl/destination/')" ;
        SQLStatement stmt =  SQLUtils.parseSingleStatement(sql, DbType.hive, SQLParserFeature.KeepComments);

        assertEquals("CREATE EXTERNAL TABLE parquet_tbl\n" +
                "LIKE MAPPING('oss://user/etl/destination/datafile1.dat')\n" +
                "TBLPROPERTIES (\n" +
                "\t'target.table.location' = 'oss://user/etl/destination/'\n" +
                ")", stmt.toString());

        assertEquals(stmt.toString(), SQLUtils.parseSingleStatement(stmt.toString(), DbType.hive, SQLParserFeature.KeepComments).toString());
    }

}
