package com.alibaba.druid.mysql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by szf on 2017/11/16.
 */
public class MysqlVarantRefTest {

    @Test
    public void test(){
        String sql =  "set session tx_variables = 1, session asdfsa = 2 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result= (SQLSetStatement)stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr)result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr)result.getItems().get(1).getTarget();
        Assert.assertTrue(resultExpr.isSession());
        Assert.assertTrue(resultExpr2.isSession());
    }

    @Test
    public void test22(){
        String sql =  "set session tx_variables = 1, session asdfsa = 2 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result= (SQLSetStatement)stmtList.get(0);

        String text = SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
        Assert.assertEquals("SET @@session.tx_variables = 1, @@session.asdfsa = 2", text);

    }


    @Test
    public void test11(){
        String sql =  "set session TRANSACTION ISOLATION LEVEL SERIALIZABLE";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        MySqlSetTransactionStatement x = (MySqlSetTransactionStatement)stmtList.get(0);

        Assert.assertTrue(x.getSession());
    }

    @Test
    public void test12(){
        String sql =  "set TRANSACTION ISOLATION LEVEL SERIALIZABLE";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        MySqlSetTransactionStatement x = (MySqlSetTransactionStatement)stmtList.get(0);

        Assert.assertTrue(x.getSession() == null);
    }

    @Test
    public void test2(){
        String sql =  "set session tx_variables = 1, asdfsa = 2 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result= (SQLSetStatement)stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr)result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr)result.getItems().get(1).getTarget();
        Assert.assertTrue(resultExpr.isSession());
        Assert.assertTrue(!resultExpr2.isSession());
    }


    @Test
    public void test3(){
        String sql =  "set  tx_variables = 1, asdfsa = 2 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result= (SQLSetStatement)stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr)result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr)result.getItems().get(1).getTarget();
        Assert.assertTrue(!resultExpr.isSession());
        Assert.assertTrue(!resultExpr2.isSession());
    }


    @Test
    public void test4(){
        String sql =  "set  tx_variables = 1,session asdfsa = 2 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result= (SQLSetStatement)stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr)result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr)result.getItems().get(1).getTarget();
        Assert.assertTrue(!resultExpr.isSession());
        Assert.assertTrue(resultExpr2.isSession());
    }


    @Test
    public void test5(){
        String sql =  "set  tx_variables = 1,session asdfsa = 2,session dfd  = 3 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result= (SQLSetStatement)stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr)result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr)result.getItems().get(1).getTarget();
        SQLVariantRefExpr resultExpr3 = (SQLVariantRefExpr)result.getItems().get(2).getTarget();
        Assert.assertTrue(!resultExpr.isSession());
        Assert.assertTrue(resultExpr2.isSession());
        Assert.assertTrue(resultExpr3.isSession());
    }


    @Test
    public void test6(){
        String sql =  "set  tx_variables = 1,session asdfsa = 2,session dfd  = 3, sdfsdf =3,session adfa =9 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result= (SQLSetStatement)stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr)result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr)result.getItems().get(1).getTarget();
        SQLVariantRefExpr resultExpr3 = (SQLVariantRefExpr)result.getItems().get(2).getTarget();
        SQLVariantRefExpr resultExpr4 = (SQLVariantRefExpr)result.getItems().get(3).getTarget();
        SQLVariantRefExpr resultExpr5 = (SQLVariantRefExpr)result.getItems().get(4).getTarget();
        Assert.assertTrue(!resultExpr.isSession());
        Assert.assertTrue(resultExpr2.isSession());
        Assert.assertTrue(resultExpr3.isSession());
        Assert.assertTrue(!resultExpr4.isSession());
        Assert.assertTrue(resultExpr5.isSession());
    }

}
