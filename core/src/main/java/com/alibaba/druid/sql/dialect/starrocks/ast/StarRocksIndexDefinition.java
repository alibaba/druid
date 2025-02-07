package com.alibaba.druid.sql.dialect.starrocks.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * INDEX index_name (col_name[, col_name, ...]) [USING BITMAP] [COMMENT '']
 * @author lizongbo
 * @see <a href="https://docs.starrocks.io/zh/docs/sql-reference/sql-statements/data-definition/CREATE_TABLE/">...</a>
 */
public class StarRocksIndexDefinition extends SQLObjectImpl implements SQLTableElement {
    private SQLName indexName;
    private List<SQLName> columns = new ArrayList<SQLName>();
    private String indexType;
    private SQLExpr comment;
    private final List<SQLAssignItem> indexOption = new ArrayList<SQLAssignItem>();

    public List<SQLAssignItem> getIndexOption() {
        return indexOption;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public SQLName getIndexName() {
        return indexName;
    }

    public void setIndexName(SQLName indexName) {
        this.indexName = indexName;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public void setColumns(List<SQLName> columns) {
        this.columns = columns;
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        this.comment = comment;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, indexName);
            acceptChild(v, columns);
        }
        v.endVisit(this);

    }

    @Override
    public StarRocksIndexDefinition clone() {
        StarRocksIndexDefinition x = new StarRocksIndexDefinition();
        if (indexName != null) {
            x.setIndexName(indexName.clone());
        }
        for (SQLName column : columns) {
            SQLName columnCloned = column.clone();
            columnCloned.setParent(x);
            x.columns.add(columnCloned);
        }
        x.indexType = indexType;
        x.comment = comment;
        return x;
    }
}
