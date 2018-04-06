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
package com.alibaba.druid.bvt.sql.mysql.createTable;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class MySqlCreateTableTest49 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "    create table tauth (" //
                     + "        cid varchar(36) not null unique,"//
                     + "        cdesc varchar(200),"//
                     + "        cname varchar(100) not null,"//
                     + "        cseq decimal(22,0),"//
                     + "        curl varchar(200),"//
                     + "        cpid varchar(36),"//
                     + "        primary key (cid)"//
                     + "    )"; //

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(6, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("tauth")));

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE TABLE tauth (" //
                            + "\n\tcid varchar(36) NOT NULL UNIQUE,"//
                            + "\n\tcdesc varchar(200),"//
                            + "\n\tcname varchar(100) NOT NULL,"//
                            + "\n\tcseq decimal(22, 0),"//
                            + "\n\tcurl varchar(200),"//
                            + "\n\tcpid varchar(36),"//
                            + "\n\tPRIMARY KEY (cid)"//
                            + "\n)",//
                            output);

    }
}
