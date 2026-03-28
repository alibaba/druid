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
package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OdpsFixTest {
    /**
     * clone
     * qualify 丢失
     */
    @Test
    public void testCloneQualify() {
        String sql = "SELECT * FROM table_a WHERE a_1 = 100 QUALIFY ROW_NUMBER() OVER (PARTITION BY b_1 ORDER BY b_2 DESC) = 1";
        SQLSelectStatement oldStmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.odps);
        SQLSelectStatement newStmt = oldStmt.clone();

        String oldStr = SQLUtils.toOdpsString(oldStmt);
        String newStr = SQLUtils.toOdpsString(newStmt);

        System.out.println("Old: " + oldStr);
        System.out.println("New: " + newStr);

        assertEquals(oldStr, newStr);
    }

}
