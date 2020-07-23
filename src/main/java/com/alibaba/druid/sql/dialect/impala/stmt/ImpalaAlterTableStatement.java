package com.alibaba.druid.sql.dialect.impala.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.impala.visitor.ImpalaASTVisitor;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class ImpalaAlterTableStatement extends SQLAlterTableStatement {
  private boolean isExists = false;

  public String getAlterType() {
    return alterType;
  }

  public void setAlterType(String alterType) {
    this.alterType = alterType;
  }

  private String alterType;

  public List<SQLExpr> getPartitions() {
    return partitions;
  }

  private List<SQLExpr>  partitions = new ArrayList<SQLExpr>();


  public boolean isNotExists() {
    return isNotExists;
  }

  public void setNotExists(boolean notExists) {
    isNotExists = notExists;
  }

  private boolean isNotExists = false;

  public ImpalaAlterTableStatement(){
    super(JdbcConstants.IMPALA);
  }

  public boolean isExists() {
    return isExists;
  }

  public void setExists(boolean exists) {
    isExists = exists;
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
    }
    visitor.endVisit(this);
  }
}
