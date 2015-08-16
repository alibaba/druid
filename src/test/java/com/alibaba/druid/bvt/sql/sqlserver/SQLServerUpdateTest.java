/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerUpdateTest extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "update reg_student_charge_item " + //
                     "set FAmountReceived = b.amount   " + //
                     "from reg_student_charge_item a" + //
                     "    ,(" + //
                     "          select a.FId,      " + //
                     "                   case when sum(b.FChargeAmount) is null then 0 " + //
                     "                        else sum(b.FChargeAmount)" + //
                     "                   end as amount " + //
                     "           from reg_student_charge_item a " + //
                     "           left join reg_student_charge_daybook b on a.FId = b.FChargeItemId" + //
                     "           where a.FId=?    group by a.FId" + //
                     "     ) b " + //
                     "where a.FId = b.FId and a.FId = ?";

        String expect = "UPDATE reg_student_charge_item" +
        		"\nSET FAmountReceived = b.amount" +
        		"\nFROM reg_student_charge_item a, (SELECT a.FId, CASE WHEN SUM(b.FChargeAmount) IS NULL THEN 0 ELSE SUM(b.FChargeAmount) END AS amount" +
        		"\n\t\tFROM reg_student_charge_item a" +
        		"\n\t\t\tLEFT JOIN reg_student_charge_daybook b ON a.FId = b.FChargeItemId" +
        		"\n\t\tWHERE a.FId = ?" +
        		"\n\t\tGROUP BY a.FId" +
        		"\n\t\t) b" +
        		"\nWHERE a.FId = b.FId" +
        		"\n\tAND a.FId = ?";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
