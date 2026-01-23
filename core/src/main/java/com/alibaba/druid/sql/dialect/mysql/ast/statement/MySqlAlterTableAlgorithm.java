package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlAlterTableAlgorithm extends MySqlObjectImpl implements SQLAlterTableItem {
    private SQLExpr algorithmType;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, algorithmType);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(SQLExpr algorithmType) {
        this.algorithmType = algorithmType;
    }
}
