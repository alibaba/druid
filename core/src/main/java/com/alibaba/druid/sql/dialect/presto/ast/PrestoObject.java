package com.alibaba.druid.sql.dialect.presto.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoVisitor;

public interface PrestoObject extends SQLObject {
    void accept0(PrestoVisitor visitor);
}
