package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLTextLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.mysql.ast.FullTextType;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lijun.cailj 2018/8/13
 */
public class MysqlCreateFullTextCharFilterStatement extends MySqlStatementImpl {


    private SQLName            name; // for all, not null
    private SQLTextLiteralExpr typeName;  // for charfilter/tokenizer/tokenfilter

    protected final List<SQLAssignItem> options = new ArrayList<SQLAssignItem>(); // charfilter/tokenizer/tokenfilter

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, typeName);
            acceptChild(visitor, options);
        }
        visitor.endVisit(this);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLTextLiteralExpr getTypeName() {
        return typeName;
    }

    public void setTypeName(SQLTextLiteralExpr typeName) {
        if (name != null) {
            name.setParent(this);
        }
        this.typeName = typeName;
    }

    public List<SQLAssignItem> getOptions() {
        return options;
    }

    public void addOption(String name, SQLExpr value) {
        SQLAssignItem assignItem = new SQLAssignItem(new SQLIdentifierExpr(name), value);
        assignItem.setParent(this);
        options.add(assignItem);
    }

    public SQLExpr getOption(String name) {
        if (name == null) {
            return null;
        }

        long hash64 = FnvHash.hashCode64(name);

        for (SQLAssignItem item : options) {
            final SQLExpr target = item.getTarget();
            if (target instanceof SQLIdentifierExpr) {
                if (((SQLIdentifierExpr) target).hashCode64() == hash64) {
                    return item.getValue();
                }
            }
        }

        return null;
    }

    public Object getOptionValue(String name) {
        SQLExpr option = getOption(name);
        if (option instanceof SQLValuableExpr) {
            return ((SQLValuableExpr) option).getValue();
        }

        return null;
    }
}
