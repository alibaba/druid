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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;


public class OracleSelectTest118 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "select CONNECT_BY_ROOT(su1.id) as id \n" +
                "from sys_unit su1 \n" +
                "where EXISTS (\n" +
                "\tselect 1 from sys_user suu where su1.id=suu.unitid and suu.id='b84f830ccc9e46e6a6c4add795c423ac'\n" +
                ") \n" +
                "start with su1.type='sch' \n" +
                "connect by prior su1.id=su1.parentid";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT CONNECT_BY_ROOT(su1.id) AS id\n" +
                "FROM sys_unit su1\n" +
                "WHERE EXISTS (\n" +
                "\tSELECT 1\n" +
                "\tFROM sys_user suu\n" +
                "\tWHERE su1.id = suu.unitid\n" +
                "\t\tAND suu.id = 'b84f830ccc9e46e6a6c4add795c423ac'\n" +
                ")\n" +
                "START WITH su1.type = 'sch'\n" +
                "CONNECT BY PRIOR su1.id = su1.parentid", stmt.toString());
    }

}