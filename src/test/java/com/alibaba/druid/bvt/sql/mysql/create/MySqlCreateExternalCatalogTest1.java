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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.*;

public class MySqlCreateExternalCatalogTest1 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE EXTERNAL CATALOG IF NOT EXISTS kafka_1 PROPERTIES ("
                    + "'connector.name'='kafka' " + "'kafka.table-names'='table1,table2' "
                    + "'kafka.nodes'='1.1.1.1:10000,1.1.1.2:10000') COMMENT 'this is a kafka connector test.'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);
        Set<String> allPossibleRes = generateAllPossibleRes4test0();
        assertTrue(allPossibleRes.contains(stmt.toString()));
    }

    private Set<String> generateAllPossibleRes4test0() {
        Set<String> possibleRes = new HashSet<>();
        List<String> list = new ArrayList<>();
        list.add("'connector.name'='kafka'");
        list.add("'kafka.nodes'='1.1.1.1:10000,1.1.1.2:10000'");
        list.add("'kafka.table-names'='table1,table2'");
        List<int[]> allPermutation = getAllPermutation(list.size());
        for (int[] cur : allPermutation) {
            possibleRes.add("CREATE EXTERNAL CATALOG IF NOT EXISTS kafka_1 PROPERTIES (\n"
                    + list.get(cur[0]) + "\n" + list.get(cur[1]) + "\n" + list.get(cur[2]) + ")\n"
                    + "COMMENT 'this is a kafka connector test.'");
        }
        return possibleRes;
    }

    public void test_1() throws Exception {
         String sql = "CREATE EXTERNAL CATALOG user_db.mysql_1 PROPERTIES ("
                      + "'connector.name'='mysql' "
                    + "'connection-url'='jdbc:mysql://1.1.1.1:3306' "
                      + "'connection-user'=\"x'!xx\" "
                    + "'connection-password'=\"x'xx\")";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);
        Set<String> allPossibleRes = generateAllPossibleRes4test1();
        assertTrue(allPossibleRes.contains(stmt.toString()));
    }

    private Set<String> generateAllPossibleRes4test1() {
        Set<String> res = new HashSet<>();
        List<String> list = new ArrayList<>();
        list.add("'connector.name'='mysql'");
        list.add("'connection-url'='jdbc:mysql://1.1.1.1:3306'");
        list.add("'connection-user'=\"x'!xx\"");
        list.add("'connection-password'=\"x'xx\"");
        List<int[]> allPermutation = getAllPermutation(list.size());
        for (int[] cur : allPermutation) {
            res.add("CREATE EXTERNAL CATALOG user_db.mysql_1 PROPERTIES (\n"
                    + list.get(cur[0]) + "\n" + list.get(cur[1]) + "\n" + list.get(cur[2]) + "\n" + list.get(cur[3]) + ")");
        }
        return res;
    }

    private static List<int[]> getAllPermutation(int n) {
        List<int[]> res = new ArrayList<>();
        int total = 1;
        int[] permutation = new int[n];
        for (int i = 1; i <= n; ++i) {
            permutation[i - 1] = i - 1;
            total *= i;
        }

        for (int i = 0; i < total; ++i) {
            res.add(Arrays.copyOf(permutation,n));
            nexPermutation(permutation);
        }
        return res;
    }

    private static void nexPermutation(int[] nums) {
        if (nums == null || nums.length == 0) return;
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            --i;
        }
        if (i == -1) {
            return;
        }
        int j = i + 1;
        while (j < nums.length && nums[j] > nums[i]) {
            ++j;
        }
        swap(nums, i, j - 1);
        reverse(nums, i + 1, nums.length);
    }
}
