package com.alibaba.druid.sql.dialect.bigquery.ast;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.bigquery.visitor.BigQueryVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class BigQueryCreateModelStatement extends SQLStatementImpl implements BigQueryObject {
    private boolean ifNotExists;
    private boolean replace;

    private SQLName name;
    private final List<SQLAssignItem> options = new ArrayList<>();
    private SQLStatement trainingData;
    private SQLStatement customHoliday;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.name = x;
    }

    public List<SQLAssignItem> getOptions() {
        return options;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    public SQLStatement getTrainingData() {
        return trainingData;
    }

    public void setTrainingData(SQLStatement x) {
        if (x != null) {
            x.setParent(this);
        }
        this.trainingData = x;
    }

    public SQLStatement getCustomHoliday() {
        return customHoliday;
    }

    public void setCustomHoliday(SQLStatement x) {
        if (x != null) {
            x.setParent(this);
        }
        this.customHoliday = x;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof BigQueryVisitor) {
            accept0((BigQueryVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    @Override
    public void accept0(BigQueryVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
    }
}
