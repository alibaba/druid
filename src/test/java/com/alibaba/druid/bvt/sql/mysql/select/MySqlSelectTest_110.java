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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import junit.framework.TestCase;

import java.util.List;

public class MySqlSelectTest_110 extends TestCase {

    public void test_0() throws Exception {
        String sql = "/*+engine=MPP*/select id \n" +
                "  from ads_service_buyer_task\n" +
                " where service_name = '灯具安装' \n" +
                "     and  split_to_map(attribute , ';'  ,  ':')['servPrice'] != null\n" +
                "     and id not in(\n" +
                "                select biz_id \n" +
                "                  from ads_service_monitor_message where biz_type=2\n" +
                "             ) \n" +
                "             limit 0,1;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, statementList.size());
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
//        print(statementList);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(2, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(3, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertEquals("/*+engine=MPP*/\n" +
                "SELECT id\n" +
                "FROM ads_service_buyer_task\n" +
                "WHERE service_name = '灯具安装'\n" +
                "\tAND split_to_map(attribute, ';', ':')['servPrice'] != NULL\n" +
                "\tAND id NOT IN (\n" +
                "\t\tSELECT biz_id\n" +
                "\t\tFROM ads_service_monitor_message\n" +
                "\t\tWHERE biz_type = 2\n" +
                "\t)\n" +
                "LIMIT 0, 1;", stmt.toString());
    }
    
    
    
}
