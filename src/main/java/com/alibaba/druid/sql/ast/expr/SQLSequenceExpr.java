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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class SQLSequenceExpr extends SQLExprImpl implements SQLReplaceable {

    private SQLName  sequence;
    private Function function;

    public SQLSequenceExpr(){

    }

    public SQLSequenceExpr(SQLName sequence, Function function){
        this.sequence = sequence;
        this.function = function;
    }

    public SQLSequenceExpr clone() {
        SQLSequenceExpr x = new SQLSequenceExpr();
        if (sequence != null) {
            x.setSequence(sequence.clone());
        }
        x.function = function;
        return x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.sequence != null) {
                this.sequence.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.sequence == expr) {
            setSequence((SQLName) target);
            return true;
        }
        return false;
    }

    public static enum Function {
                                 NextVal("NEXTVAL"), CurrVal("CURRVAL"), PrevVal("PREVVAL");

        public final String name;
        public final String name_lcase;

        private Function(String name){
            this.name = name;
            this.name_lcase = name.toLowerCase();
        }
    }

    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(sequence);
    }

    public SQLName getSequence() {
        return sequence;
    }

    public void setSequence(SQLName sequence) {
        this.sequence = sequence;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((function == null) ? 0 : function.hashCode());
        result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SQLSequenceExpr other = (SQLSequenceExpr) obj;
        if (function != other.function) return false;
        if (sequence == null) {
            if (other.sequence != null) return false;
        } else if (!sequence.equals(other.sequence)) return false;
        return true;
    }

}
