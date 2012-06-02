package com.alibaba.druid.bvt.sql;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcUtils;


public class CreateDatabaseTest extends TestCase {
    public void test_0 () throws Exception {
        String sql = "CREATE DATABASE mydb";
        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, null);
        SQLStatement stmt = stmtList.get(0);
        
        Assert.assertEquals("CREATE DATABASE mydb", SQLUtils.toSQLString(stmt, null));
    }
    
    public void test_mysql () throws Exception {
        String sql = "CREATE DATABASE mydb";
        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcUtils.MYSQL);
        SQLStatement stmt = stmtList.get(0);
        
        Assert.assertEquals("CREATE DATABASE mydb", SQLUtils.toSQLString(stmt, JdbcUtils.MYSQL));
    }
}
