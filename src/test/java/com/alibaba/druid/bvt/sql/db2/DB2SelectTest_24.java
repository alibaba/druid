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
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class DB2SelectTest_24 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "SELECT BANK_CODE, CONN_LOCATION_TYPE,\n" +
                "OUTER_KEYLABEL_NAME, INNER_KEYLABEL_NAME,\n" +
                "DESC, COMMENT, STATUS, DB_TIMESTAMP\n" +
                "FROM EGL_SYS_KEYLABEL_CONVERT_DEF\n" +
                "WHERE STATUS='1'";

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
        Assert.assertEquals(1, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("EGL_SYS_KEYLABEL_CONVERT_DEF")));

//         Assert.assertTrue(visitor.getColumns().contains(new Column("DSN8B10.EMP", "WORKDEPT")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        Assert.assertEquals("SELECT BANK_CODE, CONN_LOCATION_TYPE, OUTER_KEYLABEL_NAME, INNER_KEYLABEL_NAME, DESC\n" +
                        "\t, COMMENT, STATUS, DB_TIMESTAMP\n" +
                        "FROM EGL_SYS_KEYLABEL_CONVERT_DEF\n" +
                        "WHERE STATUS = '1'", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2));
        
        Assert.assertEquals("select BANK_CODE, CONN_LOCATION_TYPE, OUTER_KEYLABEL_NAME, INNER_KEYLABEL_NAME, DESC\n" +
                        "\t, COMMENT, STATUS, DB_TIMESTAMP\n" +
                        "from EGL_SYS_KEYLABEL_CONVERT_DEF\n" +
                        "where STATUS = '1'", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
