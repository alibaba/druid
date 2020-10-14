/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

public class MySqlSelectTest_307_lateralview
        extends MysqlTest {

    public void test_1() throws Exception {
        String sql = "select tf.* from (select 0) t lateral view explode(array('A','B','C')) tf;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT tf.*\n" +
                "FROM (\n" +
                "\tSELECT 0\n" +
                ") t\n" +
                "\tLATERAL VIEW explode(array('A', 'B', 'C')) tf;", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "select tf.* from (select 0) t lateral view explode(array('A','B','C')) tf as col;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT tf.*\n" +
                "FROM (\n" +
                "\tSELECT 0\n" +
                ") t\n" +
                "\tLATERAL VIEW explode(array('A', 'B', 'C')) tf AS col;", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "select tf.* from (select 0) t lateral view explode(map('A',10,'B',20,'C',30)) tf;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT tf.*\n" +
                "FROM (\n" +
                "\tSELECT 0\n" +
                ") t\n" +
                "\tLATERAL VIEW explode(map('A', 10, 'B', 20, 'C', 30)) tf;", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "select tf.* from (select 0) t lateral view explode(map('A',10,'B',20,'C',30)) tf as key,value;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT tf.*\n" +
                "FROM (\n" +
                "\tSELECT 0\n" +
                ") t\n" +
                "\tLATERAL VIEW explode(map('A', 10, 'B', 20, 'C', 30)) tf AS key, value;", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "select tf.* from (select 0) t lateral view posexplode(array('A','B','C')) tf;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT tf.*\n" +
                "FROM (\n" +
                "\tSELECT 0\n" +
                ") t\n" +
                "\tLATERAL VIEW posexplode(array('A', 'B', 'C')) tf;", stmt.toString());
    }

    public void test_6() throws Exception {
        String sql = "select tf.* from (select 0) t lateral view inline(array(struct('A',10,date '2015-01-01'),struct('B',20,date '2016-02-02'))) tf;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT tf.*\n" +
                "FROM (\n" +
                "\tSELECT 0\n" +
                ") t\n" +
                "\tLATERAL VIEW inline(array(struct('A', 10, DATE '2015-01-01'), struct('B', 20, DATE '2016-02-02'))) tf;", stmt.toString());
    }

    public void test_7() throws Exception {
        String sql = "select tf.* from (select 0) t lateral view inline(array(struct('A',10,date '2015-01-01'),struct('B',20,date '2016-02-02'))) tf as col1,col2,col3;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT tf.*\n" +
                "FROM (\n" +
                "\tSELECT 0\n" +
                ") t\n" +
                "\tLATERAL VIEW inline(array(struct('A', 10, DATE '2015-01-01'), struct('B', 20, DATE '2016-02-02'))) tf AS col1, col2, col3;", stmt.toString());
    }

    public void test_8() throws Exception {
        String sql = "select tf.* from (select 0) t lateral view stack(2,'A',10,date '2015-01-01','B',20,date '2016-01-01') tf;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT tf.*\n" +
                "FROM (\n" +
                "\tSELECT 0\n" +
                ") t\n" +
                "\tLATERAL VIEW stack(2, 'A', 10, DATE '2015-01-01', 'B', 20, DATE '2016-01-01') tf;", stmt.toString());
    }

    public void test_9() throws Exception {
        String sql = "select tf.* from (select 0) t lateral view stack(2,'A',10,date '2015-01-01','B',20,date '2016-01-01') tf as col0,col1,col2;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT tf.*\n" +
                "FROM (\n" +
                "\tSELECT 0\n" +
                ") t\n" +
                "\tLATERAL VIEW stack(2, 'A', 10, DATE '2015-01-01', 'B', 20, DATE '2016-01-01') tf AS col0, col1, col2;", stmt.toString());
    }
}