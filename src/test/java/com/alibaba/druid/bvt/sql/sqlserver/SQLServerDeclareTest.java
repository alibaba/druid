/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.druid.bvt.sql.sqlserver;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.util.JdbcUtils;

public class SQLServerDeclareTest extends TestCase {

    public void test_0() {
        String sql = "declare @is_updated bit";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        Assert.assertEquals(1, stmtList.size());

        String text = SQLUtils.toSQLString(stmtList, JdbcUtils.SQL_SERVER);

        Assert.assertEquals("DECLARE @is_updated bit", text);
    }
    
    public void test_1() {
        String sql = "DECLARE @Group nvarchar(50), @Sales money=1;";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        Assert.assertEquals(1, stmtList.size());

        String text = SQLUtils.toSQLString(stmtList, JdbcUtils.SQL_SERVER);

        Assert.assertEquals("DECLARE @Group nvarchar(50), @Sales money = 1", text);
    }
    
    public void test_2() {
        String sql = "DECLARE @cursor CURSOR";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        Assert.assertEquals(1, stmtList.size());

        String text = SQLUtils.toSQLString(stmtList, JdbcUtils.SQL_SERVER);

        Assert.assertEquals("DECLARE @cursor CURSOR", text);
    }

    public void test_3() {
        String sql = "DECLARE @MyTableVar table( EmpID int NOT NULL, OldVacationHours int, NewVacationHours int, ModifiedDate datetime);";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        Assert.assertEquals(1, stmtList.size());

        String text = SQLUtils.toSQLString(stmtList, JdbcUtils.SQL_SERVER);

        String expected = "DECLARE @MyTableVar TABLE ("//
                          + "\n\tEmpID int NOT NULL,"//
                          + "\n\tOldVacationHours int,"//
                          + "\n\tNewVacationHours int,"//
                          + "\n\tModifiedDate datetime"//
                          + "\n)";
        Assert.assertEquals(expected, text);
    }

}
