package com.alibaba.druid.sql.oracle.bvt;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListSingleColumnItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;

public class OracleSQLParserTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "UPDATE employees SET salary = salary * 1.1 WHERE employee_id IN (SELECT employee_id FROM job_history);";
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        
        Assert.assertEquals(1, statementList.size());
        
        OracleUpdateStatement statement = (OracleUpdateStatement) statementList.get(0);
        Assert.assertEquals(0, statement.getHints().size());
        OracleUpdateSetListClause setList = (OracleUpdateSetListClause) statement.getSetClause();
        Assert.assertEquals(1, setList.getItems().size());
        OracleUpdateSetListSingleColumnItem item = (OracleUpdateSetListSingleColumnItem) setList.getItems().get(0);
        SQLIdentifierExpr column = (SQLIdentifierExpr) item.getColumn();
        Assert.assertEquals("salary", column.getName());
        
        SQLBinaryOpExpr value = (SQLBinaryOpExpr) item.getValue();
        Assert.assertEquals(SQLBinaryOperator.Multiply, value.getOperator());

        output(statementList);
    }

    private void output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
            visitor.println();
        }

        System.out.println(out.toString());
    }
}
