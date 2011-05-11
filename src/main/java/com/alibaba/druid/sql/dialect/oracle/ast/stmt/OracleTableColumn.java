package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleTableColumn extends OracleSQLObject {
    private static final long serialVersionUID = 1L;

    private boolean sort;
    private SQLDataType dataType;
    private SQLExpr defaultValue;
    private final List<OracleConstraint> constaints = new ArrayList<OracleConstraint>();
    private String name;
    private boolean generatedAlways;
    private SQLExpr as;
    private boolean virtual = false;

    public OracleTableColumn() {

    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.dataType);
            acceptChild(visitor, this.defaultValue);
            acceptChild(visitor, this.constaints);
            acceptChild(visitor, this.as);
        }
        visitor.endVisit(this);
    }

    public SQLDataType getDataType() {
        return this.dataType;
    }

    public void setDataType(SQLDataType dataType) {
        this.dataType = dataType;
    }

    public boolean isGeneratedAlways() {
        return this.generatedAlways;
    }

    public void setGeneratedAlways(boolean generatedAlways) {
        this.generatedAlways = generatedAlways;
    }

    public SQLExpr getAs() {
        return this.as;
    }

    public void setAs(SQLExpr as) {
        this.as = as;
    }

    public boolean isVirtual() {
        return this.virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSort() {
        return this.sort;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public SQLExpr getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(SQLExpr defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<OracleConstraint> getConstaints() {
        return this.constaints;
    }
}
