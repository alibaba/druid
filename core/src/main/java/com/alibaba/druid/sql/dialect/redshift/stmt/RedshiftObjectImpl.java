package com.alibaba.druid.sql.dialect.redshift.stmt;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.redshift.visitor.RedshiftASTVisitor;

public abstract class RedshiftObjectImpl extends SQLObjectImpl implements RedshiftObject {
    public abstract void accept0(RedshiftASTVisitor visitor);
}
