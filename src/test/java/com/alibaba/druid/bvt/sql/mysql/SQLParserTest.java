/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlSelectParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySql2OracleOutputVisitor;
import com.alibaba.druid.sql.parser.SQLSelectParser;

public class SQLParserTest extends TestCase {

    public void test_select() throws Exception {
        String sql = "   SELECT COUNT(*) FROM close_plan WHERE 1=1          AND close_type = ?             AND target_type = ?             AND target_id = ?         AND(    mi_name=?   )               AND end_time >= ?         ";
        SQLSelectParser parser = new MySqlSelectParser(sql);
        SQLSelect select = parser.select();

        StringBuilder out = new StringBuilder();
        MySql2OracleOutputVisitor visitor = new MySql2OracleOutputVisitor(out);

        select.accept(visitor);

        System.out.println(out);
    }

}
