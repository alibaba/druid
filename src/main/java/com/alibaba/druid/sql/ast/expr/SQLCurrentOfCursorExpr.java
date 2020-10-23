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

import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SQLCurrentOfCursorExpr extends SQLExprImpl implements SQLReplaceable {

    private SQLName cursorName;

    public SQLCurrentOfCursorExpr(){

    }

    public SQLCurrentOfCursorExpr(SQLName cursorName){
        this.cursorName = cursorName;
    }

    public SQLCurrentOfCursorExpr clone() {
        SQLCurrentOfCursorExpr x = new SQLCurrentOfCursorExpr();
        if (cursorName != null) {
            x.setCursorName(cursorName.clone());
        }
        return x;
    }

    public SQLName getCursorName() {
        return cursorName;
    }

    public void setCursorName(SQLName cursorName) {
        if (cursorName != null) {
            cursorName.setParent(this);
        }
        this.cursorName = cursorName;
    }

    @Override
    public void output(Appendable buf) {
        try {
            buf.append("CURRENT OF ");
            cursorName.output(buf);
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (cursorName != null) {
                cursorName.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(this.cursorName);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cursorName == null) ? 0 : cursorName.hashCode());
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
        SQLCurrentOfCursorExpr other = (SQLCurrentOfCursorExpr) obj;
        if (cursorName == null) {
            if (other.cursorName != null) {
                return false;
            }
        } else if (!cursorName.equals(other.cursorName)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.cursorName == expr) {
            setCursorName((SQLName) target);
            return true;
        }
        return false;
    }
}
