package com.alibaba.druid.sql.dialect.redshift.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLTop;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.redshift.visitor.RedshiftASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class RedshiftSelectQueryBlock extends SQLSelectQueryBlock {
    private SQLTop top;
    private boolean insertTable;
    private boolean insertTemp;
    private boolean insertTemporary;

    public RedshiftSelectQueryBlock() {
        super(DbType.redshift);
        insertTable = false;
        insertTemp = false;
        insertTemporary = false;
    }

    public SQLTop getTop() {
        return top;
    }

    public void setTop(SQLTop top) {
        if (top != null) {
            top.setParent(this);
        }
        this.top = top;
    }

    public boolean isInsertTable() {
        return insertTable;
    }

    public void setInsertTable(boolean insertTable) {
        this.insertTable = insertTable;
    }

    public boolean isInsertTemp() {
        return insertTemp;
    }

    public void setInsertTemp(boolean insertTemp) {
        this.insertTemp = insertTemp;
    }

    public boolean isInsertTemporary() {
        return insertTemporary;
    }

    public void setInsertTemporary(boolean insertTemporary) {
        this.insertTemporary = insertTemporary;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof RedshiftASTVisitor) {
            accept0((RedshiftASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    protected void accept0(RedshiftASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.top);
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
        }
        visitor.endVisit(this);
    }
}
