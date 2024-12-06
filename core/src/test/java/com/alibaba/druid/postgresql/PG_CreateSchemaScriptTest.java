package com.alibaba.druid.postgresql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;

import junit.framework.TestCase;

/**
 * Created by zyc@hasor.net on 23/07/2017.
 */
public class PG_CreateSchemaScriptTest extends TestCase {

    public void testCreate() {
        String sql = "create schema abc create table test_user (id int primary key,name varchar(50));";
        String targetSql = "CREATE SCHEMA abc CREATE TABLE test_user (\n" +
                "\tid int PRIMARY KEY,\n" +
                "\tname varchar(50)\n" +
                ");";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

}
