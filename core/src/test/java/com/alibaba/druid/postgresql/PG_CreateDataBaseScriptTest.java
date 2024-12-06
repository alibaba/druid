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
public class PG_CreateDataBaseScriptTest extends TestCase {

    public void testCreate_1() {
        String sql = "create database abc";
        String targetSql = "CREATE DATABASE abc";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testCreate_owner_1() {
        String sql = "create database abc owner zyc";
        String targetSql = "CREATE DATABASE abc OWNER zyc";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testCreate_owner_2() {
        String sql = "create database abc owner = zyc";
        String targetSql = "CREATE DATABASE abc OWNER = zyc";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testCreate_owner_3() {
        String sql = "create database abc with owner zyc";
        String targetSql = "CREATE DATABASE abc WITH OWNER zyc";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testCreate_template_1() {
        String sql = "create database abc template template1";
        String targetSql = "CREATE DATABASE abc TEMPLATE template1";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testCreate_template_2() {
        String sql = "create database abc template = zyc";
        String targetSql = "CREATE DATABASE abc TEMPLATE = zyc";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testCreate_template_3() {
        String sql = "create database abc with template zyc";
        String targetSql = "CREATE DATABASE abc WITH TEMPLATE zyc";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testCreate_merge_1() {
        String sql = "create database abc with owner = zyc template zyc";
        String targetSql = "CREATE DATABASE abc WITH OWNER = zyc TEMPLATE zyc";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }
}
