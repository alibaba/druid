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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;


public class MySqlSelectTest_238 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select a.cat_id1,a.cat_name1,a.cat_id2,a.cat_name2,a.cat_id3,a.cat_name3,a.leaf_cat_id,a.leaf_cat_name,\n" +
                "       concat(case when cat_name1 is not null then concat(cat_name1,'>>') else '' end,\n" +
                "              case when cat_name2 is not null then concat(cat_name2,'>>') else '' end,\n" +
                "              case when cat_name3 is not null then concat(cat_name3,'>>') else '' end,\n" +
                "              case when leaf_cat_name is not null then concat(leaf_cat_name) else '' end) as full_name_path\n" +
                "-- select count(1)\n" +
                "from \n" +
                "(SELECT c1.cat_id as cat_id1,c1.cat_name as cat_name1,c2.cat_id as cat_id2,c2.cat_name as cat_name2,c3.cat_id as cat_id3,c3.cat_name as cat_name3,s.cat_id as leaf_cat_id,s.cat_name as leaf_cat_name\n" +
                "  FROM union_std_categories s\n" +
                "       left join union_std_categories c3\n" +
                "                 left join union_std_categories c2\n" +
                "                           left join union_std_categories c1 on c2.parent_id = c1.cat_id\n" +
                "         on c3.parent_id = c2.cat_id\n" +
                "       on s.parent_id = c3.cat_id\n" +
                " where s.channel_id=24 and s.deleted = 0 and s.is_leaf = 1\n" +
                " and s.cat_id in (127234002 )\n" +
                ") a";


        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("SELECT a.cat_id1, a.cat_name1, a.cat_id2, a.cat_name2, a.cat_id3\n" +
                "\t, a.cat_name3, a.leaf_cat_id, a.leaf_cat_name\n" +
                "\t, concat(CASE \n" +
                "\t\tWHEN cat_name1 IS NOT NULL THEN concat(cat_name1, '>>')\n" +
                "\t\tELSE ''\n" +
                "\tEND, CASE \n" +
                "\t\tWHEN cat_name2 IS NOT NULL THEN concat(cat_name2, '>>')\n" +
                "\t\tELSE ''\n" +
                "\tEND, CASE \n" +
                "\t\tWHEN cat_name3 IS NOT NULL THEN concat(cat_name3, '>>')\n" +
                "\t\tELSE ''\n" +
                "\tEND, CASE \n" +
                "\t\tWHEN leaf_cat_name IS NOT NULL THEN concat(leaf_cat_name)\n" +
                "\t\tELSE ''\n" +
                "\tEND) AS full_name_path\n" +
                "FROM (\n" +
                "\tSELECT c1.cat_id AS cat_id1, c1.cat_name AS cat_name1, c2.cat_id AS cat_id2, c2.cat_name AS cat_name2, c3.cat_id AS cat_id3\n" +
                "\t\t, c3.cat_name AS cat_name3, s.cat_id AS leaf_cat_id, s.cat_name AS leaf_cat_name\n" +
                "\tFROM union_std_categories s\n" +
                "\t\tLEFT JOIN union_std_categories c3\n" +
                "\t\tLEFT JOIN union_std_categories c2\n" +
                "\t\tLEFT JOIN union_std_categories c1\n" +
                "\t\tON c2.parent_id = c1.cat_id\n" +
                "\t\t\tAND c3.parent_id = c2.cat_id\n" +
                "\t\t\tAND s.parent_id = c3.cat_id\n" +
                "\tWHERE s.channel_id = 24\n" +
                "\t\tAND s.deleted = 0\n" +
                "\t\tAND s.is_leaf = 1\n" +
                "\t\tAND s.cat_id IN (127234002)\n" +
                ") a", stmt.toString());
    }



}