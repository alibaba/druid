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
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class HiveCreateTableTest_42 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS default.create_test (id int COMMENT '学号', name string COMMENT '姓名')ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' WITH SERDEPROPERTIES(\"serialization.encoding\"='gbk');";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive, SQLParserFeature.KeepComments);

        assertEquals("CREATE TABLE IF NOT EXISTS default.create_test (\n" +
                "\tid int COMMENT '学号',\n" +
                "\tname string COMMENT '姓名'\n" +
                ")\n" +
                "ROW FORMAT\n" +
                "\tSERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'\n" +
                "WITH SERDEPROPERTIES (\n" +
                "\t\"serialization.encoding\" = 'gbk'\n" +
                ");", SQLUtils.toSQLString(statementList, JdbcConstants.HIVE));

    }

}
