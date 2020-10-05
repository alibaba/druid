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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;


public class OracleSelectTest120 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "SELECT id, TRANSLATE (LTRIM (text, '/'), '/', ',') ledid,TRANSLATE (LTRIM (text1, '/'), '/', ',') position,picturepath,rtfpath,enter,leave,speed,stay_time,begintime,endtime FROM (SELECT ROW_NUMBER () OVER (PARTITION BY id ORDER BY id, lvl DESC) rn, id, text,text1,picturepath,rtfpath,enter,leave,speed,stay_time,begintime,endtime FROM (SELECT id, LEVEL lvl, SYS_CONNECT_BY_PATH (ledid,'/') text,SYS_CONNECT_BY_PATH (position,'/') text1,picturepath,rtfpath,enter,leave,speed,stay_time,begintime,endtime FROM (SELECT id, t.ledid as ledid,t1.position,t.picturepath,t.rtfpath,t.enter,t.leave,t.speed,t.stay_time,t.begintime,t.endtime, ROW_NUMBER () OVER (PARTITION BY id ORDER BY id,t.ledid) x FROM enjoyorvms_proginfov3 t,enjoyorvms_ledinfov3 t1 where t.ledid = t1.ledid ORDER BY id, ledid) a CONNECT BY id = PRIOR id AND x - 1 = PRIOR x)) WHERE rn = 1 ORDER BY id;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT id\n" +
                "\t, TRANSLATE(LTRIM(text, '/'), '/', ',') AS ledid\n" +
                "\t, TRANSLATE(LTRIM(text1, '/'), '/', ',') AS position\n" +
                "\t, picturepath, rtfpath, enter, leave, speed\n" +
                "\t, stay_time, begintime, endtime\n" +
                "FROM (\n" +
                "\tSELECT ROW_NUMBER() OVER (PARTITION BY id ORDER BY id, lvl DESC) AS rn, id, text, text1\n" +
                "\t\t, picturepath, rtfpath, enter, leave, speed\n" +
                "\t\t, stay_time, begintime, endtime\n" +
                "\tFROM (\n" +
                "\t\tSELECT id, LEVEL AS lvl, SYS_CONNECT_BY_PATH(ledid, '/') AS text\n" +
                "\t\t\t, SYS_CONNECT_BY_PATH(position, '/') AS text1, picturepath\n" +
                "\t\t\t, rtfpath, enter, leave, speed, stay_time\n" +
                "\t\t\t, begintime, endtime\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT id, t.ledid AS ledid, t1.position, t.picturepath, t.rtfpath\n" +
                "\t\t\t\t, t.enter, t.leave, t.speed, t.stay_time, t.begintime\n" +
                "\t\t\t\t, t.endtime, ROW_NUMBER() OVER (PARTITION BY id ORDER BY id, t.ledid) AS x\n" +
                "\t\t\tFROM enjoyorvms_proginfov3 t, enjoyorvms_ledinfov3 t1\n" +
                "\t\t\tWHERE t.ledid = t1.ledid\n" +
                "\t\t\tORDER BY id, ledid\n" +
                "\t\t) a\n" +
                "\t\tCONNECT BY id = PRIOR id\n" +
                "\t\tAND x - 1 = PRIOR x\n" +
                "\t)\n" +
                ")\n" +
                "WHERE rn = 1\n" +
                "ORDER BY id;", stmt.toString());
    }

}