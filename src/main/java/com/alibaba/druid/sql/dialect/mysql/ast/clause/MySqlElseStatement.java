package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * 
 * @Description: MySql procedure else statement
 * @author zz email:455910092@qq.com
 * @date 2015-9-14
 * @version V1.0
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
