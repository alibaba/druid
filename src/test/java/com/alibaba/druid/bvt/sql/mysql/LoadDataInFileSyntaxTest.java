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
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class LoadDataInFileSyntaxTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "LOAD DATA INFILE 'data.txt' INTO TABLE db2.my_table;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("LOAD DATA INFILE 'data.txt' INTO TABLE db2.my_table;", text);
    }

    public void test_1() throws Exception {
        String sql = "LOAD DATA INFILE '/tmp/test.txt' INTO TABLE test FIELDS TERMINATED BY ','  LINES STARTING BY 'xxx';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("LOAD DATA INFILE '/tmp/test.txt' INTO TABLE test COLUMNS TERMINATED BY ',' LINES STARTING BY 'xxx';",
                            text);
    }

    public void test_2() throws Exception {
        String sql = "LOAD DATA INFILE '/home/Order.txt' INTO TABLE Orders (Order_Number, Order_Date, Customer_ID);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("LOAD DATA INFILE '/home/Order.txt' INTO TABLE Orders (Order_Number, Order_Date, Customer_ID);",
                            text);
    }

    public void test_3() throws Exception {
        String sql = "LOAD DATA INFILE 'data.txt' INTO TABLE tbl_name FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("LOAD DATA INFILE 'data.txt' INTO TABLE tbl_name COLUMNS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES;",
                            text);
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
