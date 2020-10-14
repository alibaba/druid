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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Assert;

import java.util.List;

public class MySqlCreateTriggerTest extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TRIGGER ins_sum BEFORE INSERT ON account FOR EACH ROW SET @sum = @sum + NEW.amount;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
//        print(statementList);

        Assert.assertEquals(1, statementList.size());
    }

    public void test_1() throws Exception {
        String sql = "CREATE TRIGGER testref BEFORE INSERT ON test1" + " FOR EACH ROW" + " BEGIN"
                     + " INSERT INTO test2 SET a2 = NEW.a1;" + " DELETE FROM test3 WHERE a3 = NEW.a1;"
                     + " UPDATE test4 SET b4 = b4 + 1 WHERE a4 = NEW.a1;" + " END;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
//        print(statementList);

        Assert.assertEquals(1, statementList.size());
    }

}
