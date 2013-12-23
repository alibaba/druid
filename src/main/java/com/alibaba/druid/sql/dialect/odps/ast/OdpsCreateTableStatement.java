package com.alibaba.druid.sql.dialect.odps.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OdpsCreateTableStatement extends SQLCreateTableStatement {

    private boolean                     ifNotExiists     = false;

    private SQLExprTableSource          like;

    protected SQLExpr                   comment;

    protected List<SQLColumnDefinition> partitionColumns = new ArrayList<SQLColumnDefinition>(2);

    public boolean isIfNotExiists() {
        return ifNotExiists;
    }

    public void setIfNotExiists(boolean ifNotExiists) {
        this.ifNotExiists = ifNotExiists;
    }

    public SQLExprTableSource getLike() {
        return like;
    }

    public void setLike(SQLName like) {
        this.setLike(new SQLExprTableSource(like));
    }

    public void setLike(SQLExprTableSource like) {
        this.like = like;
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        this.comment = comment;
    }

    public List<SQLColumnDefinition> getPartitionColumns() {
        return partitionColumns;
    }
    
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OdpsASTVisitor) visitor);
    }

    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSource);
            this.acceptChild(visitor, tableElementList);
            this.acceptChild(visitor, partitionColumns);
        }
        visitor.endVisit(this);
    }    
}
