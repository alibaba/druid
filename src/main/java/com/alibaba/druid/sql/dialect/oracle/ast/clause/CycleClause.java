package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class CycleClause extends OracleSQLObjectImpl {

    private static final long   serialVersionUID = 1L;

    private final List<SQLExpr> aliases          = new ArrayList<SQLExpr>();
    private SQLExpr             mark;
    private SQLExpr             value;
    private SQLExpr             defaultValue;

    public SQLExpr getMark() {
        return mark;
    }

    public void setMark(SQLExpr mark) {
        this.mark = mark;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    public SQLExpr getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(SQLExpr defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<SQLExpr> getAliases() {
        return aliases;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, aliases);
            acceptChild(visitor, mark);
            acceptChild(visitor, value);
            acceptChild(visitor, defaultValue);
        }
        visitor.endVisit(this);
    }

}
