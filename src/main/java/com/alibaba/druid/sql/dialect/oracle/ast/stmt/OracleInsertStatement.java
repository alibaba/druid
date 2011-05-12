/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleInsertStatement extends SQLStatementImpl {

    private static final long      serialVersionUID = 1L;

    private final List<OracleHint> hints            = new ArrayList<OracleHint>();
    private Insert                 insert;

    public OracleInsertStatement(){

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OracleASTVisitor) {
            accept0((OracleASTVisitor) visitor);
            return;
        }
        super.accept0(visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.insert);
        }

        visitor.endVisit(this);
    }

    public OracleInsertStatement(Insert insert){

        this.insert = insert;
    }

    public List<OracleHint> getHints() {
        return this.hints;
    }

    public Insert getInsert() {
        return this.insert;
    }

    public void setInsert(Insert insert) {
        this.insert = insert;
    }

    public static class IntoSubQuery extends OracleInsertStatement.InsertSource {

        private static final long serialVersionUID = 1L;

        public IntoSubQuery(){

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            visitor.visit(this);

            visitor.endVisit(this);
        }
    }

    public static class IntoValues extends OracleInsertStatement.InsertSource {

        private static final long   serialVersionUID = 1L;

        private final List<SQLExpr> values           = new ArrayList<SQLExpr>();

        public IntoValues(){

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.values);
            }

            visitor.endVisit(this);
        }

        public List<SQLExpr> getValues() {
            return this.values;
        }
    }

    public static abstract class InsertSource extends SQLObjectImpl {

        private static final long serialVersionUID = 1L;

        public InsertSource(){

        }
    }

    public static class Into extends SQLObjectImpl {

        private static final long   serialVersionUID = 1L;

        private SQLExpr             target;
        private String              alias;
        private final List<SQLExpr> columns          = new ArrayList<SQLExpr>();

        public Into(){

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.target);
                acceptChild(visitor, this.columns);
            }

            visitor.endVisit(this);
        }

        public SQLExpr getTarget() {
            return this.target;
        }

        public void setTarget(SQLExpr target) {
            this.target = target;
        }

        public String getAlias() {
            return this.alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public List<SQLExpr> getColumns() {
            return this.columns;
        }
    }

    public static class MultiTableInsert {
    }

    public static class SigleTableInert extends OracleInsertStatement.Insert {

        private static final long                  serialVersionUID = 1L;

        private OracleInsertStatement.Into         into;
        private OracleInsertStatement.InsertSource source;

        public SigleTableInert(){

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.into);
                acceptChild(visitor, this.source);
            }

            visitor.endVisit(this);
        }

        public OracleInsertStatement.Into getInto() {
            return this.into;
        }

        public void setInto(OracleInsertStatement.Into into) {
            this.into = into;
        }

        public OracleInsertStatement.InsertSource getSource() {
            return this.source;
        }

        public void setSource(OracleInsertStatement.InsertSource source) {
            this.source = source;
        }
    }

    public static abstract class Insert extends SQLObjectImpl {

        private static final long serialVersionUID = 1L;

        public Insert(){

        }
    }
}
