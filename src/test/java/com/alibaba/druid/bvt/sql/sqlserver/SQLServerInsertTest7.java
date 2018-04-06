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

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;

public class SQLServerInsertTest7 extends TestCase {

    public void test_0() throws Exception {
        String sql = "INSERT INTO MMS_SETTLEMENT_COM(handler,handleTime,MID,MERCHANTNAME,TOTALAMT,ACTUALAMT,"
                + "     paymentMoney,STATUS,SERIAL_NUM,REMARKS)"
                + "SELECT 'admin',getdate(),MID,MERCHANTNAME,SUM(CONVERT(DECIMAL(18,2),isnull(TOTALAMT,0))) "
                + " TOTALAMT,SUM(CONVERT(DECIMAL(18,2),isnull(ACTUALAMT,0))) ACTUALAMT,SUM(CONVERT(DECIMAL(18,2),"
                + " isnull(paymentMoney,0))) paymentMoney,2,126,("
                + " SELECT REMARKS+'' "
                + " FROM MMS_SETTLEMENT_COM "
                + " WHERE MID=A.MID "
                + " FOR XML PATH('')"
                + ") AS REMARKS FROM MMS_SETTLEMENT_COM A WHERE ID IN (304,305,306,297,108,184) GROUP BY MID ,MERCHANTNAME";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        parser.setParseCompleteValues(false);
        parser.setParseValuesSize(3);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLServerInsertStatement insertStmt = (SQLServerInsertStatement) stmt;

        assertEquals(0, insertStmt.getValuesList().size());
        assertEquals(10, insertStmt.getColumns().size());
        assertEquals(1, statementList.size());

        SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
        stmt.accept(visitor);

        String formatSql = "INSERT INTO MMS_SETTLEMENT_COM\n" +
                "\t(handler, handleTime, MID, MERCHANTNAME, TOTALAMT\n" +
                "\t, ACTUALAMT, paymentMoney, STATUS, SERIAL_NUM, REMARKS)\n" +
                "SELECT 'admin', getdate(), MID, MERCHANTNAME\n" +
                "\t, SUM(CONVERT(DECIMAL(18, 2), isnull(TOTALAMT, 0))) AS TOTALAMT\n" +
                "\t, SUM(CONVERT(DECIMAL(18, 2), isnull(ACTUALAMT, 0))) AS ACTUALAMT\n" +
                "\t, SUM(CONVERT(DECIMAL(18, 2), isnull(paymentMoney, 0))) AS paymentMoney\n" +
                "\t, 2, 126\n" +
                "\t, (\n" +
                "\t\tSELECT REMARKS + ''\n" +
                "\t\tFROM MMS_SETTLEMENT_COM\n" +
                "\t\tWHERE MID = A.MID\n" +
                "\t\tFOR XML PATH('')\n" +
                "\t) AS REMARKS\n" +
                "FROM MMS_SETTLEMENT_COM A\n" +
                "WHERE ID IN (304, 305, 306, 297, 108, 184)\n" +
                "GROUP BY MID, MERCHANTNAME";
        assertEquals(formatSql, SQLUtils.toSQLServerString(insertStmt));
    }


}
