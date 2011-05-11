/*
 * Copyright 2011 Alibaba Group. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.alibaba.druid.sql.oracle.bvt;

import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleSelectParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;

public class OracleSelectParserTest extends TestCase {

    public void test_select() throws Exception {
        String sql = "SELECT TRIM('xxxxxxx')  FROM `T_USER`  WHERE FID >= 100 AND ROWNUM < 10";
        SQLSelectParser parser = new OracleSelectParser(sql);
        SQLSelect select = parser.select();

        select.accept(new OracleOutputVisitor(System.out));
        System.out.println();
        System.out.println(select);
    }

    private void output(List<SQLStatement> stmtList) {
        for (SQLStatement stmt : stmtList) {
            stmt.accept(new MySqlOutputVisitor(System.out));
            System.out.println(";");
            System.out.println();
        }
    }

}
