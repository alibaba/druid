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
package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class OdpsSelectTest26 extends TestCase {

    public void test_select() throws Exception {
        // 1095288847322
        String sql = "select count(distinct(trackid)) as total , process ,value5 as result\n" +
                "    from \n" +
                "    havanaapp.s_register_process_log  where \n" +
                "    ds='20170706' and value6='MOBILE_TB' and value1 REGEXP '^00'\n" +
                "    and process in('preCheckMobile','VerifyScrollCheckcode','SendMobileCheckcode','VerifyMobile',\n" +
                "    'VerifyMobileCheckCode','VerifyNick','VerifyPasswordFormat','register','CheckMobileConflict','VerifyEmail') \n" +
                "    group  by process,value5";//
        assertEquals("SELECT COUNT(DISTINCT trackid) AS total, process, value5 AS result\n" +
                "FROM havanaapp.s_register_process_log\n" +
                "WHERE ds = '20170706'\n" +
                "\tAND value6 = 'MOBILE_TB'\n" +
                "\tAND value1 REGEXP '^00'\n" +
                "\tAND process IN (\n" +
                "\t\t'preCheckMobile', \n" +
                "\t\t'VerifyScrollCheckcode', \n" +
                "\t\t'SendMobileCheckcode', \n" +
                "\t\t'VerifyMobile', \n" +
                "\t\t'VerifyMobileCheckCode', \n" +
                "\t\t'VerifyNick', \n" +
                "\t\t'VerifyPasswordFormat', \n" +
                "\t\t'register', \n" +
                "\t\t'CheckMobileConflict', \n" +
                "\t\t'VerifyEmail'\n" +
                "\t)\n" +
                "GROUP BY process, \n" +
                "\tvalue5", SQLUtils.formatOdps(sql));

        assertEquals("select count(DISTINCT trackid) as total, process, value5 as result\n" +
                "from havanaapp.s_register_process_log\n" +
                "where ds = '20170706'\n" +
                "\tand value6 = 'MOBILE_TB'\n" +
                "\tand value1 regexp '^00'\n" +
                "\tand process in (\n" +
                "\t\t'preCheckMobile', \n" +
                "\t\t'VerifyScrollCheckcode', \n" +
                "\t\t'SendMobileCheckcode', \n" +
                "\t\t'VerifyMobile', \n" +
                "\t\t'VerifyMobileCheckCode', \n" +
                "\t\t'VerifyNick', \n" +
                "\t\t'VerifyPasswordFormat', \n" +
                "\t\t'register', \n" +
                "\t\t'CheckMobileConflict', \n" +
                "\t\t'VerifyEmail'\n" +
                "\t)\n" +
                "group by process, \n" +
                "\tvalue5", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());
        
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);
        
//        System.out.println("Tables : " + visitor.getTables());
      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(1, visitor.getTables().size());
        assertEquals(6, visitor.getColumns().size());
        assertEquals(3, visitor.getConditions().size());

//        System.out.println(SQLUtils.formatOdps(sql));
        
//        assertTrue(visitor.getColumns().contains(new Column("abc", "name")));
    }


}
