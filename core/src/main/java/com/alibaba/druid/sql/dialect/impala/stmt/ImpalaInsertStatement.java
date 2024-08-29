package com.alibaba.druid.sql.dialect.impala.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class ImpalaInsertStatement extends HiveInsertStatement {
    public ImpalaInsertStatement() {
        this.dbType = DbType.impala;
    }

    private List<SQLHint> insertHints = new ArrayList<>();
    private List<SQLHint> selectHints = new ArrayList<>();

    public void cloneTo(ImpalaInsertStatement x) {
        super.cloneTo(x);
        for (SQLHint hint : insertHints) {
            SQLHint h2 = hint.clone();
            h2.setParent(x);
            x.insertHints.add(h2);
        }
        for (SQLHint hint : selectHints) {
            SQLHint h2 = hint.clone();
            h2.setParent(x);
            x.selectHints.add(h2);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, with);
            acceptChild(visitor, insertHints);
            acceptChild(visitor, tableSource);
            acceptChild(visitor, partitions);
            acceptChild(visitor, valuesList);
            acceptChild(visitor, selectHints);
            acceptChild(visitor, query);
        }
        visitor.endVisit(this);
    }

    public List<SQLHint> getInsertHints() {
        return insertHints;
    }

    public void setInsertHints(List<SQLHint> insertHints) {
        this.insertHints = insertHints;
    }

    public List<SQLHint> getSelectHints() {
        return selectHints;
    }

    public void setSelectHints(List<SQLHint> selectHint) {
        this.selectHints = selectHint;
    }
}
