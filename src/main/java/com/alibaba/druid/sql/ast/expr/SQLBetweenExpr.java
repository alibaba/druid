/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLBetweenExpr extends SQLExprImpl implements Serializable {

    private static final long serialVersionUID = 1L;
    public SQLExpr            testExpr;
    private boolean           not;
    public SQLExpr            beginExpr;
    public SQLExpr            endExpr;

    public SQLBetweenExpr(){

    }

    public SQLBetweenExpr(SQLExpr testExpr, SQLExpr beginExpr, SQLExpr endExpr){
        setTestExpr(testExpr);
        setBeginExpr(beginExpr);
        setEndExpr(endExpr);
    }

    public SQLBetweenExpr(SQLExpr testExpr, boolean not, SQLExpr beginExpr, SQLExpr endExpr){
        this(testExpr, beginExpr, endExpr);
        this.not = not;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.testExpr);
            acceptChild(visitor, this.beginExpr);
            acceptChild(visitor, this.endExpr);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getTestExpr() {
        return this.testExpr;
    }

    public void setTestExpr(SQLExpr testExpr) {
        if (testExpr != null) {
            testExpr.setParent(this);
        }
        this.testExpr = testExpr;
    }

    public boolean isNot() {
        return this.not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public SQLExpr getBeginExpr() {
        return this.beginExpr;
    }

    public void setBeginExpr(SQLExpr beginExpr) {
        if (beginExpr != null) {
            beginExpr.setParent(this);
        }
        this.beginExpr = beginExpr;
    }

    public SQLExpr getEndExpr() {
        return this.endExpr;
    }

    public void setEndExpr(SQLExpr endExpr) {
        if (endExpr != null) {
            endExpr.setParent(this);
        }
        this.endExpr = endExpr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((beginExpr == null) ? 0 : beginExpr.hashCode());
        result = prime * result + ((endExpr == null) ? 0 : endExpr.hashCode());
        result = prime * result + (not ? 1231 : 1237);
        result = prime * result + ((testExpr == null) ? 0 : testExpr.hashCode());
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
        SQLBetweenExpr other = (SQLBetweenExpr) obj;
        if (beginExpr == null) {
            if (other.beginExpr != null) {
                return false;
            }
        } else if (!beginExpr.equals(other.beginExpr)) {
            return false;
        }
        if (endExpr == null) {
            if (other.endExpr != null) {
                return false;
            }
        } else if (!endExpr.equals(other.endExpr)) {
            return false;
        }
        if (not != other.not) {
            return false;
        }
        if (testExpr == null) {
            if (other.testExpr != null) {
                return false;
            }
        } else if (!testExpr.equals(other.testExpr)) {
            return false;
        }
        return true;
    }
}
