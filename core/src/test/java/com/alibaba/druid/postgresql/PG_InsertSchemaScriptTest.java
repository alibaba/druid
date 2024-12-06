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
public class PG_InsertSchemaScriptTest extends TestCase {

    public void testInsert_1() {
        String sql = "insert into table2 with tab1Cnt as (select id,name from table1) select * from tab1Cnt;";
        String targetSql = "INSERT INTO table2\n" +
                "WITH tab1Cnt AS (\n" +
                "\t\tSELECT id, name\n" +
                "\t\tFROM table1\n" +
                "\t)\n" +
                "SELECT *\n" +
                "FROM tab1Cnt;";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }

    public void testInsert_2() {
        String sql = "insert into table2 with tab1Cnt as (select id,column1 from table1) select * from tab1Cnt returning (id,column1);";
        String targetSql = "INSERT INTO table2\n" +
                "WITH tab1Cnt AS (\n" +
                "\t\tSELECT id, column1\n" +
                "\t\tFROM table1\n" +
                "\t)\n" +
                "SELECT *\n" +
                "FROM tab1Cnt\n" +
                "RETURNING (id, column1);";
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        Assert.assertEquals(targetSql, SQLUtils.toSQLString(statement, DbType.postgresql));
    }
}
