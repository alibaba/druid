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
package com.alibaba.druid.sql.parser;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ExportParameterVisitorUtils;
import com.alibaba.druid.sql.visitor.ParameterizedVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.google.common.collect.Lists;

public class ExportAndParameterizedVisitorTestCase extends TestCase {

    public void testParameterizedVisitor() {
        // final String sql =
        // "insert  into tab01(a,b,c) values('a1','bXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX1',5)";
        Object[][] sqlAndExpectedCases = {
                {"select * from test_tab1 where name='1' or name='3'",2,"1","true"},
                {"select * from test_tab1 where name='1' or name='3'",1,Lists.newArrayList("1","3"),"false"},
                { "insert  into tab01(a,b,c) values('a1','b1',5)", 3, "a1" },
                { "select * from tab01 where a=1 and b='b1'", 2, 1 }, 
                { "update tab01 set d='d1' where a=1 and b='b1'", 3, "d1" },
                { "delete from tab01 where a=1 and b='b1'", 2, 1 } };

        String[] dbTypes = { "mysql", "oracle", "db2" ,JdbcConstants.POSTGRESQL,JdbcUtils.JTDS,"not-found"};
        for (String dbType : dbTypes) {
            System.out.println("dbType:"+dbType);
            for (Object[] arr : sqlAndExpectedCases) {
                final String sql = (String) arr[0];
                StringBuilder out = new StringBuilder();

                final SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                final ParameterizedVisitor pVisitor = (ParameterizedVisitor) ExportParameterVisitorUtils.createExportParameterVisitor(out, dbType);
               if( arr.length > 3) {
            	   pVisitor.config(VisitorFeature.OutputParameterizedQuesUnMergeOr,"true".equals(arr[3]));
               }
                final SQLStatement parseStatement = parser.parseStatement();
                parseStatement.accept(pVisitor);
                final ExportParameterVisitor vistor2 = (ExportParameterVisitor) pVisitor;
                System.out.println("before:" + sql);
                System.out.println("after:" + out);
                System.out.println("params:" + vistor2.getParameters());
                final int expectedSize = arr.length>1 ?  (Integer) arr[1] : 0;
                Assert.assertEquals(expectedSize, vistor2.getParameters().size());
                if (arr.length > 2) {
                	Assert.assertEquals(arr[2],vistor2.getParameters().get(0));
                }
            }
        }
    }
}
