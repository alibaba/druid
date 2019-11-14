package com.alibaba.druid.postgresql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitorAdapter;
import junit.framework.TestCase;
import org.junit.Assert;

public class PGUpdateFromTest extends TestCase {

  public void testUpdateFromTest(){
    String sql = "UPDATE TABLE_A TA SET A_VALUE = TB.B_VALUE FROM TABLE_B TB WHERE TB.FKEY = TA.KEY";
    final String schemaName = "myschema";
    String targetSql = "UPDATE MYSCHEMA.TABLE_A TA\n"
        + "SET A_VALUE = TB.B_VALUE\n"
        + "FROM MYSCHEMA.TABLE_B TB\n"
        + "WHERE TB.FKEY = TA.KEY";
    PGSQLStatementParser parser=new PGSQLStatementParser(sql);
    SQLStatement statement = parser.parseStatement();

    statement.accept(new PGASTVisitorAdapter(){
      @Override
      public boolean visit(SQLExprTableSource x) {
        x.getExpr().accept(this);
        if(!(x.getExpr() instanceof SQLIdentifierExpr)) {
          return true;
        }
        SQLIdentifierExpr expr = (SQLIdentifierExpr) x.getExpr();
        String newTableName = schemaName.toUpperCase() + "." + expr.getName().toUpperCase().trim();
        expr.setName(newTableName);
        return true;
      }
    });

    Assert.assertEquals(targetSql, statement.toString());
  }
}
