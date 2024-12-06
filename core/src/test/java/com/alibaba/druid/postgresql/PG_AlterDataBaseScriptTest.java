package com.alibaba.druid.postgresql;

import org.junit.Assert;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;

import junit.framework.TestCase;

/**
 * Created by zyc@hasor.net on 23/07/2017.
 */
public class PG_AlterDataBaseScriptTest extends TestCase {

    public void testAlert_1() {
        String sql = "alter database abc with allow_connections true";
        String targetSql = "ALTER DATABASE abc WITH ALLOW_CONNECTIONS true";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testAlert_2() {
        String sql = "alter database abc allow_connections false";
        String targetSql = "ALTER DATABASE abc ALLOW_CONNECTIONS false";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testAlert_3() {
        String sql = "alter database abc is_template true";
        String targetSql = "ALTER DATABASE abc IS_TEMPLATE true";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }
}
