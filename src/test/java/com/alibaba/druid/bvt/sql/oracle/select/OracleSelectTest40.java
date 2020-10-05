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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest40 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "WITH a AS (" + //
                "   SELECT to_char(csl.create_time,'yyyyMMdd') create_time, cwl.client_key ck, csl.src_id src_id " + //
                "   FROM t0 csl, t1 cwl " + //
                "   WHERE 1 =1 AND csl.src_id = cwl.src_id AND csl.curr_url = cwl.curr_url " + //
                "       AND to_char(csl.create_time,'yyyyMMdd') BETWEEN ? " + //
                "       AND ? AND cwl.day = to_char(csl.create_time,'yyyyMMdd') " + //
                "   GROUP BY cwl.client_key, csl.src_id, csl.create_time ORDER BY csl.src_id )" + //
                ", b AS (" + //
                "   SELECT itn.buyerpaytime, itn.esc_orderid, itn.oldck, " + //
                "       MAX(con.PAYED_AMOUNT) gmv, MAX(con.buyer_onlyid) buyer " + //
                "   FROM t01 itn, t02 don, t03 con " + //
                "   WHERE don.esc_orderid = itn.esc_orderid " + //
                "       AND con.esc_orderid = itn.esc_orderid " + //
                "       AND don.order_status IN (4,5,6,7,8) AND itn.buyerpaytime BETWEEN ? AND ? " + //
                "   GROUP BY itn.esc_orderid, itn.oldck, itn.buyerpaytime ) " + //
                "SELECT MAX(cos.location) AS position , a.src_id AS srcid , " + //
                "   COUNT(DISTINCT b.esc_orderid) AS orders , SUM(b.gmv) AS uvGmv, " + //
                "   COUNT(DISTINCT buyer) AS buyers FROM a, b, cp_operate_statistics cos " + //
                "WHERE a.ck = b.oldck(+) AND to_char(cos.day,'yyyyMMdd') = a.create_time " + //
                "   AND a.create_time = b.buyerpaytime AND a.src_id = cos.src_id GROUP BY a.src_id ORDER BY a.src_id"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());
        
        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(6, visitor.getTables().size());

        Assert.assertEquals(18, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
