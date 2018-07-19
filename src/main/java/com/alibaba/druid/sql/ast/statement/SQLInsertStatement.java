/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLInsertStatement extends SQLInsertInto implements SQLStatement {
    protected SQLWithSubqueryClause with;

    protected String dbType;

    protected boolean upsert = false; // for phoenix

    private boolean afterSemi;

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

    public static class ValuesClause extends SQLObjectImpl {

        private final     List<SQLExpr> values;
        private transient String        originalString;
        private transient int           replaceCount;

        public ValuesClause(){
            this(new ArrayList<SQLExpr>());
        }

        public ValuesClause clone() {
            ValuesClause x = new ValuesClause(new ArrayList<SQLExpr>(this.values.size()));
            for (SQLExpr v : values) {
                x.addValue(v);
            }
            return x;
        }

        public ValuesClause(List<SQLExpr> values){
            this.values = values;
            for (int i = 0; i < values.size(); ++i) {
                values.get(i).setParent(this);
            }
        }

        public void addValue(SQLExpr value) {
            value.setParent(this);
            values.add(value);
        }

        public List<SQLExpr> getValues() {
            return values;
        }

        public void output(StringBuffer buf) {
            buf.append(" VALUES (");
            for (int i = 0, size = values.size(); i < size; ++i) {
                if (i != 0) {
                    buf.append(", ");
                }
                values.get(i).output(buf);
            }
            buf.append(")");
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                this.acceptChild(visitor, values);
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
    public String getDbType() {
        return dbType;
    }
    
    public void setDbType(String dbType) {
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

    public String toLowerCaseString() {
        return SQLUtils.toSQLString(this, dbType, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
    }
}
