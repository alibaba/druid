package com.alibaba.druid.sql.dialect.starrocks.ast;

import com.alibaba.druid.sql.ast.statement.SQLTableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;

public class StarRocksDuplicateKey extends SQLUnique implements SQLTableConstraint, StarRocksObject {
    @Override
    public void accept0(StarRocksASTVisitor v) {
        super.accept0(v);
    }
}
