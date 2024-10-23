package com.alibaba.druid.sql.dialect.teradata.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.teradata.visitor.TDASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface TDObject extends SQLObject {
  @Override
  default void accept(SQLASTVisitor v) {
    if (v instanceof TDASTVisitor) {
      accept0((TDASTVisitor) v);
    }
  }

  void accept0(TDASTVisitor visitor);
}
