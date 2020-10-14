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

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLSelectGroupByClause extends SQLObjectImpl implements SQLReplaceable {

    private final List<SQLExpr> items      = new ArrayList<SQLExpr>();
    private SQLExpr             having;
    private boolean             withRollUp = false;
    private boolean             withCube   = false;

    private boolean             distinct   = false;
    private boolean             paren      = false;

    public SQLSelectGroupByClause(){

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (int i = 0; i < this.items.size(); i++) {
                SQLExpr item = items.get(i);
                if (item != null) {
                    item.accept(visitor);
                }
            }

            if (having != null) {
                having.accept(visitor);
            }
        }

        visitor.endVisit(this);
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isWithRollUp() {
        return withRollUp;
    }

    public void setWithRollUp(boolean withRollUp) {
        this.withRollUp = withRollUp;
    }
    
    
    public boolean isWithCube() {
        return withCube;
    }

    public void setWithCube(boolean withCube) {
        this.withCube = withCube;
    }

    public SQLExpr getHaving() {
        return this.having;
    }

    public void setHaving(SQLExpr having) {
        if (having != null) {
            having.setParent(this);
        }

        this.having = having;
    }

    public void addHaving(SQLExpr condition) {
        if (condition == null) {
            return;
        }

        if (having == null) {
            having = condition;
        } else {
            having = SQLBinaryOpExpr.and(having, condition);
        }
    }

    public List<SQLExpr> getItems() {
        return this.items;
    }

    public boolean containsItem(SQLExpr item) {
        return this.items.contains(item);
    }

    public void addItem(SQLExpr sqlExpr) {
        if (sqlExpr != null) {
            sqlExpr.setParent(this);
            this.items.add(sqlExpr);
        }
    }

    public void addItem(int index, SQLExpr sqlExpr) {
        if (sqlExpr != null) {
            sqlExpr.setParent(this);
            this.items.add(index, sqlExpr);
        }
    }

    public SQLSelectGroupByClause clone() {
        SQLSelectGroupByClause x = new SQLSelectGroupByClause();
        for (SQLExpr item : items) {
            SQLExpr item2 = item.clone();
            item2.setParent(x);
            x.items.add(item2);
        }
        if (having != null) {
            x.setHaving(having.clone());
        }
        x.withRollUp = withRollUp;
        x.withCube = withCube;
        x.distinct = distinct;
        x.paren = paren;
        if (hint != null) {
            x.setHint(hint.clone());
        }
        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (expr == having) {
            setHaving(target);
            return true;
        }

        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i) == expr) {
                if (target instanceof SQLIntegerExpr) {
                    items.remove(i);
                } else {
                    items.set(i, target);
                }
                return true;
            }
        }
        return false;
    }

    public SQLCommentHint getHint() {
        return hint;
    }

    public void setHint(SQLCommentHint hint) {
        this.hint = hint;
    }

    public boolean isParen() {
        return paren;
    }

    public void setParen(boolean paren) {
        this.paren = paren;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SQLSelectGroupByClause that = (SQLSelectGroupByClause) o;

        if (withRollUp != that.withRollUp) {
            return false;
        }
        if (withCube != that.withCube) {
            return false;
        }
        if (distinct != that.distinct) {
            return false;
        }
        if (paren != that.paren) {
            return false;
        }
        if (items != null ? !items.equals(that.items) : that.items != null) {
            return false;
        }
        if (having != null ? !having.equals(that.having) : that.having != null) {
            return false;
        }
        return hint != null ? hint.equals(that.hint) : that.hint == null;
    }

    @Override
    public int hashCode()
    {
        int result = items != null ? items.hashCode() : 0;
        result = 31 * result + (having != null ? having.hashCode() : 0);
        result = 31 * result + (withRollUp ? 1 : 0);
        result = 31 * result + (withCube ? 1 : 0);
        result = 31 * result + (distinct ? 1 : 0);
        result = 31 * result + (paren ? 1 : 0);
        result = 31 * result + (hint != null ? hint.hashCode() : 0);
        return result;
    }
}
