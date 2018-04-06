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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class MySqlCreateProcedureTest12 extends MysqlTest {

    public void test_0() throws Exception {
    	String sql = "CREATE PROCEDURE find_parts (\n" +
                "\tseed INT\n" +
                ")\n" +
                "BEGIN\n" +
                "\tDROP TABLE IF EXISTS _result;\n" +
                "\tCREATE TEMPORARY TABLE _result (\n" +
                "\t\tnode INT PRIMARY KEY\n" +
                "\t);\n" +
                "\tINSERT INTO _result\n" +
                "\tVALUES (seed);\n" +
                "\tDROP TABLE IF EXISTS _tmp;\n" +
                "\tCREATE TEMPORARY TABLE _tmp LIKE _result;\n" +
                "\tREPEAT \n" +
                "\t\tTRUNCATE TABLE _tmp;\n" +
                "\t\tINSERT INTO _tmp\n" +
                "\t\tSELECT child AS node\n" +
                "\t\tFROM _result\n" +
                "\t\t\tJOIN nodes ON node = parent;\n" +
                "\t\tINSERT IGNORE INTO _result\n" +
                "\t\tSELECT node\n" +
                "\t\tFROM _tmp;\n" +
                "\tUNTIL ROW_COUNT() = 0\n" +
                "\tEND REPEAT;\n" +
                "\tDROP TABLE _tmp;\n" +
                "\tSELECT *\n" +
                "\tFROM _result;\n" +
                "END;";

    	List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
    	SQLStatement stmt = statementList.get(0);
//    	print(statementList);
//        assertEquals(1, statementList.size());

        System.out.println(SQLUtils.toMySqlString(stmt));

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(3, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.containsColumn("_result", "node"));
        Assert.assertTrue(visitor.containsColumn("nodes", "parent"));
        Assert.assertTrue(visitor.containsColumn("nodes", "child"));
        Assert.assertTrue(visitor.containsColumn("_tmp", "node"));
    }

    
}
