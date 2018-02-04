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
package com.alibaba.druid.bvt.sql.db2;

import com.alibaba.druid.sql.DB2Test;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class DB2CreateTableTest_03 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "   CREATE TABLE DSN8A10.PROJ\n" +
                "     (PROJNO   CHAR(6)      NOT NULL,\n" +
                "      PROJNAME VARCHAR(24)  NOT NULL,\n" +
                "      DEPTNO   CHAR(3)      NOT NULL,\n" +
                "      RESPEMP  CHAR(6)      NOT NULL,\n" +
                "      PRSTAFF  DECIMAL(5,2)         ,\n" +
                "      PRSTDATE DATE                 ,\n" +
                "      PRENDATE DATE                 ,\n" +
                "      MAJPROJ  CHAR(6)      NOT NULL)\n" +
                "     IN DATABASE DSN8D10A\n" +
                "     VALIDPROC DSN8EAPR;";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        System.out.println(SQLUtils.toDB2String(stmt));

        Assert.assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(8, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.containsTable("DSN8A10.PROJ"));

//         Assert.assertTrue(visitor.getColumns().contains(new Column("DSN8B10.EMP", "WORKDEPT")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        Assert.assertEquals("CREATE TABLE DSN8A10.PROJ (\n" +
                        "\tPROJNO CHAR(6) NOT NULL,\n" +
                        "\tPROJNAME VARCHAR(24) NOT NULL,\n" +
                        "\tDEPTNO CHAR(3) NOT NULL,\n" +
                        "\tRESPEMP CHAR(6) NOT NULL,\n" +
                        "\tPRSTAFF DECIMAL(5, 2),\n" +
                        "\tPRSTDATE DATE,\n" +
                        "\tPRENDATE DATE,\n" +
                        "\tMAJPROJ CHAR(6) NOT NULL\n" +
                        ")\n" +
                        "IN DATABASE DSN8D10A\n" +
                        "VALIDPROC DSN8EAPR;", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2));
        
        Assert.assertEquals("create table DSN8A10.PROJ (\n" +
                        "\tPROJNO CHAR(6) not null,\n" +
                        "\tPROJNAME VARCHAR(24) not null,\n" +
                        "\tDEPTNO CHAR(3) not null,\n" +
                        "\tRESPEMP CHAR(6) not null,\n" +
                        "\tPRSTAFF DECIMAL(5, 2),\n" +
                        "\tPRSTDATE DATE,\n" +
                        "\tPRENDATE DATE,\n" +
                        "\tMAJPROJ CHAR(6) not null\n" +
                        ")\n" +
                        "IN DATABASE DSN8D10A\n" +
                        "VALIDPROC DSN8EAPR;", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
