/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.parser;


import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

public class MysqlParserTest extends TestCase {

    public void test_0() throws Exception {
    	//防止delete 语句手误将where关键字写错导致删除全表
        String sql = "DELETE FROM t_order WHER id = 1";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        try {
        	parser.parseDeleteStatement();
        	Assert.assertFalse("parseDeleteStatement must throw exception",true);
		} catch (ParserException e) {
			String errMsg = "You have an error in your SQL syntax; "
					+ "check the manual that corresponds to your MySQL server version "
					+ "for the right syntax to use near 'id'";
			assertEquals(errMsg, e.getMessage());
		}
        
      //防止update 语句手误将where关键字写错导致更新全表
        sql = "update t_order set name = 'testName' WHER id = 1";
        parser = new MySqlStatementParser(sql);
        try {
        	parser.parseUpdateStatement();
        	Assert.assertFalse("parseUpdateStatement must throw exception",true);
		} catch (ParserException e) {
			String errMsg = "You have an error in your SQL syntax; "
					+ "check the manual that corresponds to your MySQL server version "
					+ "for the right syntax to use near 'WHER'";
			assertEquals(errMsg, e.getMessage());
		}
    }

    

    
}
