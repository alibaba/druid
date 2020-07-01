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

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;

import junit.framework.TestCase;

public class SQLServerSelectTest21 extends TestCase {

    public void test_simple() throws Exception {
        String sql = "SELECT First_Name + ' ' + Last Name FROM Employees ORDER BY First_Name OFFSET 10 ROWS FETCH NEXT 5 ROWS ONLY;"; //

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        {
            String text = SQLUtils.toSQLServerString(stmt);

            Assert.assertEquals("SELECT First_Name + ' ' + Last AS Name" //
                                + "\nFROM Employees" //
                                + "\nORDER BY First_Name" //
                                + "\nOFFSET 10 ROWS FETCH NEXT 5 ROWS ONLY;", text);
        }
        {
            String text = SQLUtils.toSQLServerString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            Assert.assertEquals("select First_Name + ' ' + Last as Name" //
                                + "\nfrom Employees" //
                                + "\norder by First_Name" //
                                + "\noffset 10 rows fetch next 5 rows only;", text);
        }
    }
}
