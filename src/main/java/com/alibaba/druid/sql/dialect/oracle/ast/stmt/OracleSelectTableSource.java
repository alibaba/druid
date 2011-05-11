package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

public interface OracleSelectTableSource {

    String getAlias();

    void setAlias(String alias);

    OracleSelectPivotBase getPivot();

    void setPivot(OracleSelectPivotBase pivot);

}
