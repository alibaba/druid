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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlSelectTest_187 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select coach_id, tournament_name ,tournament_id, season_id, season, count(1) as num,\n" +
                "sum(case when wdl = 0 then 1 else 0 end) as loss,\n" +
                "sum(case when wdl = 1 then 1 else 0 end) as draw,\n" +
                "sum(case when wdl = 3 then 1 else 0 end) as win\n" +
                "from (\n" +
                "select a.coach_id,b.team_id, a.home_team_id, a.away_team_id,a.tournament_id, a.tournament_name, a.season_id,a.season, a.result,\n" +
                "case\n" +
                "when b.team_id = a.home_team_id then (case\n" +
                "WHEN CONVERT(SUBSTRING(a.result, 1, LOCATE(':',a.result)-1), SIGNED) > CONVERT(SUBSTRING(a.result, LOCATE(':',a.result)+1, CHAR_LENGTH(a.result)), SIGNED) THEN 3\n" +
                "WHEN CONVERT(SUBSTRING(a.result, 1, LOCATE(':',a.result)-1), SIGNED) < CONVERT(SUBSTRING(a.result, LOCATE(':',a.result)+1, CHAR_LENGTH(a.result)), SIGNED) THEN 0\n" +
                "else 1 end )\n" +
                "when b.team_id = a.away_team_id then (case\n" +
                "WHEN CONVERT(SUBSTRING(a.result, 1, LOCATE(':',a.result)-1), SIGNED) > CONVERT(SUBSTRING(a.result, LOCATE(':',a.result)+1, CHAR_LENGTH(a.result)), SIGNED) THEN 0\n" +
                "WHEN CONVERT(SUBSTRING(a.result, 1, LOCATE(':',a.result)-1), SIGNED) < CONVERT(SUBSTRING(a.result, LOCATE(':',a.result)+1, CHAR_LENGTH(a.result)), SIGNED) THEN 3\n" +
                "else 1 end ) end as wdl\n" +
                "from p_coach_match_detail as a\n" +
                "left join p_coach_career b on a.match_date > b.appoint_time and a.match_date < b.until_time and a.coach_id = b.coach_id and b.function = 'Manager'\n" +
                ") a where season_id >= 2017 and coach_id = 5075 group by coach_id, tournament_name ,tournament_id, season_id, season ORDER BY season_id DESC";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(14, visitor.getColumns().size());
        assertEquals(10, visitor.getConditions().size());
        assertEquals(1, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("p_coach_match_detail"));
        assertTrue(visitor.containsColumn("p_coach_match_detail", "match_date"));
        assertEquals("p_coach_match_detail.season_id", visitor.getOrderByColumns().get(0).toString());

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("SELECT coach_id, tournament_name, tournament_id, season_id, season\n" +
                        "\t, count(1) AS num\n" +
                        "\t, sum(CASE \n" +
                        "\t\tWHEN wdl = 0 THEN 1\n" +
                        "\t\tELSE 0\n" +
                        "\tEND) AS loss\n" +
                        "\t, sum(CASE \n" +
                        "\t\tWHEN wdl = 1 THEN 1\n" +
                        "\t\tELSE 0\n" +
                        "\tEND) AS draw\n" +
                        "\t, sum(CASE \n" +
                        "\t\tWHEN wdl = 3 THEN 1\n" +
                        "\t\tELSE 0\n" +
                        "\tEND) AS win\n" +
                        "FROM (\n" +
                        "\tSELECT a.coach_id, b.team_id, a.home_team_id, a.away_team_id, a.tournament_id\n" +
                        "\t\t, a.tournament_name, a.season_id, a.season, a.result\n" +
                        "\t\t, CASE \n" +
                        "\t\t\tWHEN b.team_id = a.home_team_id THEN \n" +
                        "\t\t\t\tCASE \n" +
                        "\t\t\t\t\tWHEN CONVERT(SUBSTRING(a.result, 1, LOCATE(':', a.result) - 1), SIGNED) > CONVERT(SUBSTRING(a.result, LOCATE(':', a.result) + 1, CHAR_LENGTH(a.result)), SIGNED) THEN 3\n" +
                        "\t\t\t\t\tWHEN CONVERT(SUBSTRING(a.result, 1, LOCATE(':', a.result) - 1), SIGNED) < CONVERT(SUBSTRING(a.result, LOCATE(':', a.result) + 1, CHAR_LENGTH(a.result)), SIGNED) THEN 0\n" +
                        "\t\t\t\t\tELSE 1\n" +
                        "\t\t\t\tEND\n" +
                        "\t\t\tWHEN b.team_id = a.away_team_id THEN \n" +
                        "\t\t\t\tCASE \n" +
                        "\t\t\t\t\tWHEN CONVERT(SUBSTRING(a.result, 1, LOCATE(':', a.result) - 1), SIGNED) > CONVERT(SUBSTRING(a.result, LOCATE(':', a.result) + 1, CHAR_LENGTH(a.result)), SIGNED) THEN 0\n" +
                        "\t\t\t\t\tWHEN CONVERT(SUBSTRING(a.result, 1, LOCATE(':', a.result) - 1), SIGNED) < CONVERT(SUBSTRING(a.result, LOCATE(':', a.result) + 1, CHAR_LENGTH(a.result)), SIGNED) THEN 3\n" +
                        "\t\t\t\t\tELSE 1\n" +
                        "\t\t\t\tEND\n" +
                        "\t\tEND AS wdl\n" +
                        "\tFROM p_coach_match_detail a\n" +
                        "\t\tLEFT JOIN p_coach_career b\n" +
                        "\t\tON a.match_date > b.appoint_time\n" +
                        "\t\t\tAND a.match_date < b.until_time\n" +
                        "\t\t\tAND a.coach_id = b.coach_id\n" +
                        "\t\t\tAND b.function = 'Manager'\n" +
                        ") a\n" +
                        "WHERE season_id >= 2017\n" +
                        "\tAND coach_id = 5075\n" +
                        "GROUP BY coach_id, tournament_name, tournament_id, season_id, season\n" +
                        "ORDER BY season_id DESC", //
                            output);
    }

}
