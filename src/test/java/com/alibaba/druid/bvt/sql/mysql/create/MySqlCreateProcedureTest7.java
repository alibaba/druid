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
import org.junit.Assert;

import java.util.List;

/**
 * 
 * @Description: create procedure parameter type support
 * @author zz email:455910092@qq.com
 * @date 2015-9-14
 * @version V1.0
 */
public class MySqlCreateProcedureTest7 extends MysqlTest {

    public void test_0() throws Exception {
    	String sql="CREATE DEFINER=test@% PROCEDURE test11111()"
    	        + "\nBEGIN"
    	        + "\ndeclare v_a date default '2007-4-10';"
    	        + "\ndeclare v_b date default '2007-4-11';"
    	        + "\ndeclare v_c datetime default '2004-4-9 0:0:0';"
    	        + "\nEND";
	
    	MySqlStatementParser parser=new MySqlStatementParser(sql);
    	List<SQLStatement> statementList = parser.parseStatementList();
    	SQLStatement stmt = statementList.get(0);
//    	print(statementList);
        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE PROCEDURE test11111 ()"
                + "\nBEGIN"
                + "\n\tDECLARE v_a date DEFAULT '2007-4-10';"
                + "\n\tDECLARE v_b date DEFAULT '2007-4-11';"
                + "\n\tDECLARE v_c datetime DEFAULT '2004-4-9 0:0:0';"
                + "\nEND", output);
        
        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }
}
