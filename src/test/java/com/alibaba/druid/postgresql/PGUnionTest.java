package com.alibaba.druid.postgresql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
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

}
