package com.alibaba.druid.bvt.sql.schemaStat;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

public class SchemaStatTest3 extends TestCase {
    public void test_schemaStat() throws Exception {
        String sql = "select count(*) from t";
        
        
        String dbType = JdbcConstants.MYSQL;
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatementList().get(0);
        
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);
        
        System.out.println(visitor.getColumns());
        
        Assert.assertEquals(1, visitor.getColumns().size());
        Assert.assertTrue(visitor.containsColumn("t", "*"));
    }
}
