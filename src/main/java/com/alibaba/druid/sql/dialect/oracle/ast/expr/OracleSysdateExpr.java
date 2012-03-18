package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleSysdateExpr extends OracleSQLObjectImpl implements SQLExpr {

    private static final long serialVersionUID = 1L;

    private String            option;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
