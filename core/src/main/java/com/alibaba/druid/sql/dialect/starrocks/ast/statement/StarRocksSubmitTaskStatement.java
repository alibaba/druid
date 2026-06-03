package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class StarRocksSubmitTaskStatement extends SQLStatementImpl {
    private SQLName name;
    private SQLExpr scheduleStart;
    private SQLExpr scheduleEvery;
    private List<SQLAssignItem> properties = new ArrayList<>();
    private SQLStatement body;

    public StarRocksSubmitTaskStatement() {
        dbType = DbType.starrocks;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.name = x;
    }

    public SQLExpr getScheduleStart() {
        return scheduleStart;
    }

    public void setScheduleStart(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.scheduleStart = x;
    }

    public SQLExpr getScheduleEvery() {
        return scheduleEvery;
    }

    public void setScheduleEvery(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.scheduleEvery = x;
    }

    public List<SQLAssignItem> getProperties() {
        return properties;
    }

    public void setProperties(List<SQLAssignItem> properties) {
        this.properties = properties;
    }

    public void addProperty(SQLAssignItem assignItem) {
        if (assignItem != null) {
            assignItem.setParent(this);
        }
        this.properties.add(assignItem);
    }

    public SQLStatement getBody() {
        return body;
    }

    public void setBody(SQLStatement x) {
        if (x != null) {
            x.setParent(this);
        }
        this.body = x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, scheduleStart);
            acceptChild(visitor, scheduleEvery);
            acceptChild(visitor, properties);
            acceptChild(visitor, body);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<>();
        if (name != null) {
            children.add(name);
        }
        if (scheduleStart != null) {
            children.add(scheduleStart);
        }
        if (scheduleEvery != null) {
            children.add(scheduleEvery);
        }
        children.addAll(properties);
        if (body != null) {
            children.add(body);
        }
        return children;
    }

    public StarRocksSubmitTaskStatement clone() {
        StarRocksSubmitTaskStatement x = new StarRocksSubmitTaskStatement();
        if (this.name != null) {
            x.setName(this.name.clone());
        }
        if (this.scheduleStart != null) {
            x.setScheduleStart(this.scheduleStart.clone());
        }
        if (this.scheduleEvery != null) {
            x.setScheduleEvery(this.scheduleEvery.clone());
        }
        for (SQLAssignItem item : this.properties) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.properties.add(cloned);
        }
        if (this.body != null) {
            x.setBody(this.body.clone());
        }
        return x;
    }
}
