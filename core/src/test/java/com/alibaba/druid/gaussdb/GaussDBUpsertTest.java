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
package com.alibaba.druid.gaussdb;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.gaussdb.parser.GaussDbStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * @author Acewuye
 *
 * Notes: Original code of this class based on com.alibaba.druid.postgresql.PGUpsertTest
 */
public class GaussDBUpsertTest extends TestCase {
    public void testUpsert() {
        String sql = "insert into \"test_dup\" values(1,'2',-100) on conflict(id) do update set \"count\" = test_dup.\"count\" + 1;";
        String targetSql = "INSERT INTO \"test_dup\"\n"
                + "VALUES (1, '2', -100)\n"
                + "ON CONFLICT (id) DO UPDATE SET \"count\" = test_dup.\"count\" + 1";
        GaussDbStatementParser parser = new GaussDbStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, statement.toString());
    }

}
