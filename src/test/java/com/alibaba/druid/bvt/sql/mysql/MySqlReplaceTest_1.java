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
package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlReplaceTest_1 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "REPLACE INTO `tb_001` (`gmt_create`, `theday`, `pt`, `song_id`, `song_name`, `play_num_1d`) (SELECT now(), '20170820', case a.pt\n" +
                "        when 'android' then '安卓'\n" +
                "        when 'ios' then 'ios'\n" +
                "        when 'yunos' then '云os'\n" +
                "     end AS `pt`, a.song_id, if(b.song_name is null,'新发布歌曲',b.song_name), a.play_num_1d FROM ((SELECT pt, song_id, sum(play_num_1d) AS `play_num_1d` FROM `tb_002` WHERE `theday` = '20170820' AND `pt` IN ('android') GROUP BY `pt`, `song_id` ORDER BY `play_num_1d` DESC LIMIT 0, 100) UNION (SELECT pt, song_id, sum(play_num_1d) AS `play_num_1d` FROM `tb_002` WHERE `theday` = '20170820' AND `pt` IN ('ios') GROUP BY `pt`, `song_id` ORDER BY `play_num_1d` DESC LIMIT 0, 100) UNION (SELECT pt, song_id, sum(play_num_1d) AS `play_num_1d` FROM `tb_002` WHERE `theday` = '20170820' AND `pt` IN ('yunos') GROUP BY `pt`, `song_id` ORDER BY `play_num_1d` DESC LIMIT 0, 100)) AS `a` INNER JOIN (SELECT song_id, song_name FROM `tb_003` WHERE `song_name` IS NOT NULL) AS `b` ON `a`.`song_id` = `b`.`song_id`) UNION (SELECT now(), '20170820', '整体' AS `pt`, a.song_id, if(b.song_name is null,'新发布歌曲',b.song_name), a.play_num_1d FROM (SELECT song_id, play_num_1d FROM (SELECT song_id, sum(play_num_1d) AS `play_num_1d` FROM `tb_002` WHERE `theday` = '20170820' GROUP BY `song_id`) AS `tmp` ORDER BY `play_num_1d` DESC LIMIT 0, 100) AS `a` INNER JOIN (SELECT song_id, song_name FROM `tb_003` WHERE `song_name` IS NOT NULL) AS `b` ON `a`.`song_id` = `b`.`song_id`)";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        assertEquals("REPLACE INTO `tb_001` (`gmt_create`, `theday`, `pt`, `song_id`, `song_name`, `play_num_1d`)\n" +
                "\t(SELECT now(), '20170820'\n" +
                "\t\t, CASE a.pt\n" +
                "\t\t\tWHEN 'android' THEN '安卓'\n" +
                "\t\t\tWHEN 'ios' THEN 'ios'\n" +
                "\t\t\tWHEN 'yunos' THEN '云os'\n" +
                "\t\tEND AS `pt`, a.song_id\n" +
                "\t\t, if(b.song_name IS NULL, '新发布歌曲', b.song_name)\n" +
                "\t\t, a.play_num_1d\n" +
                "\tFROM (\n" +
                "\t\t(SELECT pt, song_id, sum(play_num_1d) AS `play_num_1d`\n" +
                "\t\tFROM `tb_002`\n" +
                "\t\tWHERE `theday` = '20170820'\n" +
                "\t\t\tAND `pt` IN ('android')\n" +
                "\t\tGROUP BY `pt`, `song_id`\n" +
                "\t\tORDER BY `play_num_1d` DESC\n" +
                "\t\tLIMIT 0, 100)\n" +
                "\t\tUNION\n" +
                "\t\t(SELECT pt, song_id, sum(play_num_1d) AS `play_num_1d`\n" +
                "\t\tFROM `tb_002`\n" +
                "\t\tWHERE `theday` = '20170820'\n" +
                "\t\t\tAND `pt` IN ('ios')\n" +
                "\t\tGROUP BY `pt`, `song_id`\n" +
                "\t\tORDER BY `play_num_1d` DESC\n" +
                "\t\tLIMIT 0, 100)\n" +
                "\t\tUNION\n" +
                "\t\t(SELECT pt, song_id, sum(play_num_1d) AS `play_num_1d`\n" +
                "\t\tFROM `tb_002`\n" +
                "\t\tWHERE `theday` = '20170820'\n" +
                "\t\t\tAND `pt` IN ('yunos')\n" +
                "\t\tGROUP BY `pt`, `song_id`\n" +
                "\t\tORDER BY `play_num_1d` DESC\n" +
                "\t\tLIMIT 0, 100)\n" +
                "\t) `a`\n" +
                "\t\tINNER JOIN (\n" +
                "\t\t\tSELECT song_id, song_name\n" +
                "\t\t\tFROM `tb_003`\n" +
                "\t\t\tWHERE `song_name` IS NOT NULL\n" +
                "\t\t) `b`\n" +
                "\t\tON `a`.`song_id` = `b`.`song_id`)\n" +
                "\tUNION\n" +
                "\t(SELECT now(), '20170820', '整体' AS `pt`, a.song_id\n" +
                "\t\t, if(b.song_name IS NULL, '新发布歌曲', b.song_name)\n" +
                "\t\t, a.play_num_1d\n" +
                "\tFROM (\n" +
                "\t\tSELECT song_id, play_num_1d\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT song_id, sum(play_num_1d) AS `play_num_1d`\n" +
                "\t\t\tFROM `tb_002`\n" +
                "\t\t\tWHERE `theday` = '20170820'\n" +
                "\t\t\tGROUP BY `song_id`\n" +
                "\t\t) `tmp`\n" +
                "\t\tORDER BY `play_num_1d` DESC\n" +
                "\t\tLIMIT 0, 100\n" +
                "\t) `a`\n" +
                "\t\tINNER JOIN (\n" +
                "\t\t\tSELECT song_id, song_name\n" +
                "\t\t\tFROM `tb_003`\n" +
                "\t\t\tWHERE `song_name` IS NOT NULL\n" +
                "\t\t) `b`\n" +
                "\t\tON `a`.`song_id` = `b`.`song_id`)", SQLUtils.toMySqlString(stmt));
        assertEquals("replace into `tb_001` (`gmt_create`, `theday`, `pt`, `song_id`, `song_name`, `play_num_1d`)\n" +
                "\t(select now(), '20170820'\n" +
                "\t\t, case a.pt\n" +
                "\t\t\twhen 'android' then '安卓'\n" +
                "\t\t\twhen 'ios' then 'ios'\n" +
                "\t\t\twhen 'yunos' then '云os'\n" +
                "\t\tend as `pt`, a.song_id\n" +
                "\t\t, if(b.song_name is null, '新发布歌曲', b.song_name)\n" +
                "\t\t, a.play_num_1d\n" +
                "\tfrom (\n" +
                "\t\t(select pt, song_id, sum(play_num_1d) as `play_num_1d`\n" +
                "\t\tfrom `tb_002`\n" +
                "\t\twhere `theday` = '20170820'\n" +
                "\t\t\tand `pt` in ('android')\n" +
                "\t\tgroup by `pt`, `song_id`\n" +
                "\t\torder by `play_num_1d` desc\n" +
                "\t\tlimit 0, 100)\n" +
                "\t\tunion\n" +
                "\t\t(select pt, song_id, sum(play_num_1d) as `play_num_1d`\n" +
                "\t\tfrom `tb_002`\n" +
                "\t\twhere `theday` = '20170820'\n" +
                "\t\t\tand `pt` in ('ios')\n" +
                "\t\tgroup by `pt`, `song_id`\n" +
                "\t\torder by `play_num_1d` desc\n" +
                "\t\tlimit 0, 100)\n" +
                "\t\tunion\n" +
                "\t\t(select pt, song_id, sum(play_num_1d) as `play_num_1d`\n" +
                "\t\tfrom `tb_002`\n" +
                "\t\twhere `theday` = '20170820'\n" +
                "\t\t\tand `pt` in ('yunos')\n" +
                "\t\tgroup by `pt`, `song_id`\n" +
                "\t\torder by `play_num_1d` desc\n" +
                "\t\tlimit 0, 100)\n" +
                "\t) `a`\n" +
                "\t\tinner join (\n" +
                "\t\t\tselect song_id, song_name\n" +
                "\t\t\tfrom `tb_003`\n" +
                "\t\t\twhere `song_name` is not null\n" +
                "\t\t) `b`\n" +
                "\t\ton `a`.`song_id` = `b`.`song_id`)\n" +
                "\tunion\n" +
                "\t(select now(), '20170820', '整体' as `pt`, a.song_id\n" +
                "\t\t, if(b.song_name is null, '新发布歌曲', b.song_name)\n" +
                "\t\t, a.play_num_1d\n" +
                "\tfrom (\n" +
                "\t\tselect song_id, play_num_1d\n" +
                "\t\tfrom (\n" +
                "\t\t\tselect song_id, sum(play_num_1d) as `play_num_1d`\n" +
                "\t\t\tfrom `tb_002`\n" +
                "\t\t\twhere `theday` = '20170820'\n" +
                "\t\t\tgroup by `song_id`\n" +
                "\t\t) `tmp`\n" +
                "\t\torder by `play_num_1d` desc\n" +
                "\t\tlimit 0, 100\n" +
                "\t) `a`\n" +
                "\t\tinner join (\n" +
                "\t\t\tselect song_id, song_name\n" +
                "\t\t\tfrom `tb_003`\n" +
                "\t\t\twhere `song_name` is not null\n" +
                "\t\t) `b`\n" +
                "\t\ton `a`.`song_id` = `b`.`song_id`)", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, statementList.size());

        System.out.println(stmt);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(3, visitor.getTables().size());
        assertEquals(13, visitor.getColumns().size());
        assertEquals(6, visitor.getConditions().size());

        assertTrue(visitor.containsTable("tb_001"));

//        assertTrue(visitor.containsColumn("t1", "id"));
//        assertTrue(visitor.containsColumn("t1", "name"));
    }
}
