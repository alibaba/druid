package com.alibaba.druid.sql.dialect.impala.ast;

import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.parser.Token;

import java.util.ArrayList;
import java.util.List;

public class ImpalaKuduPartition {
  protected Token type;
  protected final List<SQLColumnDefinition> partitionColumns = new ArrayList<SQLColumnDefinition>();
  protected final List<String> partitionAssign = new ArrayList<String>();
  protected int number = -1;

  private ImpalaKuduPartition(){};
  public ImpalaKuduPartition(Token t){
    this.type = t;
  }

  public Token getType() {
    return type;
  }

  public void setType(Token type) {
    this.type = type;
  }

  public List<SQLColumnDefinition> getPartitionColumns() {
    return partitionColumns;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public List<String> getPartitionAssign() {
    return partitionAssign;
  }
}
