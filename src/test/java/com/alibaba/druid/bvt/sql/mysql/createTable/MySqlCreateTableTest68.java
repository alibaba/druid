/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;
import org.junit.Test;

public class MySqlCreateTableTest68 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE TABLE t1 ("
                + "\n\tyear_col  INT,"
                + "\n\tsome_data INT"
                + "\n)"
                + "\nPARTITION BY RANGE (year_col) ("
                + "    PARTITION p0 VALUES LESS THAN (1991),"
                + "    PARTITION p1 VALUES LESS THAN (1995),"
                + "    PARTITION p2 VALUES LESS THAN (1999),"
                + "    PARTITION p3 VALUES LESS THAN (2002),"
                + "    PARTITION p4 VALUES LESS THAN (2006),"
                + "    PARTITION p5 VALUES LESS THAN MAXVALUE"
                + ");";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE t1 ("
                    + "\n\tyear_col INT,"
                    + "\n\tsome_data INT"
                    + "\n)"
                    + "\nPARTITION BY RANGE COLUMNS (year_col) ("
                    + "\n\tPARTITION p0 VALUES LESS THAN (1991),"
                    + "\n\tPARTITION p1 VALUES LESS THAN (1995),"
                    + "\n\tPARTITION p2 VALUES LESS THAN (1999),"
                    + "\n\tPARTITION p3 VALUES LESS THAN (2002),"
                    + "\n\tPARTITION p4 VALUES LESS THAN (2006),"
                    + "\n\tPARTITION p5 VALUES LESS THAN MAXVALUE"
                    + "\n)", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table t1 ("
                    + "\n\tyear_col INT,"
                    + "\n\tsome_data INT"
                    + "\n)"
                    + "\npartition by range columns (year_col) ("
                    + "\n\tpartition p0 values less than (1991),"
                    + "\n\tpartition p1 values less than (1995),"
                    + "\n\tpartition p2 values less than (1999),"
                    + "\n\tpartition p3 values less than (2002),"
                    + "\n\tpartition p4 values less than (2006),"
                    + "\n\tpartition p5 values less than maxvalue"
                    + "\n)", output);
        }
    }
}
