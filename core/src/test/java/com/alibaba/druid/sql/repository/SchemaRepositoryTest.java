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
package com.alibaba.druid.sql.repository;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author ruansheng
 */
public class SchemaRepositoryTest {
    @Test
    public void testFindFunction() {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        SQLCreateFunctionStatement stmt = new SQLCreateFunctionStatement();
        String funcName = "Test";
        stmt.setName(new SQLIdentifierExpr(funcName));
        repository.acceptCreateFunction(stmt);
        SchemaObject schemaObject = repository.findFunction(funcName);
        assertNotNull(schemaObject);
    }
}
