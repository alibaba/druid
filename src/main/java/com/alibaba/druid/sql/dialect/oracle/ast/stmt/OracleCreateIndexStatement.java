package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleCreateIndexStatement extends OracleStatementImpl {

    private static final long          serialVersionUID  = 1L;

    private SQLName                    name;

    private SQLName                    table;

    private List<SQLSelectOrderByItem> items             = new ArrayList<SQLSelectOrderByItem>();

    private SQLName                    tablespace;

    private Type                       type;

    private boolean                    online            = false;

    private boolean                    indexOnlyTopLevel = false;

    private boolean                    noParallel;

    private SQLExpr                    parallel;

    public SQLExpr getParallel() {
        return parallel;
    }

    public void setParallel(SQLExpr parallel) {
        this.parallel = parallel;
    }

    public boolean isNoParallel() {
        return noParallel;
    }

    public void setNoParallel(boolean noParallel) {
        this.noParallel = noParallel;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isIndexOnlyTopLevel() {
        return indexOnlyTopLevel;
    }

    public void setIndexOnlyTopLevel(boolean indexOnlyTopLevel) {
        this.indexOnlyTopLevel = indexOnlyTopLevel;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, table);
            acceptChild(visitor, items);
            acceptChild(visitor, tablespace);
            acceptChild(visitor, parallel);
        }
        visitor.endVisit(this);
    }

    public static enum Type {
        UNIQUE, BITMAP
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLName getTable() {
        return table;
    }

    public void setTable(SQLName table) {
        this.table = table;
    }

    public List<SQLSelectOrderByItem> getItems() {
        return items;
    }

    public void setItems(List<SQLSelectOrderByItem> items) {
        this.items = items;
    }

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName tablespace) {
        this.tablespace = tablespace;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}
