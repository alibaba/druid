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

import org.junit.Assert;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ExportParameterVisitorUtils;
import com.alibaba.druid.sql.visitor.ParameterizedVisitor;

import junit.framework.TestCase;

public class ExportAndParameterizedVisitor4OracleTestCase extends TestCase {

    public void testParameterizedVisitor() {
         Object[][] sqlAndExpectedCases = {
        		{ "SELECT XMLSERIALIZE(CONTENT XMLTYPE('<Owner>Grandco</Owner>') as varchar(200) ) AS xmlserialize_doc   FROM DUAL",1},
             };

        final DbType[] dbTypes = { DbType.oracle };
        for (final DbType dbType : dbTypes) {
            System.out.println("dbType:" + dbType);
            for (Object[] arr : sqlAndExpectedCases) {

                final String sql = (String) arr[0];
                StringBuilder out = new StringBuilder();

                final SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                final ParameterizedVisitor pVisitor = (ParameterizedVisitor) ExportParameterVisitorUtils.createExportParameterVisitor(out, dbType);
                final SQLStatement parseStatement = parser.parseStatement();
                parseStatement.accept(pVisitor);
                // final ExportParameterVisitor vistor2 = new MySqlExportParameterVisitor();
                // parseStatement.accept(vistor2);
                final ExportParameterVisitor vistor2 = (ExportParameterVisitor) pVisitor;
                System.out.println("before:" + sql);
                System.out.println("after:" + out);
                System.out.println("size:" + vistor2.getParameters());
                final int expectedSize = arr.length > 1 ?  (Integer) arr[1] : 0 ;
                Assert.assertEquals(expectedSize, vistor2.getParameters().size());
            }
        }
    }
}
