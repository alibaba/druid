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
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class SQLServerSelectTest26 extends TestCase {

    public void test_simple() throws Exception {
        String sql = //
                "-- ----------------------------\n" +
                        "-- Table structure for Adode\n" +
                        "-- ----------------------------\n" +
                        "DROP TABLE [dbo].[Adode]\n" +
                        "GO\n" +
                        "CREATE TABLE [dbo].[Adode] (\n" +
                        "[Ad_Work_Start_Time] varchar(255) NULL,\n" +
                        "[Ad_Work_Stop_Time] varchar(255) NULL,\n" +
                        "[Ad_Wait_Start_Time] varchar(255) NULL,\n" +
                        "[Ad_Wait_Stop_Time] varchar(255) NULL,\n" +
                        "[Order_ID] varchar(255) NOT NULL\n" +
                        ")"; //

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.SQL_SERVER);

        {
            String text = SQLUtils.toSQLString(stmtList, JdbcConstants.SQL_SERVER);

            Assert.assertEquals("DROP TABLE [dbo].[Adode]\n" +
                    "GO\n" +
                    "CREATE TABLE [dbo].[Adode] (\n" +
                    "\t[Ad_Work_Start_Time] varchar(255) NULL,\n" +
                    "\t[Ad_Work_Stop_Time] varchar(255) NULL,\n" +
                    "\t[Ad_Wait_Start_Time] varchar(255) NULL,\n" +
                    "\t[Ad_Wait_Stop_Time] varchar(255) NULL,\n" +
                    "\t[Order_ID] varchar(255) NOT NULL\n" +
                    ")", text);
        }
        {
            String text = SQLUtils.toSQLString(stmtList, JdbcConstants.SQL_SERVER, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            Assert.assertEquals("drop table [dbo].[Adode]\n" +
                    "go\n" +
                    "create table [dbo].[Adode] (\n" +
                    "\t[Ad_Work_Start_Time] varchar(255) null,\n" +
                    "\t[Ad_Work_Stop_Time] varchar(255) null,\n" +
                    "\t[Ad_Wait_Start_Time] varchar(255) null,\n" +
                    "\t[Ad_Wait_Stop_Time] varchar(255) null,\n" +
                    "\t[Order_ID] varchar(255) not null\n" +
                    ")", text);
        }
    }
}
