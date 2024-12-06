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
public class PG_DropSchemaScriptTest extends TestCase {
    public void testDrop_1() {
        String sql = "drop schema if exists abc restrict";
        String targetSql = "DROP SCHEMA IF EXISTS abc RESTRICT";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testDrop_2() {
        String sql = "drop schema if exists abc cascade";
        String targetSql = "DROP SCHEMA IF EXISTS abc CASCADE";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }
}