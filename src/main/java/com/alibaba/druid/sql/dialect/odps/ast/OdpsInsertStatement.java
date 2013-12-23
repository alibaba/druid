package com.alibaba.druid.sql.dialect.odps.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OdpsInsertStatement extends SQLStatementImpl implements SQLStatement {

    private SQLSubqueryTableSource from;

    private List<OdpsInsert>       items = new ArrayList<OdpsInsert>();

    public void setFrom(SQLSubqueryTableSource from) {
        this.from = from;
    }

    public SQLSubqueryTableSource getFrom() {
        return from;
    }

    public List<OdpsInsert> getItems() {
        return items;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OdpsASTVisitor) visitor);
    }
    
    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, from);
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }
}
