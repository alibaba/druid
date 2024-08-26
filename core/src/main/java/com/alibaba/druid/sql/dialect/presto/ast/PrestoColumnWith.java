package com.alibaba.druid.sql.dialect.presto.ast;

import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class PrestoColumnWith extends PrestoColumnConstraint implements PrestoObject {
    private final List<SQLAssignItem> properties;

    public PrestoColumnWith() {
        properties = new ArrayList<>();
    }

    public void addProperty(SQLAssignItem sqlAssignItem) {
        if (sqlAssignItem != null) {
            sqlAssignItem.setParent(this);
            properties.add(sqlAssignItem);
        }
    }

    public List<SQLAssignItem> getProperties() {
        return properties;
    }

    @Override
    public void accept0(PrestoASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, properties);
        }
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PrestoASTVisitor) {
            accept0((PrestoASTVisitor) visitor);
        }
    }
}
