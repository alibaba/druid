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

public class HiveCreateTableTest_43 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE EXTERNAL TABLE tbl2_copy  STORED AS PARQUET LOCATION 'oss://oss-cn-beijing-for-openanalytics-test-2/datasets/jinluo/tbl1_copy/' TBLPROPERTIES ('auto.create.location' = 'true')" +
                " like SELECT * FROM tbl1;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.hive, SQLParserFeature.KeepComments);

        assertEquals("CREATE EXTERNAL TABLE tbl2_copy\n" +
                "STORED AS PARQUET\n" +
                "LOCATION 'oss://oss-cn-beijing-for-openanalytics-test-2/datasets/jinluo/tbl1_copy/'\n" +
                "TBLPROPERTIES (\n" +
                "\t'auto.create.location' = 'true'\n" +
                ")\n" +
                "LIKE\n" +
                "SELECT *\n" +
                "FROM tbl1;", SQLUtils.toSQLString(statementList, JdbcConstants.HIVE));

    }

}
