package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlDropUser extends MySqlStatementImpl {

    private static final long serialVersionUID = 1L;

    private List<SQLExpr>     users            = new ArrayList<SQLExpr>(2);

    public List<SQLExpr> getUsers() {
        return users;
    }

    public void setUsers(List<SQLExpr> users) {
        this.users = users;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, users);
        }
    }
}
