/*
 * Copyright 1999-2026 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MySqlSchemaStatVisitorTest_join_relationship {
    @Test
    public void test_join_relationship() {
        String sql = "SELECT u.name, o.total FROM users u "
                + "JOIN orders o ON u.id = o.user_id "
                + "WHERE u.age > 18";

        List<SQLStatement> statements = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, statements.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.mysql);
        statements.get(0).accept(visitor);

        assertEquals("[users.id, orders.user_id, users.name, orders.total, users.age]",
                visitor.getColumns().toString());
        assertEquals("[users.id =, orders.user_id =, users.age > 18]", visitor.getConditions().toString());
    }
}
