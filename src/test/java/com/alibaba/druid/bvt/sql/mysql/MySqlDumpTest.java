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
package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlDumpTest extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "dump data into 'odps://ktv_market_analyzing/bohan_test_001/ds=20171233/seller_bucket=9727' " +
                "select '6225607' as crowd_snapshot_id, a.seller_id as seller_id, a.buyer_id as buyer_id " +
                "from amp.otpx_buyer_seller_relation_detail_to_garuda a " +
                "join amp.otp_user_base_info b on b.user_id= a.buyer_id " +
                "where(((a.seller_id= 765844183 and a.seller_zone= 765844183%20+ 1000)) and((a.last_pay_date>= 20151226 and a.last_pay_date<= 20171215)) and((a.is_market_target in('3'))))";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(2, visitor.getTables().size());
        assertEquals(6, visitor.getColumns().size());
        assertEquals(7, visitor.getConditions().size());

        assertTrue(visitor.containsTable("amp.otp_user_base_info"));
        assertTrue(visitor.containsTable("amp.otpx_buyer_seller_relation_detail_to_garuda"));
//        assertTrue(visitor.getTables().containsKey(new TableStat.Name("t2")));
        
        assertTrue(visitor.containsColumn("amp.otp_user_base_info", "user_id"));
        assertTrue(visitor.containsColumn("amp.otpx_buyer_seller_relation_detail_to_garuda", "buyer_id"));
        assertTrue(visitor.containsColumn("amp.otpx_buyer_seller_relation_detail_to_garuda", "seller_id"));
        assertTrue(visitor.containsColumn("amp.otpx_buyer_seller_relation_detail_to_garuda", "seller_zone"));
        assertTrue(visitor.containsColumn("amp.otpx_buyer_seller_relation_detail_to_garuda", "is_market_target"));
        assertTrue(visitor.containsColumn("amp.otpx_buyer_seller_relation_detail_to_garuda", "last_pay_date"));
    }
}
