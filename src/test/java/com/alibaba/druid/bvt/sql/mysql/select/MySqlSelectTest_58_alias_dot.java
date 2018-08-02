/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_58_alias_dot extends MysqlTest {

    public void test_0() throws Exception {
        String sql =  "SELECT m.*, m.icon AS micon, md.uid as md.uid, md.lastmsg,md.postnum,md.rvrc,md.money,md.credit,md.currency,md.lastvisit,md.thisvisit,md.onlinetime,md.lastpost,md.todaypost, md.monthpost,md.onlineip,md.uploadtime,md.uploadnum,md.starttime,md.pwdctime,md.monoltime,md.digests,md.f_num,md.creditpop, md.jobnum,md.lastgrab,md.follows,md.fans,md.newfans,md.newreferto,md.newcomment,md.postcheck,md.punch, mi.customdata " +
                "FROM pw_members m    LEFT JOIN pw_memberdata md ON m.uid=md.uid    LEFT JOIN pw_memberinfo mi ON mi.uid=m.uid WHERE m.uid IN (?)";

        System.out.println(sql);


        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
//        assertEquals(1, visitor.getTables().size());
//        assertEquals(1, visitor.getColumns().size());
//        assertEquals(0, visitor.getConditions().size());
//        assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("SELECT m.*, m.icon AS micon, md.uid AS md.uid, md.lastmsg, md.postnum\n" +
                            "\t, md.rvrc, md.money, md.credit, md.currency, md.lastvisit\n" +
                            "\t, md.thisvisit, md.onlinetime, md.lastpost, md.todaypost, md.monthpost\n" +
                            "\t, md.onlineip, md.uploadtime, md.uploadnum, md.starttime, md.pwdctime\n" +
                            "\t, md.monoltime, md.digests, md.f_num, md.creditpop, md.jobnum\n" +
                            "\t, md.lastgrab, md.follows, md.fans, md.newfans, md.newreferto\n" +
                            "\t, md.newcomment, md.postcheck, md.punch, mi.customdata\n" +
                            "FROM pw_members m\n" +
                            "\tLEFT JOIN pw_memberdata md ON m.uid = md.uid\n" +
                            "\tLEFT JOIN pw_memberinfo mi ON mi.uid = m.uid\n" +
                            "WHERE m.uid IN (?)", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select m.*, m.icon as micon, md.uid as md.uid, md.lastmsg, md.postnum\n" +
                            "\t, md.rvrc, md.money, md.credit, md.currency, md.lastvisit\n" +
                            "\t, md.thisvisit, md.onlinetime, md.lastpost, md.todaypost, md.monthpost\n" +
                            "\t, md.onlineip, md.uploadtime, md.uploadnum, md.starttime, md.pwdctime\n" +
                            "\t, md.monoltime, md.digests, md.f_num, md.creditpop, md.jobnum\n" +
                            "\t, md.lastgrab, md.follows, md.fans, md.newfans, md.newreferto\n" +
                            "\t, md.newcomment, md.postcheck, md.punch, mi.customdata\n" +
                            "from pw_members m\n" +
                            "\tleft join pw_memberdata md on m.uid = md.uid\n" +
                            "\tleft join pw_memberinfo mi on mi.uid = m.uid\n" +
                            "where m.uid in (?)", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT m.*, m.icon AS micon, md.uid AS md.uid, md.lastmsg, md.postnum\n" +
                            "\t, md.rvrc, md.money, md.credit, md.currency, md.lastvisit\n" +
                            "\t, md.thisvisit, md.onlinetime, md.lastpost, md.todaypost, md.monthpost\n" +
                            "\t, md.onlineip, md.uploadtime, md.uploadnum, md.starttime, md.pwdctime\n" +
                            "\t, md.monoltime, md.digests, md.f_num, md.creditpop, md.jobnum\n" +
                            "\t, md.lastgrab, md.follows, md.fans, md.newfans, md.newreferto\n" +
                            "\t, md.newcomment, md.postcheck, md.punch, mi.customdata\n" +
                            "FROM pw_members m\n" +
                            "\tLEFT JOIN pw_memberdata md ON m.uid = md.uid\n" +
                            "\tLEFT JOIN pw_memberinfo mi ON mi.uid = m.uid\n" +
                            "WHERE m.uid IN (?)", //
                    output);
        }
    }
}
