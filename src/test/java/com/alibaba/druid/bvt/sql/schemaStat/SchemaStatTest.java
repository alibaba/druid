package com.alibaba.druid.bvt.sql.schemaStat;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

import junit.framework.TestCase;

public class SchemaStatTest extends TestCase {
    public void test_schemaStat() throws Exception {
        String sql = "select id as orderId from t_order order by orderId";
        
        SQLStatementParser parser = new SQLStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        
        SchemaStatVisitor statVisitor = new SchemaStatVisitor();
        stmt.accept(statVisitor);
        
        Assert.assertEquals(1, statVisitor.getColumns().size());
    }
}
