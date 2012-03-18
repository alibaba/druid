package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleForStatement extends OracleStatementImpl {

    private static final long  serialVersionUID = 1L;

    private SQLName            index;

    private SQLExpr            range;

    private List<SQLStatement> statements       = new ArrayList<SQLStatement>();

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, index);
            acceptChild(visitor, range);
            acceptChild(visitor, statements);
        }
        visitor.endVisit(this);
    }

    public SQLName getIndex() {
        return index;
    }

    public void setIndex(SQLName index) {
        this.index = index;
    }

    public SQLExpr getRange() {
        return range;
    }

    public void setRange(SQLExpr range) {
        this.range = range;
    }

    public List<SQLStatement> getStatements() {
        return statements;
    }

    public void setStatements(List<SQLStatement> statements) {
        this.statements = statements;
    }

}
