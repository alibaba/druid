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
package com.alibaba.druid.bvt.sql.oracle.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class OracleOutputVisitorTest_timestampAtLocal extends TestCase {
    public void test_0() throws Exception {
        String sql = "INSERT INTO ALL_TYPE_FIELDS (\"TIMESTAMP WITH LOCAL TIME ZONE\", \"TIMESTAMP WITH LOCAL TIME ZONE\") VALUES (SYSTIMESTAMP AT LOCAL, SYSTIMESTAMP AT TIME ZONE 'UTC')";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        assertEquals(1, statementList.size());

        String formatSql = SQLUtils.toSQLString(stmt, DbType.oracle, new SQLUtils.FormatOption(false, false, false));
        assertEquals("insert into ALL_TYPE_FIELDS (\"TIMESTAMP WITH LOCAL TIME ZONE\", \"TIMESTAMP WITH LOCAL TIME ZONE\") values (SYSTIMESTAMP at local, SYSTIMESTAMP at time zone 'UTC')", formatSql);
    }
}
