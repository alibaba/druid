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
package com.alibaba.druid.bvt.sql.mysql.show;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

public class MySqlShowTest_9_partitions extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SHOW PARTITIONS from t";

        SQLStatement stmt = SQLUtils.parseStatements(sql, DbType.mysql).get(0);
        
        String result = SQLUtils.toMySqlString(stmt);
        assertEquals("SHOW PARTITIONS FROM t", result);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
        stmt.accept(visitor);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

    }
}
