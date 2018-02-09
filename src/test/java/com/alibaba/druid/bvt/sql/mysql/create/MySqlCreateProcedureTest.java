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

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlCreateProcedureTest extends MysqlTest {

    public void test_0() throws Exception {
    	String sql="create or replace procedure sp_name(level int,age int)"+
				" begin"+
				" declare x,y,z int;"+
				" select id into x,y,z from test;"+
				" insert into test values(id,age);"+
				" while x do"+
				" insert into test values(id,age);"+
				" end while;"+
				" if x then"+
				" insert into test values(id,age);"+
				" insert into test values(id,age);"+
				" else if y then"+
				" insert into test values(id,age);"+
				" while x do"+
				" insert into test values(id,age);"+
				" end while;"+
				" else"+
				" insert into test values(id,age);"+
				" end if;"+
				" case x"+
				" when x>10 then"+
				" insert into test values(id,age);"+
				" insert into test values(id,age);"+
				" when x>20 then"+
				" insert into test values(id,age);"+
				" insert into test values(id,age);"+
				" else"+
				" insert into test values(id,age);"+
				" end case;"+
				" end";
	
    	MySqlStatementParser parser=new MySqlStatementParser(sql);
    	List<SQLStatement> statementList = parser.parseStatementList();
    	SQLStatement stmt = statementList.get(0);
//    	print(statementList);
        Assert.assertEquals(1, statementList.size());

		System.out.println(stmt);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
		stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(1, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("test")));
        
        Assert.assertTrue(visitor.containsColumn("test", "id"));
    }
}
