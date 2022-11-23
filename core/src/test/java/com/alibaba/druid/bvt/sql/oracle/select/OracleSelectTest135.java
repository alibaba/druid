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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;


public class OracleSelectTest135 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from (\n" +
                "        select C.FLEX_VALUE,\n" +
                "        c.PARENT_FLEX_VALUE,\n" +
                "        c.CN_DESCRIPTION,\n" +
                "        c.En_Description from(\n" +
                "        (select a.PARENT_FLEX_VALUE,\n" +
                "        a.FLEX_VALUE,\n" +
                "        a.CN_DESCRIPTION,\n" +
                "        a.EN_DESCRIPTION,\n" +
                "        a.SUMMARY_FLAG from\n" +
                "        bpm_department_t4 a\n" +
                "        where 1=1   \n" +
                "        AND a.ENABLED_FLAG='Y'\n" +
                "        )\n" +
                "        MINUS\n" +
                "        (select distinct b.PARENT_FLEX_VALUE,\n" +
                "        b.FLEX_VALUE,\n" +
                "        b.CN_DESCRIPTION,\n" +
                "        b.EN_DESCRIPTION,\n" +
                "        b.SUMMARY_FLAG\n" +
                "        from\n" +
                "        bpm_department_t4 b\n" +
                "        where 1=1     \n" +
                "        AND b.ENABLED_FLAG='Y'\n" +
                "        start with\n" +
                "        (b.flex_value = 'C019998' or b.flex_value like 'ZF%')\n" +
                "        connect\n" +
                "        by\n" +
                "        prior b.flex_value =\n" +
                "        b.parent_flex_value)\n" +
                "        )c\n" +
                "        where 1=1\n" +
                "    and\n" +
                "        UPPER(c.FLEX_VALUE) like\n" +
                "        concat(concat('%','334518102833'),'%')\n" +
                "            OR c.CN_DESCRIPTION like\n" +
                "            concat(concat('%','334518102833'),'%')\n" +
                "        )";
//        System.out.println(sql);

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT C.FLEX_VALUE, c.PARENT_FLEX_VALUE, c.CN_DESCRIPTION, c.En_Description\n" +
                "\tFROM (\n" +
                "\t\tSELECT a.PARENT_FLEX_VALUE, a.FLEX_VALUE, a.CN_DESCRIPTION, a.EN_DESCRIPTION, a.SUMMARY_FLAG\n" +
                "\t\tFROM bpm_department_t4 a\n" +
                "\t\tWHERE 1 = 1\n" +
                "\t\t\tAND a.ENABLED_FLAG = 'Y'\n" +
                "\t\tMINUS\n" +
                "\t\tSELECT DISTINCT b.PARENT_FLEX_VALUE, b.FLEX_VALUE, b.CN_DESCRIPTION, b.EN_DESCRIPTION, b.SUMMARY_FLAG\n" +
                "\t\tFROM bpm_department_t4 b\n" +
                "\t\tWHERE 1 = 1\n" +
                "\t\t\tAND b.ENABLED_FLAG = 'Y'\n" +
                "\t\tSTART WITH b.flex_value = 'C019998'\n" +
                "\t\t\tOR b.flex_value LIKE 'ZF%'\n" +
                "\t\tCONNECT BY PRIOR b.flex_value = b.parent_flex_value\n" +
                "\t) c\n" +
                "\tWHERE 1 = 1\n" +
                "\t\tAND UPPER(c.FLEX_VALUE) LIKE concat(concat('%', '334518102833'), '%')\n" +
                "\t\tOR c.CN_DESCRIPTION LIKE concat(concat('%', '334518102833'), '%')\n" +
                ")", stmt.toString());

        assertEquals("select *\n" +
                "from (\n" +
                "\tselect C.FLEX_VALUE, c.PARENT_FLEX_VALUE, c.CN_DESCRIPTION, c.En_Description\n" +
                "\tfrom (\n" +
                "\t\tselect a.PARENT_FLEX_VALUE, a.FLEX_VALUE, a.CN_DESCRIPTION, a.EN_DESCRIPTION, a.SUMMARY_FLAG\n" +
                "\t\tfrom bpm_department_t4 a\n" +
                "\t\twhere 1 = 1\n" +
                "\t\t\tand a.ENABLED_FLAG = 'Y'\n" +
                "\t\tminus\n" +
                "\t\tselect distinct b.PARENT_FLEX_VALUE, b.FLEX_VALUE, b.CN_DESCRIPTION, b.EN_DESCRIPTION, b.SUMMARY_FLAG\n" +
                "\t\tfrom bpm_department_t4 b\n" +
                "\t\twhere 1 = 1\n" +
                "\t\t\tand b.ENABLED_FLAG = 'Y'\n" +
                "\t\tstart with b.flex_value = 'C019998'\n" +
                "\t\t\tor b.flex_value like 'ZF%'\n" +
                "\t\tconnect by prior b.flex_value = b.parent_flex_value\n" +
                "\t) c\n" +
                "\twhere 1 = 1\n" +
                "\t\tand UPPER(c.FLEX_VALUE) like concat(concat('%', '334518102833'), '%')\n" +
                "\t\tor c.CN_DESCRIPTION like concat(concat('%', '334518102833'), '%')\n" +
                ")", stmt.toLowerCaseString());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(6, visitor.getColumns().size());
        assertEquals(5, visitor.getConditions().size());
        assertEquals(1, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

//        assertTrue(visitor.containsColumn("srm1.CONSIGNEE_ADDRESS", "id"));
    }

}