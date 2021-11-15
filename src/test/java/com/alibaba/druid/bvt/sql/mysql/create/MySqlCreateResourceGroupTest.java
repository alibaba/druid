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
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.*;

public class MySqlCreateResourceGroupTest
        extends MysqlTest {

    @Test
    public void test_create() throws Exception {
        String sql = "CREATE RESOURCE GROUP sql_thread TYPE = USER VCPU = 1,3 THREAD_PRIORITY = -20";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Set<String> allPossibleRes = generateAllPossibleRes("CREATE RESOURCE GROUP sql_thread ", "");
        assertTrue(allPossibleRes.contains(output));
    }

    @Test
    public void test_create2() throws Exception {
        String sql = "CREATE RESOURCE GROUP sql_thread TYPE = USER VCPU = 1,3 THREAD_PRIORITY = -20 disable";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Set<String> allPossibleRes = generateAllPossibleRes("CREATE RESOURCE GROUP sql_thread ", " DISABLE");
        assertTrue(allPossibleRes.contains(output));
    }

    @Test
    public void test_create3() throws Exception {
        String sql = "CREATE RESOURCE GROUP group_name\n" +
                "    QUERY_EXECUTION_TYPE = USER" +
                " ACU = 3 " +
                " ENABLE";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE RESOURCE GROUP group_name QUERY_EXECUTION_TYPE = USER ACU = 3 ENABLE", output);
    }

    @Test
    public void test_create4() throws Exception {
        String sql = "CREATE RESOURCE GROUP group_name\n" +
                " ENABLE";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE RESOURCE GROUP group_name ENABLE", output);
    }

    @Test
    public void test_alter() throws Exception {
        String sql = "ALTER RESOURCE GROUP sql_thread TYPE = USER VCPU = 1,3 THREAD_PRIORITY = -20";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Set<String> allPossibleRes = generateAllPossibleRes("ALTER RESOURCE GROUP sql_thread ", "");
        assertTrue(allPossibleRes.contains(output));
    }

    private Set<String> generateAllPossibleRes(String prefix, String suffix) {
        Set<String> res = new HashSet<>();
        List<String> list = new ArrayList<>();
        list.add("THREAD_PRIORITY = -20");
        list.add("VCPU = 1,3");
        list.add("TYPE = USER");
        List<int[]> allPermutation = getAllPermutation(list.size());
        for (int[] cur : allPermutation) {
            res.add(prefix + list.get(cur[0]) + " " + list.get(cur[1]) + " " + list.get(cur[2]) + suffix);
        }
        return res;
    }

    @Test
    public void test_alter2() throws Exception {
        String sql = "ALTER RESOURCE GROUP group_name\n" +
                "    QUERY_EXECUTION_TYPE = USER" +
                " ACU = 3 " +
                " ENABLE";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER RESOURCE GROUP group_name QUERY_EXECUTION_TYPE = USER ACU = 3 ENABLE", output);
    }

    @Test
    public void test_alter4() throws Exception {
        String sql = "ALTER RESOURCE GROUP group_name\n" +
                " ENABLE";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER RESOURCE GROUP group_name ENABLE", output);
    }

    @Test
    public void test_drop() throws Exception {
        String sql = "DROP RESOURCE GROUP sql_thread;";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("DROP RESOURCE GROUP sql_thread;", output);
    }

    @Test
    public void test_list() throws Exception {
        String sql = "LIST RESOURCE GROUP;";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("LIST RESOURCE GROUP;", output);
    }

    @Test
    public void test_list2() throws Exception {
        String sql = "LIST RESOURCE GROUP";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("LIST RESOURCE GROUP", output);
    }

    private List<int[]> getAllPermutation(int n) {
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

    private void nexPermutation(int[] nums) {
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
