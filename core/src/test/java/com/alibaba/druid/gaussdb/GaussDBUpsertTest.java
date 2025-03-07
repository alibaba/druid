package com.alibaba.druid.gaussdb;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.gaussdb.parser.GaussDbStatementParser;
import junit.framework.TestCase;
import org.junit.Assert;

public class GaussDBUpsertTest extends TestCase {
    public void testUpsert() {
        String sql = "insert into \"test_dup\" values(1,'2',-100) on conflict(id) do update set \"count\" = test_dup.\"count\" + 1;";
        String targetSql = "INSERT INTO \"test_dup\"\n"
                + "VALUES (1, '2', -100)\n"
                + "ON CONFLICT (id) DO UPDATE SET \"count\" = test_dup.\"count\" + 1;";
        GaussDbStatementParser parser = new GaussDbStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, statement.toString());
    }

}
