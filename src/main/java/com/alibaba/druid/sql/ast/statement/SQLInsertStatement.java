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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLInsertStatement extends SQLInsertInto implements SQLStatement {
    protected SQLWithSubqueryClause with;
    protected boolean upsert = false; // for phoenix

    public SQLInsertStatement(){

    }

    public void cloneTo(SQLInsertStatement x) {
        super.cloneTo(x);
        x.dbType = dbType;
        x.upsert = upsert;
        x.afterSemi = afterSemi;

        if (with != null) {
            x.setWith(with.clone());
        }
    }

    public SQLInsertStatement clone() {
        SQLInsertStatement x = new SQLInsertStatement();
        cloneTo(x);
        return x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSource);
            this.acceptChild(visitor, columns);
            this.acceptChild(visitor, valuesList);
            this.acceptChild(visitor, query);
        }

        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();

        children.add(tableSource);
        children.addAll(this.columns);
        children.addAll(this.valuesList);
        if (query != null) {
            children.add(query);
        }

        return children;
    }

    public boolean isUpsert() {
        return upsert;
    }

    public void setUpsert(boolean upsert) {
        this.upsert = upsert;
    }

    public static class ValuesClause extends SQLObjectImpl implements SQLReplaceable {

        private final     List   values;
        private transient String originalString;
        private transient int    replaceCount;

        public ValuesClause(){
            this(new ArrayList<SQLExpr>());
        }

        public ValuesClause clone() {
            ValuesClause x = new ValuesClause(new ArrayList<SQLExpr>(this.values.size()));
            for (Object v : values) {
                x.addValue(v);
            }
            return x;
        }

        public boolean replace(SQLExpr expr, SQLExpr target) {
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) == expr) {
                    target.setParent(this);
                    values.set(i, target);
                    return true;
                }
            }

            return false;
        }

        public ValuesClause(List<SQLExpr> values){
            this.values = values;
            for (int i = 0; i < values.size(); ++i) {
                values.get(i).setParent(this);
            }
        }


        public ValuesClause(List values, SQLObject parent){
            this.values = values;
            for (int i = 0; i < values.size(); ++i) {
                Object val = values.get(i);
                if (val instanceof SQLObject) {
                    ((SQLObject) val).setParent(this);
                }
            }
            this.parent = parent;
        }

        public void addValue(Object value) {
            if (value instanceof SQLObject) {
                ((SQLObject) value).setParent(this);
            }
            values.add(value);
        }

        public void addValue(SQLExpr value) {
            value.setParent(this);
            values.add(value);
        }

        public List<SQLExpr> getValues() {
            return values;
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                for (int i = 0; i < values.size(); i++) {
                    Object item = values.get(i);
                    if (item instanceof SQLObject) {
                        SQLObject value = (SQLObject) item;
                        value.accept(visitor);
                    }
                }
            }

            visitor.endVisit(this);
        }

        public String getOriginalString() {
            return originalString;
        }

        public void setOriginalString(String originalString) {
            this.originalString = originalString;
        }

        public int getReplaceCount() {
            return replaceCount;
        }

        public void incrementReplaceCount() {
            this.replaceCount++;
        }
    }

    @Override
    public DbType getDbType() {
        return dbType;
    }
    
    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    @Override
    public boolean isAfterSemi() {
        return afterSemi;
    }

    @Override
    public void setAfterSemi(boolean afterSemi) {
        this.afterSemi = afterSemi;
    }


    public SQLWithSubqueryClause getWith() {
        return with;
    }

    public void setWith(SQLWithSubqueryClause with) {
        if (with != null) {
            with.setParent(this);
        }
        this.with = with;
    }

    public String toString() {
        return SQLUtils.toSQLString(this, dbType);
    }
}
