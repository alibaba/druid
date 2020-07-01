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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerSelectTest6 extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "WITH DirReps(ManagerID, DirectReports) AS " + //
                     "(" + //
                     "    SELECT ManagerID, COUNT(*) " + //
                     "    FROM HumanResources.Employee AS e" + //
                     "    WHERE ManagerID IS NOT NULL" + //
                     "    GROUP BY ManagerID" + //
                     ")" + //
                     "SELECT ManagerID, DirectReports " + //
                     "FROM DirReps " + //
                     "ORDER BY ManagerID;";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        assertEquals("WITH DirReps (ManagerID, DirectReports) AS (\n" +
				"\t\tSELECT ManagerID, COUNT(*)\n" +
				"\t\tFROM HumanResources.Employee e\n" +
				"\t\tWHERE ManagerID IS NOT NULL\n" +
				"\t\tGROUP BY ManagerID\n" +
				"\t)\n" +
				"SELECT ManagerID, DirectReports\n" +
				"FROM DirReps\n" +
				"ORDER BY ManagerID;", text);

//        System.out.println(text);
    }
}
