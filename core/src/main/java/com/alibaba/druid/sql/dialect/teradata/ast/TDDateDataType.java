package com.alibaba.druid.sql.dialect.teradata.ast;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.teradata.visitor.TDASTVisitor;

public class TDDateDataType extends SQLDataTypeImpl implements TDObject {
    private SQLExpr format;

    public TDDateDataType(String name) {
        super(name);
    }

    public void setFormat(SQLExpr expr) {
        format = expr;
    }

    public SQLExpr getFormat() {
        return format;
    }
    @Override
    public void accept0(TDASTVisitor visitor) {
        if (visitor.visit(this)) {
            format.accept(visitor);
            visitor.endVisit(this);
        }
    }
}
