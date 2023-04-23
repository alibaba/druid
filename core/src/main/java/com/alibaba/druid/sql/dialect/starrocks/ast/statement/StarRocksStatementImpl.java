package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

public abstract class StarRocksStatementImpl extends SQLStatementImpl implements StarRocksStatement {
    public StarRocksStatementImpl() {
        super(DbType.starrocks);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((StarRocksASTVisitor) visitor);
    }

    public void accept0(StarRocksASTVisitor v) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public List<SQLObject> getChildren() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
}
