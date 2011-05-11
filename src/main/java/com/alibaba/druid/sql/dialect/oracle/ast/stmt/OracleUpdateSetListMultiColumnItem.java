package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleUpdateSetListMultiColumnItem extends OracleUpdateSetListItem {
    private static final long serialVersionUID = 1L;

    private final List<SQLName> columns = new ArrayList<SQLName>();
    private OracleSelect subQuery;

    public OracleUpdateSetListMultiColumnItem() {

    }

    public OracleUpdateSetListMultiColumnItem(OracleSelect subQuery) {

        this.subQuery = subQuery;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.subQuery);
        }

        visitor.endVisit(this);
    }

    public OracleSelect getSubQuery() {
        return this.subQuery;
    }

    public void setSubQuery(OracleSelect subQuery) {
        this.subQuery = subQuery;
    }

    public List<SQLName> getColumns() {
        return this.columns;
    }
}
