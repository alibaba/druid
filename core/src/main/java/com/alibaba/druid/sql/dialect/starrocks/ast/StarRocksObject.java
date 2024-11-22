package com.alibaba.druid.sql.dialect.starrocks.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface StarRocksObject extends SQLObject {
    void accept0(StarRocksASTVisitor v);

    @Override
    default void accept(SQLASTVisitor visitor) {
        if (visitor instanceof StarRocksASTVisitor) {
            accept0((StarRocksASTVisitor) visitor);
        }
    }
}
