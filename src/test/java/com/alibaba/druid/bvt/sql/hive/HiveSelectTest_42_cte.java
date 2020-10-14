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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveSelectTest_42_cte extends TestCase {

    public void test_0() throws Exception {
        String sql = "with dycg_perform_startgametaketimelog as(\n" +
                "select app_id, if(ver is null, '', ver) as ver, if(machine_name is null, '', split(machine_name, '-') [0]) as machine_dc, game_id, current_step, err_code\n" +
                "  from ods_compass.ods_10006_dycg_perform_startgametaketimelog_tbl\n" +
                " where length(app_id)> 0\n" +
                "   and dt= substring(${dt_hour}, 0, 8)\n" +
                "   and hour= substr(${dt_hour}, 9, 2)\n" +
                "   and game_type= 1)\n" +
                "insert overwrite table ads_compass.ads_dycg_perform_eventpre_reach_game_process_rate_hour partition(ds= ${dt_hour})\n" +
                "select if(app_id is not null, app_id, 'all') as app_id, if(ver is not null, ver, 'all') as dycg_ver, if(machine_dc is not null, machine_dc, 'all') as machine_dc, if(game_id is not null, game_id, 'all') as game_id, sum(CASE WHEN current_step= 6\n" +
                "    or current_step= 7 THEN 1 ELSE 0 END) as reach_game_process_cnt, sum(CASE WHEN current_step= 5\n" +
                "    or current_step= 6\n" +
                "    or current_step= 7 THEN 1 ELSE 0 END) as tot_reach_game_process_cnt, round(1-(sum(CASE WHEN current_step= 5 THEN 1 ELSE 0 END) /sum(CASE WHEN current_step= 5\n" +
                "    or current_step= 6\n" +
                "    or current_step= 7 THEN 1 ELSE 0 END)), 4) as reach_game_process_rate, from_unixtime(unix_timestamp(cast(${dt_hour} as string), 'yyyyMMddHH'), 'yyyy-MM-dd') as dt, substring(${dt_hour}, 9, 2) as `hour`\n" +
                "  from dycg_perform_startgametaketimelog\n" +
                "group by app_id, ver, machine_dc, game_id grouping sets((app_id),(app_id, ver),(app_id, ver, machine_dc),(app_id, ver, game_id),(app_id, machine_dc),(app_id, machine_dc, game_id),(app_id, game_id),(app_id, ver, machine_dc, game_id))";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        HiveInsertStatement stmt = (HiveInsertStatement) statementList.get(0);

        assertEquals("WITH dycg_perform_startgametaketimelog AS (\n" +
                "\t\tSELECT app_id\n" +
                "\t\t\t, if(ver IS NULL, '', ver) AS ver\n" +
                "\t\t\t, if(machine_name IS NULL, '', split(machine_name, '-')[0]) AS machine_dc\n" +
                "\t\t\t, game_id, current_step, err_code\n" +
                "\t\tFROM ods_compass.ods_10006_dycg_perform_startgametaketimelog_tbl\n" +
                "\t\tWHERE length(app_id) > 0\n" +
                "\t\t\tAND dt = substring(${dt_hour}, 0, 8)\n" +
                "\t\t\tAND hour = substr(${dt_hour}, 9, 2)\n" +
                "\t\t\tAND game_type = 1\n" +
                "\t)\n" +
                "INSERT OVERWRITE TABLE ads_compass.ads_dycg_perform_eventpre_reach_game_process_rate_hour PARTITION (ds=${dt_hour})\n" +
                "SELECT if(app_id IS NOT NULL, app_id, 'all') AS app_id\n" +
                "\t, if(ver IS NOT NULL, ver, 'all') AS dycg_ver\n" +
                "\t, if(machine_dc IS NOT NULL, machine_dc, 'all') AS machine_dc\n" +
                "\t, if(game_id IS NOT NULL, game_id, 'all') AS game_id\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN current_step = 6\n" +
                "\t\t\tOR current_step = 7\n" +
                "\t\tTHEN 1\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS reach_game_process_cnt\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN current_step = 5\n" +
                "\t\t\tOR current_step = 6\n" +
                "\t\t\tOR current_step = 7\n" +
                "\t\tTHEN 1\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS tot_reach_game_process_cnt\n" +
                "\t, round(1 - sum(CASE \n" +
                "\t\tWHEN current_step = 5 THEN 1\n" +
                "\t\tELSE 0\n" +
                "\tEND) / sum(CASE \n" +
                "\t\tWHEN current_step = 5\n" +
                "\t\t\tOR current_step = 6\n" +
                "\t\t\tOR current_step = 7\n" +
                "\t\tTHEN 1\n" +
                "\t\tELSE 0\n" +
                "\tEND), 4) AS reach_game_process_rate\n" +
                "\t, from_unixtime(unix_timestamp(CAST(${dt_hour} AS string), 'yyyyMMddHH'), 'yyyy-MM-dd') AS dt\n" +
                "\t, substring(${dt_hour}, 9, 2) AS `hour`\n" +
                "FROM dycg_perform_startgametaketimelog\n" +
                "GROUP BY app_id, ver, machine_dc, game_id\n" +
                "\tGROUPING SETS ((app_id), (app_id, ver), (app_id, ver, machine_dc), (app_id, ver, game_id), (app_id, machine_dc), (app_id, machine_dc, game_id), (app_id, game_id), (app_id, ver, machine_dc, game_id))", stmt.toString());

    }
}
