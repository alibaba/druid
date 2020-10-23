package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

public class PGUnionTest extends TestCase  {

    public void testUnion(){
        String sql = "(select id,name from t1) union (select id,name from t2)";
        String targetSql = "(SELECT id, name\n"
                + "FROM t1)\n"
                + "UNION\n"
                + "(SELECT id, name\n"
                + "FROM t2)";

        PGSQLStatementParser pgparser=new PGSQLStatementParser(sql);
        SQLStatement pgstatement = pgparser.parseStatement();
        Assert.assertEquals(targetSql, pgstatement.toString());
        System.out.println(pgstatement.toString());
    }
//
//
//    public void testUnion1(){
//        String sql = "select * from test_a where id=(select id from test_b where id = 1) union all select * from test_c";
//        String targetSql = "(SELECT id, name\n"
//                + "FROM t1)\n"
//                + "UNION\n"
//                + "(SELECT id, name\n"
//                + "FROM t2)";
//
//        com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser pgparser=new com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser(sql);
//        com.alibaba.druid.sql.ast.SQLStatement pgstatement = pgparser.parseStatement();
//        Assert.assertEquals(targetSql, pgstatement.toString());
//        System.out.println(pgstatement.toString());
//    }
}