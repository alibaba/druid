package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OraclePrimaryKey extends OracleSQLObjectImpl implements SQLPrimaryKey, SQLTableElement {

    private static final long serialVersionUID = 1L;

    private SQLName           name;
    private List<SQLExpr>     columns          = new ArrayList<SQLExpr>();

    private SQLExpr           usingIndex;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, columns);
            acceptChild(visitor, usingIndex);
        }
        visitor.endVisit(this);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public void setColumns(List<SQLExpr> columns) {
        this.columns = columns;
    }

    public SQLExpr getUsingIndex() {
        return usingIndex;
    }

    public void setUsingIndex(SQLExpr usingIndex) {
        this.usingIndex = usingIndex;
    }

}
