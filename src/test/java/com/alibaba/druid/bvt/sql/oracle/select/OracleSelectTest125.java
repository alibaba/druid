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
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;


public class OracleSelectTest125 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT\n" +
                "specialact0_. ID AS id1_14_,\n" +
                "specialact0_.gmt_create AS gmt_create2_14_,\n" +
                "specialact0_.gmt_modified AS gmt_modified3_14_,\n" +
                "specialact0_.issuer AS issuer4_14_,\n" +
                "specialact0_.prisonarea AS prisonarea5_14_,\n" +
                "specialact0_.prisonarea_code AS prisonarea_code6_14_,\n" +
                "specialact0_. NAME AS name7_14_,\n" +
                "specialact0_.prison AS prison8_14_,\n" +
                "specialact0_.synopsis AS synopsis9_14_,\n" +
                "specialact0_.sys_orgcode AS sys_orgcode10_14_,\n" +
                "specialact0_.sys_orgname AS sys_orgname11_14_,\n" +
                "specialact0_.sys_permname AS sys_permname12_14_,\n" +
                "specialact0_.sys_perrmcode AS sys_perrmcode13_14_,\n" +
                "specialact0_. TIMES AS times14_14_\n" +
                "FROM\n" +
                "hs_special_activities specialact0_\n" +
                "WHERE\n" +
                "1 = 1 offset ? ROWS FETCH NEXT ? ROWS ONLY";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT specialact0_.ID AS id1_14_, specialact0_.gmt_create AS gmt_create2_14_, specialact0_.gmt_modified AS gmt_modified3_14_, specialact0_.issuer AS issuer4_14_, specialact0_.prisonarea AS prisonarea5_14_\n" +
                "\t, specialact0_.prisonarea_code AS prisonarea_code6_14_, specialact0_.NAME AS name7_14_, specialact0_.prison AS prison8_14_, specialact0_.synopsis AS synopsis9_14_, specialact0_.sys_orgcode AS sys_orgcode10_14_\n" +
                "\t, specialact0_.sys_orgname AS sys_orgname11_14_, specialact0_.sys_permname AS sys_permname12_14_, specialact0_.sys_perrmcode AS sys_perrmcode13_14_, specialact0_.TIMES AS times14_14_\n" +
                "FROM hs_special_activities specialact0_\n" +
                "WHERE 1 = 1\n" +
                "OFFSET ? ROWS FETCH FIRST ? ROWS ONLY", stmt.toString());

        assertEquals("select specialact0_.ID as id1_14_, specialact0_.gmt_create as gmt_create2_14_, specialact0_.gmt_modified as gmt_modified3_14_, specialact0_.issuer as issuer4_14_, specialact0_.prisonarea as prisonarea5_14_\n" +
                "\t, specialact0_.prisonarea_code as prisonarea_code6_14_, specialact0_.NAME as name7_14_, specialact0_.prison as prison8_14_, specialact0_.synopsis as synopsis9_14_, specialact0_.sys_orgcode as sys_orgcode10_14_\n" +
                "\t, specialact0_.sys_orgname as sys_orgname11_14_, specialact0_.sys_permname as sys_permname12_14_, specialact0_.sys_perrmcode as sys_perrmcode13_14_, specialact0_.TIMES as times14_14_\n" +
                "from hs_special_activities specialact0_\n" +
                "where 1 = 1\n" +
                "offset ? rows fetch first ? rows only", stmt.toLowerCaseString());
    }

}