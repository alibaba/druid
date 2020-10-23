package com.alibaba.druid.sql.dialect.ads.visitor;

import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface AdsVisitor extends SQLASTVisitor {
    boolean visit(MySqlPrimaryKey x);
    void endVisit(MySqlPrimaryKey x);

    boolean visit(MySqlCreateTableStatement x);
    void endVisit(MySqlCreateTableStatement x);
}
