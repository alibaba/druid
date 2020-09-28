package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dagon0577
 * @date 2019/11/7 11:10
 */
public class MySqlCheckTableStatement extends MySqlStatementImpl {
    private final List<SQLTableSource> tables = new ArrayList<SQLTableSource>();

    private boolean for_upgrade;
    private boolean quick;
    private boolean fast;
    private boolean medium;
    private boolean extended;
    private boolean changed;

    public MySqlCheckTableStatement() {

    }

    public void addTable(SQLTableSource table) {
        if (table == null) {
            return;
        }

        table.setParent(this);
        tables.add(table);
    }

    public List<SQLTableSource> getTables() {
        return tables;
    }

    public boolean isFor_upgrade() {
        return for_upgrade;
    }

    public void setFor_upgrade(boolean for_upgrade) {
        this.for_upgrade = for_upgrade;
    }

    public boolean isFast() {
        return fast;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }

    public boolean isMedium() {
        return medium;
    }

    public void setMedium(boolean medium) {
        this.medium = medium;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isQuick() {
        return quick;
    }

    public void setQuick(boolean quick) {
        this.quick = quick;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tables);
        }
        visitor.endVisit(this);
    }
}
