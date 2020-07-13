package com.alibaba.druid.sql.dialect.impala.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.impala.visitor.ImpalaASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;

public class ImpalaUpdateStatements extends SQLUpdateStatement {

  public SQLJoinTableSource getJoin() {
    return join;
  }

  public void setJoin(SQLJoinTableSource join) {
    this.join = join;
  }

  private SQLJoinTableSource join;

  public ImpalaUpdateStatements(){
    super(JdbcUtils.IMPALA);
  }

  @Override
  protected void accept0(SQLASTVisitor visitor) {
    if (visitor instanceof ImpalaASTVisitor) {
      accept0((ImpalaASTVisitor) visitor);
    } else {
      super.accept0(visitor);
    }
  }

  protected void accept0(ImpalaASTVisitor visitor) {
    if (visitor.visit(this)) {
      acceptChild(visitor, tableSource);
      acceptChild(visitor, from);
      acceptChild(visitor, items);
      acceptChild(visitor, where);
    }
    visitor.endVisit(this);
  }
}
