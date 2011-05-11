package com.alibaba.druid.sql.ast;

import java.util.Map;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface SQLObject {
    void accept(SQLASTVisitor visitor);

    SQLObject getParent();

    void setParent(SQLObject parent);

    Map<String, Object> getAttributes();

    Object getAttribute(String name);

    void putAttribute(String name, Object value);

    Map<String, Object> getAttributesDirect();

    void output(StringBuffer buf);
}
