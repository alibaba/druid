package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * MySql procedure else statement
 * @author zz
 *
 */
public class MySqlElseStatement extends MySqlObjectImpl {

    private List<SQLStatement> statements = new ArrayList<SQLStatement>();

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, statements);
        }
        visitor.endVisit(this);
    }

    public List<SQLStatement> getStatements() {
        return statements;
    }

    public void setStatements(List<SQLStatement> statements) {
        this.statements = statements;
    }

}
