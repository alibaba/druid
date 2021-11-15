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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.*;

public class MySqlCreateDatabaseTest extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "create database if not exists a";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE DATABASE IF NOT EXISTS a", output);
    }

    // for ads
    @Test
    public void test_2() throws Exception {
        String sql = "create database test_cascade for 'ALIYUN$test@aliyun.com' options(resourceType=ecu ecu_type=c1 ecu_count=2)";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();

        String output = SQLUtils.toMySqlString(stmt);
        Set<String> allPossibleRes = generateAllPossibleRes();
        assertTrue(allPossibleRes.contains(output));
    }

    private Set<String> generateAllPossibleRes() {
        Set<String> res = new HashSet<>();
        List<String> list = new ArrayList<>();
        list.add("ecu_type=c1");
        list.add("ecu_count=2");
        list.add("resourceType=ecu");
        List<int[]> allPermutation = getAllPermutation(list.size());
        for (int[] cur : allPermutation) {
            res.add("CREATE DATABASE test_cascade FOR 'ALIYUN$test@aliyun.com' OPTIONS ("
                    + list.get(cur[0]) + " " + list.get(cur[1]) + " " + list.get(cur[2]) + " )");
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

    // for ads
    @Test
    public void test_3() throws Exception {
        String sql = "CREATE EXTERNAL TABLE IF NOT EXISTS ots_0.ots_table_0 (pk VARCHAR, a BIGINT, b BIGINT) "
                    + "STORED BY 'OTS' WITH (column_mapping = 'pk:pk,a:col1,b:col2', serializer = 'default') "
                    + "COMMENT 'test_ots_table_0'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE EXTERNAL TABLE IF NOT EXISTS ots_0.ots_table_0 (\n"
                            + "\tpk VARCHAR,\n"
                            + "\ta BIGINT,\n"
                            + "\tb BIGINT\n"
                            + ") COMMENT 'test_ots_table_0'\n"
                            + " STORED BY 'OTS'\n"
                            + " WITH (column_mapping = 'pk:pk,a:col1,b:col2', serializer = 'default')", output);
    }
}
