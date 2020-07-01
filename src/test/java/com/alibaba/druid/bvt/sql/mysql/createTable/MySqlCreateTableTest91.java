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

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlCreateTableTest91 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE tbl_name(id int, sid int, name varchar(8)) " + //
	    "PARTITION BY LINEAR KEY ALGORITHM=2 (id, sid) PARTITIONS 4 (PARTITION p0, PARTITION p1, PARTITION p2, PARTITION p3)";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());
    }

    public void test_1() throws Exception {
        String sql = "CREATE TABLE tbl_name(id int, sid int, name varchar(8)) " + //
	    "PARTITION BY LINEAR KEY ALGORITHM=2 (id, sid) PARTITIONS 4 " + //
	    "SUBPARTITION BY LINEAR KEY ALGORITHM=2 (id, sid) SUBPARTITIONS 2 " + //
	    "(PARTITION p0 (SUBPARTITION s0, SUBPARTITION s1), " + //
	    "PARTITION p1 (SUBPARTITION s0, SUBPARTITION s1), " + //
	    "PARTITION p2 (SUBPARTITION s0, SUBPARTITION s1), " + //
	    "PARTITION p3 (SUBPARTITION s0, SUBPARTITION s1))";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());
    }
}
