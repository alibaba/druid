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
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

/**
 * 
 * Description: 测试异常声明
 * author zhujun [455910092@qq.com]
 * date 2016-4-17
 * version V1.0
 */
public class MySqlCreateProcedureTest9 extends MysqlTest {

    public void test_0() throws Exception {
    	String sql = "create or replace procedure sp_name(level int,age int)"+
				" begin"+
				" declare test1 CONDITION FOR SQLSTATE '02000';"+
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
				" declare condition_name CONDITION FOR 1002;"+
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
    	String sql="create or replace procedure test_cursor (in param int(10),out result varchar(90))  "
    			+" begin" 
    			+" declare name varchar(20);"  
    			+" declare pass varchar(20);"  
    			+" declare done int;"  
    			+" declare cur_test CURSOR for select user_name,user_pass from test;" 
    			+" declare condition_name CONDITION FOR 1002;"
    			+" declare continue handler FOR condition_name SET done = 1;"  
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
	
    	MySqlStatementParser parser=new MySqlStatementParser(sql);
    	List<SQLStatement> statementList = parser.parseStatementList();
    	SQLStatement statemen = statementList.get(0);
		System.out.println(SQLUtils.toSQLString(statementList, JdbcConstants.MYSQL));
		Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());
    }
    
}
