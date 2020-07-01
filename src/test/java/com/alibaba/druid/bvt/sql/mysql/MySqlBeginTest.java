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
package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlBeginTest extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "START TRANSACTION;"
                + "\nSELECT @A:=SUM(salary) FROM table1 WHERE type=1;"
                + "\nUPDATE table2 SET summary=@A WHERE type=1;"
                + "\nCOMMIT;"
                + "\n";

        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
//        print(statementList);

        Assert.assertEquals(4, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        for (SQLStatement stmt : statementList) {
            stmt.accept(visitor);
        }

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toSQLString(statementList, JdbcConstants.MYSQL);
            assertEquals("START TRANSACTION;"
                    + "\n"
                    + "\nSELECT @A := SUM(salary)"
                    + "\nFROM table1"
                    + "\nWHERE type = 1;"
                    + "\n"
                    + "\nUPDATE table2"
                    + "\nSET summary = @A"
                    + "\nWHERE type = 1;"
                    + "\n"
                    + "\nCOMMIT;", //
                                output);
        }
        {
            String output = SQLUtils.toSQLString(statementList, JdbcConstants.MYSQL, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("start transaction;"
                    + "\n"
                    + "\nselect @A := sum(salary)"
                    + "\nfrom table1"
                    + "\nwhere type = 1;"
                    + "\n"
                    + "\nupdate table2"
                    + "\nset summary = @A"
                    + "\nwhere type = 1;"
                    + "\n"
                    + "\ncommit;", //
                                output);
        }
    }
    
    
    
}
