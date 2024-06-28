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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;
import org.junit.Test;

public class OracleSelectTest_issue_5109 extends OracleTest {
    @Test
    public void test_group_by_connect_by1() {
        String sql = "SELECT\n" +
                "c.TABLE_NAME,\n" +
                "c.CONSTRAINT_NAME,\n" +
                "c.CONSTRAINT_TYPE,\n" +
                "c.STATUS,\n" +
                "c.SEARCH_CONDITION,\n" +
                "(\n" +
                "SELECT LTRIM(MAX(SYS_CONNECT_BY_PATH(cname || ':' || NVL(p,1),',')) KEEP (DENSE_RANK LAST ORDER BY curr),',')\n" +
                "FROM (SELECT\n" +
                "col.CONSTRAINT_NAME cn,col.POSITION p,col.COLUMN_NAME cname,\n" +
                "ROW_NUMBER() OVER (PARTITION BY col.CONSTRAINT_NAME ORDER BY col.POSITION) AS curr,\n" +
                "ROW_NUMBER() OVER (PARTITION BY col.CONSTRAINT_NAME ORDER BY col.POSITION) -1 AS prev\n" +
                "FROM ALL_CONS_COLUMNS col\n" +
                "WHERE col.OWNER ='IMPL_ZHUZHOU' AND col.TABLE_NAME = 'ACC1'\n" +
                ") WHERE cn = c.CONSTRAINT_NAME GROUP BY cn CONNECT BY prev = PRIOR curr AND cn = PRIOR cn START WITH curr = 1\n" +
                ") COLUMN_NAMES_NUMS\n" +
                "FROM\n" +
                "ALL_CONSTRAINTS c\n" +
                "WHERE\n" +
                "c.CONSTRAINT_TYPE <> 'R'\n" +
                "AND c.OWNER = 'IMPL_ZHUZHOU'\n" +
                "AND c.TABLE_NAME = 'ACC1';";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.oracle);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(2, visitor.getTables().size());
        System.out.println(stmt);
    }

    @Test
    public void test_group_by_connect_by_start_with() {
        String sql = "SELECT deptno,\n" +
                "       LTRIM(MAX(SYS_CONNECT_BY_PATH(ename,','))\n" +
                "       KEEP (DENSE_RANK LAST ORDER BY curr),',') AS employees\n" +
                "FROM   (SELECT deptno,\n" +
                "               ename,\n" +
                "               ROW_NUMBER() OVER (PARTITION BY deptno ORDER BY ename) AS curr,\n" +
                "               ROW_NUMBER() OVER (PARTITION BY deptno ORDER BY ename) -1 AS prev\n" +
                "        FROM   emp)\n" +
                "GROUP BY deptno\n" +
                "CONNECT BY prev = PRIOR curr AND deptno = PRIOR deptno\n" +
                "START WITH curr = 1;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.oracle);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(1, visitor.getTables().size());
        System.out.println(stmt);
    }
}
