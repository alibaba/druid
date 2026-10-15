TO STRING
1. not 括号丢失
正确case: not (a) and (not b) and (c)
错误case: not a and not b and (c)

2. not 括号错乱
正确case: (not a) != b
错误case: (NOT a != b)

3. 多括号不打印
正确case: ((a = 1))
错误case: (a = 1)

4. query表达式 toString 时不会自动添加括号，生成错误SQL
参考代码模块

CLONE
1. clone时qualify丢失
正确case: SELECT * FROM a WHERE b = 100 QUALIFY ROW_NUMBER() OVER (PARTITION BY c ORDER BY d DESC) = 1
错误case: SELECT * FROM a WHERE b = 100

```
package com.alibaba.dt.SQLAbility.DruidTest;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import org.junit.Test;

public class Druid26Test {
    private void print(SQLObject oldObject, SQLObject newObject) {
        System.out.println("old: \n" + SQLUtils.toOdpsString(oldObject));
        System.out.println("\nnew: \n" + SQLUtils.toOdpsString(newObject));
    }

    private void printStr(String oldObject, String newObject) {
        System.out.println("old: \n" + oldObject);
        System.out.println("\nnew: \n" + newObject);
    }
    /**
     * clone
     * qualify 丢失
     */
    @Test
    public void TestCloneQualify() {
        String sql = "select * from table_a where a_1 = 100 QUALIFY ROW_NUMBER() OVER (PARTITION BY b_1 ORDER BY b_2 DESC ) = 1;";
        SQLSelectStatement oldStmt = (SQLSelectStatement)SQLUtils.parseSingleStatement(sql, DbType.odps);
        SQLSelectStatement newStmt = oldStmt.clone();
        print(oldStmt, newStmt);
    }

    /**
     * toString
     * not 带括号，且在表达式开始位置，会出现括号错乱
     */
    @Test
    public void TestToStringNotErrorBracket() {
        String sqlExpr = "(not a) != b";
        SQLExpr oldExpr = SQLUtils.toSQLExpr(sqlExpr, DbType.odps);
        printStr(sqlExpr, SQLUtils.toOdpsString(oldExpr));
    }

    /**
     * toString
     * not 括号丢失
     */
    @Test
    public void TestToStringNotMissBracket() {
        String sqlExpr = "not (a) and (not b) and (c)";
        SQLExpr oldExpr = SQLUtils.toSQLExpr(sqlExpr, DbType.odps);
        printStr(sqlExpr, SQLUtils.toOdpsString(oldExpr));
    }

    /**
     * toString
     * query 表达式外的括号不会自动补足
     */
    @Test
    public void TestToStringSubQueryExpr() {
        String sql = "select a_1 from table_a";
        SQLQueryExpr queryExpr = new SQLQueryExpr(((SQLSelectStatement)(SQLUtils.parseSingleStatement(sql, DbType.odps))).getSelect());
        SQLBinaryOpExpr binaryOpExpr = new SQLBinaryOpExpr();
        binaryOpExpr.setLeft(new SQLNumberExpr(1));
        binaryOpExpr.setRight(queryExpr);
        binaryOpExpr.setOperator(SQLBinaryOperator.Equality);
        print(queryExpr, binaryOpExpr);
    }

    /**
     * toString
     * 多括号不打印
     */
    @Test
    public void TestToStringMultiBracket() {
        String sqlExpr = "((a = 1))";
        SQLExpr oldExpr = SQLUtils.toSQLExpr(sqlExpr, DbType.odps);
        printStr(sqlExpr, SQLUtils.toOdpsString(oldExpr));
    }

}
```