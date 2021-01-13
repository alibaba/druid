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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SQLInListExpr extends SQLExprImpl implements SQLReplaceable, Serializable {

    private static final long serialVersionUID = 1L;
    private boolean not = false;
    private SQLExpr expr;
    private List<SQLExpr> targetList = new ArrayList<SQLExpr>();

    //for ads query hint
    protected SQLCommentHint hint;

    public SQLInListExpr() {

    }

    public SQLInListExpr(SQLExpr expr) {
        this.setExpr(expr);
    }

    public SQLInListExpr(String expr, String... values) {
        this.setExpr(
                SQLUtils.toSQLExpr(expr));

        for (String value : values) {
            targetList.add(new SQLCharExpr(value));
        }
    }

    public SQLInListExpr(SQLExpr expr, boolean not) {
        this.setExpr(expr);
        this.not = not;
    }

    public SQLInListExpr clone() {
        SQLInListExpr x = new SQLInListExpr();
        x.not = not;
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        for (SQLExpr e : targetList) {
            SQLExpr e2 = e.clone();
            e2.setParent(x);
            x.targetList.add(e2);
        }
        return x;
    }

    public boolean isNot() {
        return this.not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }

        this.expr = expr;
    }

    public void addTarget(SQLExpr x) {
        x.setParent(this);
        targetList.add(x);
    }

    public void addTarget(int index, SQLExpr x) {
        x.setParent(this);
        targetList.add(index, x);
    }

    public List<SQLExpr> getTargetList() {
        return this.targetList;
    }

    public boolean sortTargetList() {
        if (targetList.size() < 2) {
            return true;
        }

        Class<?> firstClass = targetList.get(0).getClass();
        for (int i = 1; i < targetList.size(); i++) {
            if (targetList.get(i).getClass() != firstClass) {
                return false;
            }
        }

        if (!Comparable.class.isAssignableFrom(firstClass)) {
            return false;
        }

        Collections.sort((List) targetList);
        return true;
    }

    public void setTargetList(List<SQLExpr> targetList) {
        this.targetList = targetList;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (expr != null) {
                expr.accept(visitor);
            }

            if (targetList != null) {
                for (SQLExpr item : targetList) {
                    if (item != null) {
                        item.accept(visitor);
                    }
                }
            }
        }

        visitor.endVisit(this);
    }

    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (this.expr != null) {
            children.add(this.expr);
        }
        children.addAll(this.targetList);
        return children;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        result = prime * result + (not ? 1231 : 1237);
        result = prime * result + ((targetList == null) ? 0 : targetList.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLInListExpr other = (SQLInListExpr) obj;
        if (expr == null) {
            if (other.expr != null) {
                return false;
            }
        } else if (!expr.equals(other.expr)) {
            return false;
        }
        if (not != other.not) {
            return false;
        }
        if (targetList == null) {
            if (other.targetList != null) {
                return false;
            }
        } else if (!targetList.equals(other.targetList)) {
            return false;
        }
        return true;
    }

    public SQLDataType computeDataType() {
        return SQLBooleanExpr.DATA_TYPE;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.expr == expr) {
            setExpr(target);
            return true;
        }

        for (int i = targetList.size() - 1; i >= 0; i--) {
            if (targetList.get(i) == expr) {
                if (target == null) {
                    targetList.remove(i);
                } else {
                    targetList.set(i, target);
                    target.setParent(this);
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
}
