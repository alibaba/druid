package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcUtils;


public class PGSelectTest extends PGTest {
    
    protected void setUp() throws Exception {
        super.setUp();
        
        System.out.println();
    }

    public void test_0() throws Exception {
        String sql = "select    categoryId ,   offerIds    from cnres.function_select_get_spt_p4p_offer_list      ('    1031918   ,    1031919   ,    1037004   ')       as a(categoryId numeric,offerIds character varying(4000))";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);
//
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());


        Assert.assertEquals(2, visitor.getColumns().size());
        
        String mergedSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.POSTGRESQL);
        System.out.println(mergedSql);
    }
    
    public void test_1() throws Exception {
        String sql = "select    memberId ,   offerIds    from cnres.function_select_get_seller_hot_offer_list('\\'gzyyd168\\'')    as a(memberId character varying(20),offerIds character varying(4000))";
        
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);
        
        Assert.assertEquals(1, statementList.size());
        
        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);
        
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
        
        
        Assert.assertEquals(2, visitor.getColumns().size());
        
        String mergedSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.POSTGRESQL);
        System.out.println(mergedSql);
    }
    
    public void test_2() throws Exception {
        String sql = "            select    offerId ,   offerIds    from cnres.function_select_get_self_rel_offer_by_behavior      ('    350740   ')       as a(offerId numeric,offerIds character varying(4000))     ";
        
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);
        
        Assert.assertEquals(1, statementList.size());
        
        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);
        
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
        
        
        Assert.assertEquals(2, visitor.getColumns().size());
        
        String mergedSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.POSTGRESQL);
        System.out.println(mergedSql);
    }
    
    public void test_3() throws Exception {
        String sql = "            select    memberId ,   offerIds    from cnres.function_select_get_seller_hot_offer_list('\\'-1\\'')    as a(memberId character varying(20),offerIds character varying(4000))     ";
        
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);
        
        Assert.assertEquals(1, statementList.size());
        
        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);
        
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
        
        
        Assert.assertEquals(2, visitor.getColumns().size());
        
        String mergedSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcUtils.POSTGRESQL);
        System.out.println(mergedSql);
    }

}

//select    categoryId ,   offerIds    from cnres.function_select_get_spt_p4p_offer_list      ('    1031918   ,    1031919   ,    1037004   ')       as a(categoryId numeric,offerIds character varying(4000))     
//    select    memberId ,   offerIds    from cnres.function_select_get_seller_hot_offer_list('\'gzyyd168\'')    as a(memberId character varying(20),offerIds character varying(4000))

