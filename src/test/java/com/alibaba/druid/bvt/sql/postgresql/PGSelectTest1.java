package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;

public class PGSelectTest1 extends PGTest {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM t1 CROSS JOIN t2;";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getTables().size());
    }

}

// select categoryId , offerIds from cnres.function_select_get_spt_p4p_offer_list (' 1031918 , 1031919 , 1037004 ') as
// a(categoryId numeric,offerIds character varying(4000))
// select memberId , offerIds from cnres.function_select_get_seller_hot_offer_list('\'gzyyd168\'') as a(memberId
// character varying(20),offerIds character varying(4000))

