package com.alibaba.druid.sql.dialect.clickhouse.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.CKVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class CKAlterTableUpdateStatement extends CKAlterTableStatement {
    protected final List<SQLUpdateSetItem> items = new ArrayList<SQLUpdateSetItem>();
    private SQLName partitionId;
    protected SQLExpr where;

    public CKAlterTableUpdateStatement(DbType dbType) {
        super(dbType);
    }

    public List<SQLUpdateSetItem> getItems() {
        return items;
    }

    public SQLName getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(SQLName partitionId) {
        this.partitionId = partitionId;
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr where) {
        this.where = where;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof CKVisitor) {
            CKVisitor vv = (CKVisitor) v;
            if (vv.visit(this)) {
                acceptChild(vv, this.getTableName());
                acceptChild(vv, this.getClusterName());
                acceptChild(vv, this.getItems());
                acceptChild(vv, this.getWhere());
            }
            vv.endVisit(this);
        }
    }
}
