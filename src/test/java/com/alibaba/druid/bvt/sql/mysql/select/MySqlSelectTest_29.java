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

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class MySqlSelectTest_29 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select * from Function "
                + "where Id in (select FunctionId from RoleFunction "
                + "where RoleId = '001' and LogicalDel = 0) and LogicalDel = 0";

        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(6, visitor.getColumns().size());
        Assert.assertEquals(4, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("SELECT *\n" +
                            "FROM Function\n" +
                            "WHERE Id IN (\n" +
                            "\t\tSELECT FunctionId\n" +
                            "\t\tFROM RoleFunction\n" +
                            "\t\tWHERE RoleId = '001'\n" +
                            "\t\t\tAND LogicalDel = 0\n" +
                            "\t)\n" +
                            "\tAND LogicalDel = 0", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select *\n" +
                            "from Function\n" +
                            "where Id in (\n" +
                            "\t\tselect FunctionId\n" +
                            "\t\tfrom RoleFunction\n" +
                            "\t\twhere RoleId = '001'\n" +
                            "\t\t\tand LogicalDel = 0\n" +
                            "\t)\n" +
                            "\tand LogicalDel = 0", //
                                output);
        }
    }
    
    
    
}
