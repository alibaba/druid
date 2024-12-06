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

    public void testCreate_1() {
        String sql = "create schema abc create table test_user (id int primary key,name varchar(50));";
        String targetSql = "CREATE SCHEMA abc CREATE TABLE test_user (\n" +
                "\tid int PRIMARY KEY,\n" +
                "\tname varchar(50)\n" +
                ");";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testCreate_2() {
        String sql = "create schema authorization aac\n" +
                "    create table test_user1 (id int primary key,name varchar(50))\n" +
                "    create table test_user2 (id int primary key,name varchar(50));";
        String targetSql = "CREATE SCHEMA AUTHORIZATION aac CREATE TABLE test_user1 (\n" +
                "\tid int PRIMARY KEY,\n" +
                "\tname varchar(50)\n" +
                ") CREATE TABLE test_user2 (\n" +
                "\tid int PRIMARY KEY,\n" +
                "\tname varchar(50)\n" +
                ");";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }
}
