/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SQLSetStatement extends SQLStatementImpl {
    private Option option;

    private List<SQLAssignItem> items = new ArrayList<SQLAssignItem>();
    
    private List<SQLCommentHint> hints;

    public SQLSetStatement(){
    }
    
    public SQLSetStatement(DbType dbType){
        super (dbType);
    }
    
    public SQLSetStatement(SQLExpr target, SQLExpr value){
        this(target, value, null);
    }

    public SQLSetStatement(SQLExpr target, SQLExpr value, DbType dbType){
        super (dbType);
        SQLAssignItem item = new SQLAssignItem(target, value);
        item.setParent(this);
        this.items.add(item);
    }

    public static SQLSetStatement plus(SQLName target) {
        SQLExpr value = new SQLBinaryOpExpr(target.clone(), SQLBinaryOperator.Add, new SQLIntegerExpr(1));
        return new SQLSetStatement(target, value);
    }

    public List<SQLAssignItem> getItems() {
        return items;
    }

    public void setItems(List<SQLAssignItem> items) {
        this.items = items;
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public void set(SQLExpr target, SQLExpr value) {
        SQLAssignItem assignItem = new SQLAssignItem(target, value);
        assignItem.setParent(this);
        this.items.add(assignItem);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.hints);
        }
        visitor.endVisit(this);
    }

    public void output(Appendable buf) {
        try {
            buf.append("SET ");

            for (int i = 0; i < items.size(); ++i) {
                if (i != 0) {
                    buf.append(", ");
                }

                SQLAssignItem item = items.get(i);
                item.output(buf);
            }
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    public SQLSetStatement clone() {
        SQLSetStatement x = new SQLSetStatement();
        for (SQLAssignItem item : items) {
            SQLAssignItem item2 = item.clone();
            item2.setParent(x);
            x.items.add(item2);
        }
        if (hints != null) {
            for (SQLCommentHint hint : hints) {
                SQLCommentHint h2 = hint.clone();
                h2.setParent(x);
                x.hints.add(h2);
            }
        }
        return x;
    }

    public List getChildren() {
        return this.items;
    }

    public static enum Option {
        IDENTITY_INSERT,
        PASSWORD, // mysql
        GLOBAL,
        SESSION,
        LOCAL
    }
}
