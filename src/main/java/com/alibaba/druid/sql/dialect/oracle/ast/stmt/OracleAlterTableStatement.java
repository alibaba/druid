package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleAlterTableStatement extends OracleStatementImpl {

    private static final long          serialVersionUID        = 1L;
    private SQLName                    name;
    private List<OracleAlterTableItem> items                   = new ArrayList<OracleAlterTableItem>();

    private boolean                    updateGlobalIndexes     = false;
    private boolean                    invalidateGlobalIndexes = false;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }

    public boolean isUpdateGlobalIndexes() {
        return updateGlobalIndexes;
    }

    public void setUpdateGlobalIndexes(boolean updateGlobalIndexes) {
        this.updateGlobalIndexes = updateGlobalIndexes;
    }

    public boolean isInvalidateGlobalIndexes() {
        return invalidateGlobalIndexes;
    }

    public void setInvalidateGlobalIndexes(boolean invalidateGlobalIndexes) {
        this.invalidateGlobalIndexes = invalidateGlobalIndexes;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public List<OracleAlterTableItem> getItems() {
        return items;
    }

    public void setItems(List<OracleAlterTableItem> items) {
        this.items = items;
    }

}
