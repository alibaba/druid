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
package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class MySqlCreateTableTest45 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE th (id INT, name VARCHAR(30), adate DATE)" + //
                     "PARTITION BY LIST(YEAR(adate))" + //
                     "(" + //
                     "  PARTITION p1999 VALUES IN (1995, 1999, 2003)" + //
                     "    DATA DIRECTORY = '/var/appdata/95/data'" + //
                     "    INDEX DIRECTORY = '/var/appdata/95/idx'," + //
                     "  PARTITION p2000 VALUES IN (1996, 2000, 2004)" + //
                     "    DATA DIRECTORY = '/var/appdata/96/data'" + //
                     "    INDEX DIRECTORY = '/var/appdata/96/idx'," + //
                     "  PARTITION p2001 VALUES IN (1997, 2001, 2005)" + //
                     "    DATA DIRECTORY = '/var/appdata/97/data'" + //
                     "    INDEX DIRECTORY = '/var/appdata/97/idx'," + //
                     "  PARTITION p2002 VALUES IN (1998, 2002, 2006)" + //
                     "    DATA DIRECTORY = '/var/appdata/98/data'" + //
                     "    INDEX DIRECTORY = '/var/appdata/98/idx'" + //
                     ");"; //

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("th")));

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE TABLE th (" + //
                            "\n\tid INT, " + //
                            "\n\tname VARCHAR(30), " + //
                            "\n\tadate DATE" + //
                            "\n) PARTITION BY LIST (YEAR(adate)) (" + //
                            "\n\tPARTITION p1999 VALUES IN (1995, 1999, 2003)" + //
                            "\n\t\tDATA DIRECTORY '/var/appdata/95/data'" + //
                            "\n\t\tINDEX DIRECTORY '/var/appdata/95/idx', " + //
                            "\n\tPARTITION p2000 VALUES IN (1996, 2000, 2004)" + //
                            "\n\t\tDATA DIRECTORY '/var/appdata/96/data'" + //
                            "\n\t\tINDEX DIRECTORY '/var/appdata/96/idx', " + //
                            "\n\tPARTITION p2001 VALUES IN (1997, 2001, 2005)" + //
                            "\n\t\tDATA DIRECTORY '/var/appdata/97/data'" + //
                            "\n\t\tINDEX DIRECTORY '/var/appdata/97/idx', " + //
                            "\n\tPARTITION p2002 VALUES IN (1998, 2002, 2006)" + //
                            "\n\t\tDATA DIRECTORY '/var/appdata/98/data'" + //
                            "\n\t\tINDEX DIRECTORY '/var/appdata/98/idx'" + //
                            "\n)", output);

    }
}
