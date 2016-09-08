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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLColumnDefinition extends SQLObjectImpl implements SQLTableElement {

    protected SQLName                         name;
    protected SQLDataType                     dataType;
    protected SQLExpr                         defaultExpr;
    protected final List<SQLColumnConstraint> constraints   = new ArrayList<SQLColumnConstraint>(0);
    protected SQLExpr                         comment;

    protected Boolean                         enable;

    // for mysql
    protected boolean                         autoIncrement = false;
    protected SQLExpr                         onUpdate;
    protected SQLExpr                         storage;
    protected SQLExpr                         charsetExpr;
    protected SQLExpr                         asExpr;
    protected boolean                         sorted        = false;
    protected boolean                         virtual       = false;

    protected Identity                        identity;

    public SQLColumnDefinition(){

    }

    public Identity getIdentity() {
        return identity;
    }

    // for sqlserver
    public void setIdentity(Identity identity) {
        if (identity != null) {
            identity.setParent(this);
        }
        this.identity = identity;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLDataType getDataType() {
        return dataType;
    }

    public void setDataType(SQLDataType dataType) {
        this.dataType = dataType;
    }

    public SQLExpr getDefaultExpr() {
        return defaultExpr;
    }

    public void setDefaultExpr(SQLExpr defaultExpr) {
        if (defaultExpr != null) {
            defaultExpr.setParent(this);
        }
        this.defaultExpr = defaultExpr;
    }

    public List<SQLColumnConstraint> getConstraints() {
        return constraints;
    }
    
    public void addConstraint(SQLColumnConstraint constraint) {
        if (constraint != null) {
            constraint.setParent(this);
        }
        this.constraints.add(constraint);
    }

    @Override
    public void output(StringBuffer buf) {
        name.output(buf);
        buf.append(' ');
        this.dataType.output(buf);
        if (defaultExpr != null) {
            buf.append(" DEFAULT ");
            this.defaultExpr.output(buf);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, name);
            this.acceptChild(visitor, dataType);
            this.acceptChild(visitor, defaultExpr);
            this.acceptChild(visitor, constraints);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        this.comment = comment;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public boolean isSorted() {
        return sorted;
    }

    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    public SQLExpr getCharsetExpr() {
        return charsetExpr;
    }

    public void setCharsetExpr(SQLExpr charsetExpr) {
        if (charsetExpr != null) {
            charsetExpr.setParent(this);
        }
        this.charsetExpr = charsetExpr;
    }

    public SQLExpr getAsExpr() {
        return asExpr;
    }

    public void setAsExpr(SQLExpr asExpr) {
        if (charsetExpr != null) {
            charsetExpr.setParent(this);
        }
        this.asExpr = asExpr;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public SQLExpr getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(SQLExpr onUpdate) {
        this.onUpdate = onUpdate;
    }

    public SQLExpr getStorage() {
        return storage;
    }

    public void setStorage(SQLExpr storage) {
        this.storage = storage;
    }

    public static class Identity extends SQLObjectImpl {

        private Integer seed;
        private Integer increment;

        private boolean notForReplication;

        public Identity(){

        }

        public Integer getSeed() {
            return seed;
        }

        public void setSeed(Integer seed) {
            this.seed = seed;
        }

        public Integer getIncrement() {
            return increment;
        }

        public void setIncrement(Integer increment) {
            this.increment = increment;
        }

        public boolean isNotForReplication() {
            return notForReplication;
        }

        public void setNotForReplication(boolean notForReplication) {
            this.notForReplication = notForReplication;
        }

        @Override
        public void accept0(SQLASTVisitor visitor) {
            visitor.visit(this);
            visitor.endVisit(this);
        }

    }
}
