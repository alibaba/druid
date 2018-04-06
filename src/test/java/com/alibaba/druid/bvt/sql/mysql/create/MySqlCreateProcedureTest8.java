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
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

/**
 * 
 * @Description: begin-end within begin-end
 * @author zhujun [455910092@qq.com]
 * @date 2016-4-14
 * @version V1.0
 */
public class MySqlCreateProcedureTest8 extends MysqlTest {

	/**
	 * DECLARE handler_type HANDLER FOR condition_value[,...] sp_statement handler_type: CONTINUE | EXIT condition_value: SQLSTATE [VALUE] sqlstate_value | condition_name | SQLWARNING | NOT FOUND | SQLEXCEPTION | mysql_error_code
	 * @throws Exception
	 */
    public void test_0() throws Exception {
    	String sql="create or replace procedure test_cursor (in param int(10),out result varchar(90))  "
    			+" begin" 
    			+" declare name varchar(20);"  
    			+" declare pass varchar(20);"  
    			+" declare done int;"  
    			+" declare cur_test CURSOR for select user_name,user_pass from test;"  
    			+" declare continue handler FOR SQLSTATE '02000' SET done = 1;"  
    			+" if param then"  
    			+" 		select concat_ws(',',user_name,user_pass) into result from test.users where id=param;"  
    			+" else"  
    			+" 		open cur_test;"  
    			+" 		repeat"  
    			+" 		fetch cur_test into name, pass;"  
    			+" 		select concat_ws(',',result,name,pass) into result;"  
    			+" 		until done end repeat;"  
    			+" 		close cur_test;"  
    			+" end if;"  
    			+" end;";

    	List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
    	SQLStatement stmt = statementList.get(0);
		System.out.println(SQLUtils.toSQLString(stmt, JdbcConstants.MYSQL));
//    	print(statementList);
        assertEquals(1, statementList.size());

		System.out.println(stmt);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(2, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());
    }
    
    public void test_1() throws Exception {
    	String sql = "create or replace procedure sp_name(level int,age int)"+
				" begin"+
				" declare continue handler FOR SQLSTATE '02000' SET done = 1;"+
				" end";
    	
    	MySqlStatementParser parser=new MySqlStatementParser(sql);
    	List<SQLStatement> statementList = parser.parseStatementList();
    	SQLStatement statemen = statementList.get(0);
//    	print(statementList);
        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }
    
    public void test_2() throws Exception {
    	String sql = "create or replace procedure sp_name(level int,age int)"+
				" begin"+
				" declare continue handler FOR SQLEXCEPTION,SQLWARNING SET done = 1;"+
				" end";
    	
    	MySqlStatementParser parser=new MySqlStatementParser(sql);
    	List<SQLStatement> statementList = parser.parseStatementList();
    	SQLStatement statemen = statementList.get(0);
//    	print(statementList);
        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }
    
    public void test_3() throws Exception {
    	String sql = "create or replace procedure sp_name(level int,age int)"+
				" begin"+
				" declare continue handler FOR 1002 SET done = 1;"+
				" end";
    	
    	MySqlStatementParser parser=new MySqlStatementParser(sql);
    	List<SQLStatement> statementList = parser.parseStatementList();
    	SQLStatement statemen = statementList.get(0);
//    	print(statementList);
        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }
    
    public void test_4() throws Exception {
    	String sql = "create or replace procedure sp_name(level int,age int)"+
				" begin"+
				" declare continue handler FOR SQLWARNING begin set done = 1; end"+
				" end";
    	
    	MySqlStatementParser parser=new MySqlStatementParser(sql);
    	List<SQLStatement> statementList = parser.parseStatementList();
    	SQLStatement statemen = statementList.get(0);
//    	print(statementList);
        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }
    
    public void test_5() throws Exception {
    	String sql = "create or replace procedure sp_name(level int,age int)"+
				" begin"+
				" declare continue handler FOR SQLWARNING begin set done = 1; end;"+
				" end";
    	
    	MySqlStatementParser parser=new MySqlStatementParser(sql);
    	List<SQLStatement> statementList = parser.parseStatementList();
    	SQLStatement statemen = statementList.get(0);
//    	print(statementList);
        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }
}
