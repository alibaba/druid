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

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;

import junit.framework.TestCase;

public class SQLServerRowNumberTest2 extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "SELECT * FROM ("
                     + //
                     "   SELECT ROW_NUMBER() OVER (ORDER BY FAlertDate Desc, FAlertLevel, FAlertType)  AS RowNumber, *"
                     + //
                     "        from monitor_business" + //
                     "   where FRemoveAlert = ?" + //
                     " ) AS temp_table" + //
                     "   WHERE RowNumber BETWEEN ? AND ?";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT ROW_NUMBER() OVER (ORDER BY FAlertDate DESC, FAlertLevel, FAlertType) AS RowNumber, *\n" +
                "\tFROM monitor_business\n" +
                "\tWHERE FRemoveAlert = ?\n" +
                ") temp_table\n" +
                "WHERE RowNumber BETWEEN ? AND ?", SQLUtils.toSQLServerString(stmt));

        assertEquals("select *\n" +
                "from (\n" +
                "\tselect row_number() over (order by FAlertDate desc, FAlertLevel, FAlertType) as RowNumber, *\n" +
                "\tfrom monitor_business\n" +
                "\twhere FRemoveAlert = ?\n" +
                ") temp_table\n" +
                "where RowNumber between ? and ?", SQLUtils.toSQLServerString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
    
    public void test_isEmpty_2() throws Exception {
        String sql = "SELECT * FROM ("
                     + //
                     "   SELECT ROW_NUMBER() OVER (ORDER BY FAlertDate Desc, FAlertLevel, FAlertType)  AS RowNumber, *"
                     + //
                     "        from monitor_business" + //
                     "   where FRemoveAlert = ?" + //
                     " ) AS temp_table" + //
                     "   WHERE RowNumber NOT BETWEEN ? AND ?";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        Assert.assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT ROW_NUMBER() OVER (ORDER BY FAlertDate DESC, FAlertLevel, FAlertType) AS RowNumber, *\n" +
                "\tFROM monitor_business\n" +
                "\tWHERE FRemoveAlert = ?\n" +
                ") temp_table\n" +
                "WHERE RowNumber NOT BETWEEN ? AND ?", SQLUtils.toSQLServerString(stmt));

        assertEquals("select *\n" +
                "from (\n" +
                "\tselect row_number() over (order by FAlertDate desc, FAlertLevel, FAlertType) as RowNumber, *\n" +
                "\tfrom monitor_business\n" +
                "\twhere FRemoveAlert = ?\n" +
                ") temp_table\n" +
                "where RowNumber not between ? and ?", SQLUtils.toSQLServerString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
