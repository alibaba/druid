package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLResetStatement extends SQLStatementImpl {
    private List<String> options = new ArrayList<String>();

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
