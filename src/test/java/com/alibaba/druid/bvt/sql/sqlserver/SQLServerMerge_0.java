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
package com.alibaba.druid.bvt.sql.sqlserver;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class SQLServerMerge_0 extends TestCase {

    public void test_0() throws Exception {
        String sql = "MERGE INTO tb_device_category t1\n" +
                "USING ( select distinct device_category, factory_id from temp_device where device_category is not null\n" +
                ") t2\n" +
                "on (t1.name = t2.device_category and t1.factory_id = t2.factory_id)\n" +
                "WHEN NOT MATCHED THEN\n" +
                "INSERT (name, factory_id) values (t2.device_category, t2.factory_id);";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        parser.setParseCompleteValues(false);
        parser.setParseValuesSize(3);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLMergeStatement mergeStmt = (SQLMergeStatement) stmt;


        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

        String formatSql = "MERGE INTO tb_device_category t1\n" +
                "USING (\n" +
                "\tSELECT DISTINCT device_category, factory_id\n" +
                "\tFROM temp_device\n" +
                "\tWHERE device_category IS NOT NULL\n" +
                ") t2 ON (t1.name = t2.device_category\n" +
                "AND t1.factory_id = t2.factory_id) \n" +
                "WHEN NOT MATCHED THEN INSERT (name, factory_id) VALUES (t2.device_category, t2.factory_id);";
        assertEquals(formatSql, SQLUtils.toSQLServerString(mergeStmt));
    }
}
