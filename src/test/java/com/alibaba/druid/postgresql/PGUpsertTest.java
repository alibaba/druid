package com.alibaba.druid.postgresql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

public class PGUpsertTest extends TestCase  {

  public void testUpsert(){
    String sql = "insert into \"test_dup\" values(1,'2',-100) on conflict(id) do update set \"count\" = test_dup.\"count\" + 1;";
    String targetSql = "INSERT INTO \"test_dup\"\n"
        + "VALUES (1, '2', -100)\n"
        + "ON CONFLICT (id) DO UPDATE SET \"count\" = test_dup.\"count\" + 1";
    PGSQLStatementParser parser=new PGSQLStatementParser(sql);
    SQLStatement statement = parser.parseStatement();
    Assert.assertEquals(targetSql, statement.toString());
  }

}
