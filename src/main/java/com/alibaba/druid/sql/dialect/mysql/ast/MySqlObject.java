package com.alibaba.druid.sql.dialect.mysql.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;


public interface MySqlObject extends SQLObject {
    void accept0(MySqlASTVisitor visitor);
}
