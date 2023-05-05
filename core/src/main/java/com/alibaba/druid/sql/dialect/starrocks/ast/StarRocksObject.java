package com.alibaba.druid.sql.dialect.starrocks.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;

public interface StarRocksObject extends SQLObject {
    void accept0(StarRocksASTVisitor v);
}
