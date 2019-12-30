package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

public class PGUnionTest
        extends TestCase  {

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
//    public void testUnion1(){
//        String sql = "select * from test_a where id=(select id from test_b where id = 1) union all select * from test_c";
//        String targetSql = "SELECT *\n" +
//                "FROM test_a\n" +
//                "WHERE id = (\n" +
//                "\tSELECT id\n" +
//                "\tFROM test_b\n" +
//                "\tWHERE id = 1\n" +
//                ")\n" +
//                "UNION ALL\n" +
//                "SELECT *\n" +
//                "FROM test_c";
//
//        PGSQLStatementParser pgparser=new PGSQLStatementParser(sql);
//        SQLStatement pgstatement = pgparser.parseStatement();
//        Assert.assertEquals(targetSql, pgstatement.toString());
//        System.out.println(pgstatement.toString());
//    }

    public void testUnion2(){
        String sql = "select                                                                                                                                                    \n" +
                "(                                                                                                                                                              \n" +
                " (                                                                                                                                                             \n" +
                "  select 'y' from dual                                                                                                                                         \n" +
                "  where exists ( select 1 from dual where 1 = 0 )                                                                                                              \n" +
                " )                                                                                                                                                             \n" +
                " union                                                                                                                                                         \n" +
                " (                                                                                                                                                             \n" +
                "  select 'n' from dual                                                                                                                                         \n" +
                "  where not exists ( select 1 from dual where 1 = 0 )                                                                                                          \n" +
                " )                                                                                                                                                             \n" +
                ")                                                                                                                                                              \n" +
                "as yes_no                                                                                                                                                      \n" +
                "from dual";
        String targetSql = "SELECT (\n" +
                "\t\tSELECT 'y'\n" +
                "\t\tFROM dual\n" +
                "\t\tWHERE EXISTS (\n" +
                "\t\t\tSELECT 1\n" +
                "\t\t\tFROM dual\n" +
                "\t\t\tWHERE 1 = 0\n" +
                "\t\t)\n" +
                "\t\tUNION\n" +
                "\t\t(SELECT 'n'\n" +
                "\t\tFROM dual\n" +
                "\t\tWHERE NOT EXISTS (\n" +
                "\t\t\tSELECT 1\n" +
                "\t\t\tFROM dual\n" +
                "\t\t\tWHERE 1 = 0\n" +
                "\t\t))\n" +
                "\t) AS yes_no\n" +
                "FROM dual";

        PGSQLStatementParser pgparser=new PGSQLStatementParser(sql);
        SQLStatement pgstatement = pgparser.parseStatement();
        Assert.assertEquals(targetSql, pgstatement.toString());
        System.out.println(pgstatement.toString());
    }

    public void testUnion3(){
        String sql = "select * from v.e\n" +
                "where\n" +
                "\tcid <> rid\n" +
                "\tand  rid  not in\n" +
                "\t(\n" +
                "\t\t(select distinct  rid  from  v.s )\n" +
                "\t\tunion\n" +
                "\t\t(select distinct  rid  from v.p )\n" +
                "\t)\n" +
                "\tand  \"timestamp\"  <= 1298505600000";
        String targetSql = "SELECT *\n" +
                "FROM v.e\n" +
                "WHERE cid <> rid\n" +
                "\tAND rid NOT IN (\n" +
                "\t\tSELECT DISTINCT rid\n" +
                "\t\tFROM v.s\n" +
                "\t\tUNION\n" +
                "\t\t(SELECT DISTINCT rid\n" +
                "\t\tFROM v.p)\n" +
                "\t)\n" +
                "\tAND 'timestamp' <= 1298505600000";

        SQLStatement pgstatement = SQLUtils.parseSingleMysqlStatement(sql);
        Assert.assertEquals(targetSql, pgstatement.toString());
        System.out.println(pgstatement.toString());
    }

}