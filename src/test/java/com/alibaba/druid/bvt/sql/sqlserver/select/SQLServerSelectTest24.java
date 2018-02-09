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
package com.alibaba.druid.bvt.sql.sqlserver.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

public class SQLServerSelectTest24 extends TestCase {

    public void test_simple() throws Exception {
        String sql = //
                "SELECT TOP 100 *\n" +
                        "FROM\n" +
                        "(SELECT ROW_NUMBER()\n" +
                        "OVER (ORDER BY DateUpdated DESC) PAGE_ROW_NUMBER, *\n" +
                        "FROM\n" +
                        "(SELECT x.*\n" +
                        "FROM\n" +
                        "(SELECT TOP 2000 a.*\n" +
                        "FROM pk_Battle a\n" +
                        "WHERE a.RequestUserId = 33460323\n" +
                        "AND a.IsActive = 1\n" +
                        "AND a.BattleType <> 2\n" +
                        "AND a.Status NOT IN (0, 2)\n" +
                        "AND EXISTS\n" +
                        "(SELECT TOP 1 1\n" +
                        "FROM pk_BattleExt\n" +
                        "WHERE BattleId = a.Id\n" +
                        "AND RequestIsViewResults = 0)\n" +
                        "ORDER BY a.Id DESC\n" +
                        "UNION\n" +
                        "SELECT TOP 1000 a.*\n" +
                        "FROM pk_Battle a\n" +
                        "WHERE a.ResponseUserId = 33460323\n" +
                        "AND a.IsActive = 1\n" +
                        "AND a.BattleType = 1\n" +
                        "AND a.Status NOT IN (0, 2)\n" +
                        "AND EXISTS\n" +
                        "(SELECT TOP 1 1\n" +
                        "FROM pk_BattleExt\n" +
                        "WHERE BattleId = a.Id\n" +
                        "AND ResponseIsViewResults = 0)\n" +
                        "ORDER BY a.Id DESC) x) AS PAGE_TABLE_ALIAS) AS PAGE_TABLE_ALIAS\n" +
                        "WHERE PAGE_ROW_NUMBER > 0\n" +
                        "ORDER BY PAGE_ROW_NUMBER"; //

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        {
            String text = SQLUtils.toSQLServerString(stmt);

            Assert.assertEquals("SELECT TOP 100 *\n" +
                    "FROM (\n" +
                    "\tSELECT ROW_NUMBER() OVER (ORDER BY DateUpdated DESC) AS PAGE_ROW_NUMBER, *\n" +
                    "\tFROM (\n" +
                    "\t\tSELECT x.*\n" +
                    "\t\tFROM (\n" +
                    "\t\t\tSELECT TOP 2000 a.*\n" +
                    "\t\t\tFROM pk_Battle a\n" +
                    "\t\t\tWHERE a.RequestUserId = 33460323\n" +
                    "\t\t\t\tAND a.IsActive = 1\n" +
                    "\t\t\t\tAND a.BattleType <> 2\n" +
                    "\t\t\t\tAND a.Status NOT IN (0, 2)\n" +
                    "\t\t\t\tAND EXISTS (\n" +
                    "\t\t\t\t\tSELECT TOP 1 1\n" +
                    "\t\t\t\t\tFROM pk_BattleExt\n" +
                    "\t\t\t\t\tWHERE BattleId = a.Id\n" +
                    "\t\t\t\t\t\tAND RequestIsViewResults = 0\n" +
                    "\t\t\t\t)\n" +
                    "\t\t\tORDER BY a.Id DESC\n" +
                    "\t\t\tUNION\n" +
                    "\t\t\tSELECT TOP 1000 a.*\n" +
                    "\t\t\tFROM pk_Battle a\n" +
                    "\t\t\tWHERE a.ResponseUserId = 33460323\n" +
                    "\t\t\t\tAND a.IsActive = 1\n" +
                    "\t\t\t\tAND a.BattleType = 1\n" +
                    "\t\t\t\tAND a.Status NOT IN (0, 2)\n" +
                    "\t\t\t\tAND EXISTS (\n" +
                    "\t\t\t\t\tSELECT TOP 1 1\n" +
                    "\t\t\t\t\tFROM pk_BattleExt\n" +
                    "\t\t\t\t\tWHERE BattleId = a.Id\n" +
                    "\t\t\t\t\t\tAND ResponseIsViewResults = 0\n" +
                    "\t\t\t\t)\n" +
                    "\t\t\tORDER BY a.Id DESC\n" +
                    "\t\t) x\n" +
                    "\t) PAGE_TABLE_ALIAS\n" +
                    ") PAGE_TABLE_ALIAS\n" +
                    "WHERE PAGE_ROW_NUMBER > 0\n" +
                    "ORDER BY PAGE_ROW_NUMBER", text);
        }
        {
            String text = SQLUtils.toSQLServerString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            Assert.assertEquals("select top 100 *\n" +
                    "from (\n" +
                    "\tselect row_number() over (order by DateUpdated desc) as PAGE_ROW_NUMBER, *\n" +
                    "\tfrom (\n" +
                    "\t\tselect x.*\n" +
                    "\t\tfrom (\n" +
                    "\t\t\tselect top 2000 a.*\n" +
                    "\t\t\tfrom pk_Battle a\n" +
                    "\t\t\twhere a.RequestUserId = 33460323\n" +
                    "\t\t\t\tand a.IsActive = 1\n" +
                    "\t\t\t\tand a.BattleType <> 2\n" +
                    "\t\t\t\tand a.Status not in (0, 2)\n" +
                    "\t\t\t\tand exists (\n" +
                    "\t\t\t\t\tselect top 1 1\n" +
                    "\t\t\t\t\tfrom pk_BattleExt\n" +
                    "\t\t\t\t\twhere BattleId = a.Id\n" +
                    "\t\t\t\t\t\tand RequestIsViewResults = 0\n" +
                    "\t\t\t\t)\n" +
                    "\t\t\torder by a.Id desc\n" +
                    "\t\t\tunion\n" +
                    "\t\t\tselect top 1000 a.*\n" +
                    "\t\t\tfrom pk_Battle a\n" +
                    "\t\t\twhere a.ResponseUserId = 33460323\n" +
                    "\t\t\t\tand a.IsActive = 1\n" +
                    "\t\t\t\tand a.BattleType = 1\n" +
                    "\t\t\t\tand a.Status not in (0, 2)\n" +
                    "\t\t\t\tand exists (\n" +
                    "\t\t\t\t\tselect top 1 1\n" +
                    "\t\t\t\t\tfrom pk_BattleExt\n" +
                    "\t\t\t\t\twhere BattleId = a.Id\n" +
                    "\t\t\t\t\t\tand ResponseIsViewResults = 0\n" +
                    "\t\t\t\t)\n" +
                    "\t\t\torder by a.Id desc\n" +
                    "\t\t) x\n" +
                    "\t) PAGE_TABLE_ALIAS\n" +
                    ") PAGE_TABLE_ALIAS\n" +
                    "where PAGE_ROW_NUMBER > 0\n" +
                    "order by PAGE_ROW_NUMBER", text);
        }
    }
}
