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
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

public class Kill_Test extends TestCase {

    public void test_0() throws Exception {
        String sql = "KILL  QUERY 233";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("KILL QUERY 233;", text);
    }
    
    public void test_1() throws Exception {
    	String sql = "KILL  CONNECTION 233";
    	
    	MySqlStatementParser parser = new MySqlStatementParser(sql);
    	List<SQLStatement> stmtList = parser.parseStatementList();
    	
    	String text = output(stmtList);
    	
    	Assert.assertEquals("KILL CONNECTION 233;", text);
    }

    public void test_2() throws Exception {
        String sql = "KILL 233";
        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        
        String text = output(stmtList);
        
        Assert.assertEquals("KILL 233;", text);
    }

    public void test_3() throws Exception {
        String sql = "KILL 233,234";
        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        
        String text = output(stmtList);
        
        Assert.assertEquals("KILL 233, 234;", text);
    }
    private String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();

        for (SQLStatement stmt : stmtList) {
            stmt.accept(new MySqlOutputVisitor(out));
            out.append(";");
        }

        return out.toString();
    }
}
