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
import org.junit.Test;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlCreateTableTest73 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE TABLE total ("
                + "    id INT NOT NULL AUTO_INCREMENT,"
                + "    message CHAR(20), INDEX(a))"
                + "    ENGINE=MERGE UNION=(t1,t2) INSERT_METHOD=LAST;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        Column column = visitor.getColumn("total", "id");
        Assert.assertNotNull(column);
        Assert.assertEquals("INT", column.getDataType());

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE total ("
                    + "\n\tid INT NOT NULL AUTO_INCREMENT,"
                    + "\n\tmessage CHAR(20),"
                    + "\n\tINDEX(a)"
                    + "\n) ENGINE = MERGE UNION = (t1, t2) INSERT_METHOD = LAST", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table total ("
                    + "\n\tid INT not null auto_increment,"
                    + "\n\tmessage CHAR(20),"
                    + "\n\tindex(a)"
                    + "\n) engine = MERGE union = (t1, t2) insert_method = LAST", output);
        }
    }
}
