package com.alibaba.druid.mysql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;

/**
 * Created by szf on 2017/11/16.
 */
public class MysqlVarantRefTest {
    @Test
    public void test() {
        String sql = "set session tx_variables = 1, session asdfsa = 2 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result = (SQLSetStatement) stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr) result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr) result.getItems().get(1).getTarget();
        Assert.assertTrue(resultExpr.isSession());
        Assert.assertTrue(resultExpr2.isSession());
    }

    @Test
    public void test22() {
        String sql = "set session tx_variables = 1, session asdfsa = 2 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result = (SQLSetStatement) stmtList.get(0);

        String text = SQLUtils.toSQLString(stmtList, DbType.mysql);
        Assert.assertEquals("SET @@session.tx_variables = 1, @@session.asdfsa = 2", text);

    }


    @Test
    public void test11() {
        String sql = "set session TRANSACTION ISOLATION LEVEL SERIALIZABLE";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        MySqlSetTransactionStatement x = (MySqlSetTransactionStatement) stmtList.get(0);

        Assert.assertTrue(x.getSession());
    }

    @Test
    public void test12() {
        String sql = "set TRANSACTION ISOLATION LEVEL SERIALIZABLE";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        MySqlSetTransactionStatement x = (MySqlSetTransactionStatement) stmtList.get(0);

        Assert.assertTrue(x.getSession() == null);
    }

    @Test
    public void test2() {
        String sql = "set session tx_variables = 1, asdfsa = 2 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result = (SQLSetStatement) stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr) result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr) result.getItems().get(1).getTarget();
        Assert.assertTrue(resultExpr.isSession());
        Assert.assertTrue(!resultExpr2.isSession());
    }


    @Test
    public void test3() {
        String sql = "set  tx_variables = 1, asdfsa = 2 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result = (SQLSetStatement) stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr) result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr) result.getItems().get(1).getTarget();
        Assert.assertTrue(!resultExpr.isSession());
        Assert.assertTrue(!resultExpr2.isSession());
    }


    @Test
    public void test4() {
        String sql = "set  tx_variables = 1,session asdfsa = 2 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result = (SQLSetStatement) stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr) result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr) result.getItems().get(1).getTarget();
        Assert.assertTrue(!resultExpr.isSession());
        Assert.assertTrue(resultExpr2.isSession());
    }


    @Test
    public void test5() {
        String sql = "set  tx_variables = 1,session asdfsa = 2,session dfd  = 3 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result = (SQLSetStatement) stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr) result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr) result.getItems().get(1).getTarget();
        SQLVariantRefExpr resultExpr3 = (SQLVariantRefExpr) result.getItems().get(2).getTarget();
        Assert.assertTrue(!resultExpr.isSession());
        Assert.assertTrue(resultExpr2.isSession());
        Assert.assertTrue(resultExpr3.isSession());
    }


    @Test
    public void test6() {
        String sql = "set  tx_variables = 1,session asdfsa = 2,session dfd  = 3, sdfsdf =3,session adfa =9 ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result = (SQLSetStatement) stmtList.get(0);

        SQLVariantRefExpr resultExpr = (SQLVariantRefExpr) result.getItems().get(0).getTarget();
        SQLVariantRefExpr resultExpr2 = (SQLVariantRefExpr) result.getItems().get(1).getTarget();
        SQLVariantRefExpr resultExpr3 = (SQLVariantRefExpr) result.getItems().get(2).getTarget();
        SQLVariantRefExpr resultExpr4 = (SQLVariantRefExpr) result.getItems().get(3).getTarget();
        SQLVariantRefExpr resultExpr5 = (SQLVariantRefExpr) result.getItems().get(4).getTarget();
        Assert.assertTrue(!resultExpr.isSession());
        Assert.assertTrue(resultExpr2.isSession());
        Assert.assertTrue(resultExpr3.isSession());
        Assert.assertTrue(!resultExpr4.isSession());
        Assert.assertTrue(resultExpr5.isSession());
    }

    @Test
    public void test7() {
        // in this case, < @sid is recognized as < operator and user-defined variables@sid
        String sql = "set @sid=1;select * from aaa where id < @sid; ";
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        SQLSetStatement result0 = (SQLSetStatement) stmtList.get(0);
        SQLVariantRefExpr resultExpr0 = (SQLVariantRefExpr) result0.getItems().get(0).getTarget();
        Assert.assertEquals("@sid", resultExpr0.getName());

        AtomicReference<SQLVariantRefExpr> resultExpr1Ref = new AtomicReference<>();
        AtomicInteger variableCnt = new AtomicInteger(0);
        AtomicInteger arrayContainerByOperatorCnt = new AtomicInteger(0);
        MySqlASTVisitor visitor = new MySqlASTVisitorAdapter() {
            @Override
            public boolean visit(SQLVariantRefExpr x) {
                resultExpr1Ref.set(x);
                variableCnt.addAndGet(1);
                return super.visit(x);
            }

            @Override
            public boolean visit(SQLBinaryOpExpr x) {
                if(SQLBinaryOperator.Array_ContainedBy.equals(x.getOperator())) {
                    arrayContainerByOperatorCnt.addAndGet(1);
                }
                return super.visit(x);
            }
        };
        stmtList.get(1).accept(visitor);


        Assert.assertEquals(1, variableCnt.get());
        Assert.assertEquals(0, arrayContainerByOperatorCnt.get());
        Assert.assertEquals("@sid", resultExpr1Ref.get().getName());

        // in this case, <@ is recognized as <@, instead of < user-defined variables@sid
        String sql2 = "select * from aaa where id <@ sid; ";
        SQLStatementParser parser2 = new MySqlStatementParser(sql2);

        List<SQLStatement> stmtList2 = parser2.parseStatementList();
        variableCnt.set(0);
        arrayContainerByOperatorCnt.set(0);
        resultExpr1Ref.set(null);
        stmtList2.get(0).accept(visitor);
        Assert.assertEquals(0, variableCnt.get());
        Assert.assertEquals(1, arrayContainerByOperatorCnt.get());
        Assert.assertNull(resultExpr1Ref.get());
    }
}
