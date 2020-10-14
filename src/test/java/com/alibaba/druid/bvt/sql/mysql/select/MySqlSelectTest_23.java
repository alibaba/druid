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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.ads.parser.AdsStatementParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.repository.SchemaRepository;
import org.junit.Assert;

import java.util.List;

public class MySqlSelectTest_23 extends MysqlTest {

    public void test_0() throws Exception {

        SchemaRepository repository = new SchemaRepository(DbType.ads);
        repository.acceptDDL("CREATE TABLE linxi_test.test_realtime1 (\n" + "  id bigint NOT NULL COMMENT '',\n"
                             + "  int_test1 bigint NOT NULL COMMENT '',\n" + "  boolean_test boolean COMMENT '',\n"
                             + "  byte_test tinyint COMMENT '',\n" + "  short_test smallint COMMENT '',\n"
                             + "  int_test2 int COMMENT '',\n" + "  float_test float COMMENT '',\n"
                             + "  string_test varchar COMMENT '',\n" + "  date_test date COMMENT '',\n"
                             + "  time_test time COMMENT '',\n" + "  timestamp_test timestamp COMMENT '',\n"
                             + "  double_test double COMMENT '',\n" + "  INDEX id_index HASH (string_test),\n"
                             + "  PRIMARY KEY (id,int_test1,int_test2)\n" + ")\n"
                             + "PARTITION BY HASH KEY (id) PARTITION NUM 10\n" + "TABLEGROUP group2\n"
                             + "OPTIONS (UPDATETYPE='realtime')\n" + "COMMENT ''");


//        String sql = "INSERT INTO test_realtime1(id, int_test1, int_test2, string_test, date_test, time_test, timestamp_test)\n"
//                     + "VALUES (2, 2, 2, 'string', '2017-1-3', '12:00:00', '2017-1-3 12:00:00');";
//        String sql = "delete test_realtime1 where id ='cailijun'";
        String sql = "SELECT  date_test, time_test, timestamp_test from test_realtime1 where timestamp_test = \"2017-01-02 12:00:00\"";

        AdsStatementParser parser = new AdsStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        repository.resolve(statemen);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);



//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());
    }
    
    
    
}
