/*
 * Copyright 2011 Alibaba Group.
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
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleAlterTableStatement extends SQLStatementImpl {
    private static final long serialVersionUID = 1L;

    private SQLName table;
    private SQLObject node;
    private final List<EnableClause> enableClauses = new ArrayList<EnableClause>();

    public OracleAlterTableStatement() {

    }

    public SQLName getTable() {
        return this.table;
    }

    public void setTable(SQLName table) {
        this.table = table;
    }

    public SQLObject getNode() {
        return this.node;
    }

    public void setNode(SQLObject node) {
        this.node = node;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.table);
            acceptChild(visitor, this.node);
            acceptChild(visitor, this.enableClauses);
        }
        visitor.endVisit(this);
    }

    public List<EnableClause> getEnableClauses() {
        return this.enableClauses;
    }

    public static class ModifyCollectionRetrieval extends OracleAlterTableStatement.ColumnClause {
        private static final long serialVersionUID = 1L;

        private SQLName collectionItem;
        private ReturnAs returnAs;

        public ModifyCollectionRetrieval() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.collectionItem);
            }
            visitor.endVisit(this);
        }

        public SQLName getCollectionItem() {
            return this.collectionItem;
        }

        public void setCollectionItem(SQLName collectionItem) {
            this.collectionItem = collectionItem;
        }

        public ReturnAs getReturnAs() {
            return this.returnAs;
        }

        public void setReturnAs(ReturnAs returnAs) {
            this.returnAs = returnAs;
        }

        public static enum ReturnAs {
            LOCATOR,
            VALUE;
        }
    }

    public static class AddColumnClause extends OracleAlterTableStatement.ColumnClause {
        private static final long serialVersionUID = 1L;

        private final List<OracleTableColumn> columns = new ArrayList<OracleTableColumn>();

        public AddColumnClause() {

        }

        public List<OracleTableColumn> getColumns() {
            return this.columns;
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.columns);
            }
            visitor.endVisit(this);
        }
    }

    public static class ModifyColumnClause extends OracleAlterTableStatement.ColumnClause {
        private static final long serialVersionUID = 1L;

        private final List<OracleTableColumn> columns = new ArrayList<OracleTableColumn>();

        public ModifyColumnClause() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.columns);
            }
            visitor.endVisit(this);
        }

        public List<OracleTableColumn> getColumns() {
            return this.columns;
        }
    }

    public static abstract class ColumnClause extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;

        public ColumnClause() {

        }
    }

    public static enum ConstraintType {
        CONSTAINT,
        UNIQUE,
        PRIMARY_KEY;

        public String toFormalString() {
            if (PRIMARY_KEY.equals(this)) {
                return "PRIMARY KEY";
            }
            return name();
        }
    }

    public static enum IndexType {
        KEEP_INDEX,
        DROP_INDEX;

        public String toFormalString() {
            if (KEEP_INDEX.equals(this)) {
                return "KEEP INDEX";
            }
            if (DROP_INDEX.equals(this)) {
                return "DROP INDEX";
            }
            return name();
        }
    }

    public static class EnableClause extends OracleAlterTableStatement.AlterTableProperties {
        private static final long serialVersionUID = 1L;

        private boolean enable;
        private Boolean validate;
        private OracleAlterTableStatement.ConstraintType type;
        private final List<SQLName> columns = new ArrayList<SQLName>();
        private OracleAlterTableStatement.UsingIndexClause usingIndex;
        private SQLName exceptions;
        private boolean cascade = false;
        private OracleAlterTableStatement.IndexType indexType;
        private SQLName constraintName;

        public EnableClause() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.columns);
                acceptChild(visitor, this.constraintName);
                acceptChild(visitor, this.usingIndex);
                acceptChild(visitor, this.exceptions);
            }
            visitor.endVisit(this);
        }

        public boolean isCascade() {
            return this.cascade;
        }

        public void setCascade(boolean cascade) {
            this.cascade = cascade;
        }

        public boolean isEnable() {
            return this.enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public Boolean getValidate() {
            return this.validate;
        }

        public void setValidate(Boolean validate) {
            this.validate = validate;
        }

        public OracleAlterTableStatement.ConstraintType getType() {
            return this.type;
        }

        public void setType(OracleAlterTableStatement.ConstraintType type) {
            this.type = type;
        }

        public List<SQLName> getColumns() {
            return this.columns;
        }

        public OracleAlterTableStatement.UsingIndexClause getUsingIndex() {
            return this.usingIndex;
        }

        public void setUsingIndex(OracleAlterTableStatement.UsingIndexClause usingIndex) {
            this.usingIndex = usingIndex;
        }

        public SQLName getExceptions() {
            return this.exceptions;
        }

        public void setExceptions(SQLName exceptions) {
            this.exceptions = exceptions;
        }

        public OracleAlterTableStatement.IndexType getIndexType() {
            return this.indexType;
        }

        public void setIndexType(OracleAlterTableStatement.IndexType indexType) {
            this.indexType = indexType;
        }

        public SQLName getConstraintName() {
            return this.constraintName;
        }

        public void setConstraintName(SQLName constraintName) {
            this.constraintName = constraintName;
        }
    }

    public static class DeallocateClause extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;

        private SQLExpr keep;

        public DeallocateClause() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.keep);
            }
            visitor.endVisit(this);
        }

        public SQLExpr getKeep() {
            return this.keep;
        }

        public void setKeep(SQLExpr keep) {
            this.keep = keep;
        }
    }

    public static class EnableTrigger extends OracleAlterTableStatement.AlterTableProperties {
        private static final long serialVersionUID = 1L;

        private boolean enable;

        public EnableTrigger() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            visitor.visit(this);
            visitor.endVisit(this);
        }

        public boolean isEnable() {
            return this.enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }

    public static class UsingIndexClause extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;

        public UsingIndexClause() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            throw new UnsupportedOperationException();
        }
    }

    public static class NoParallelClause extends OracleAlterTableStatement.AlterTableProperties {
        private static final long serialVersionUID = 1L;

        public NoParallelClause() {

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

    public static class ParallelClause extends OracleAlterTableStatement.AlterTableProperties {
        private static final long serialVersionUID = 1L;

        private SQLIntegerExpr value;

        public ParallelClause() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.value);
            }
            visitor.endVisit(this);
        }

        public SQLIntegerExpr getValue() {
            return this.value;
        }

        public void setValue(SQLIntegerExpr value) {
            this.value = value;
        }
    }

    public static abstract class AlterTableProperties extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;

        public AlterTableProperties() {

        }
    }

    public static class ModifyConstaint extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;

        private OracleConstraintState state;
        private final List<SQLName> columns = new ArrayList<SQLName>();
        private OracleAlterTableStatement.ConstraintType type;
        private SQLName constraintName;

        public ModifyConstaint() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.constraintName);
                acceptChild(visitor, this.columns);
            }
            visitor.endVisit(this);
        }

        public OracleConstraintState getState() {
            return this.state;
        }

        public void setState(OracleConstraintState state) {
            this.state = state;
        }

        public OracleAlterTableStatement.ConstraintType getType() {
            return this.type;
        }

        public void setType(OracleAlterTableStatement.ConstraintType indexType) {
            this.type = indexType;
        }

        public SQLName getConstraintName() {
            return this.constraintName;
        }

        public void setConstraintName(SQLName constraintName) {
            this.constraintName = constraintName;
        }

        public List<SQLName> getColumns() {
            return this.columns;
        }
    }

    public static class RenameConstaint extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;

        private SQLName oldName;
        private SQLName newName;

        public RenameConstaint() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.oldName);
                acceptChild(visitor, this.newName);
            }
            visitor.endVisit(this);
        }

        public SQLName getOldName() {
            return this.oldName;
        }

        public void setOldName(SQLName oldName) {
            this.oldName = oldName;
        }

        public SQLName getNewName() {
            return this.newName;
        }

        public void setNewName(SQLName newName) {
            this.newName = newName;
        }
    }

    public static class RenameColumn extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;

        private SQLName oldName;
        private SQLName newName;

        public RenameColumn() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.oldName);
                acceptChild(visitor, this.newName);
            }
            visitor.endVisit(this);
        }

        public SQLName getOldName() {
            return this.oldName;
        }

        public void setOldName(SQLName oldName) {
            this.oldName = oldName;
        }

        public SQLName getNewName() {
            return this.newName;
        }

        public void setNewName(SQLName newName) {
            this.newName = newName;
        }
    }

    public static class DropColumn extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;

        private final List<SQLName> columns = new ArrayList<SQLName>();
        private boolean cascade;
        private boolean invlidate;
        private SQLIntegerExpr checkPoint;

        public DropColumn() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.columns);
            }
            visitor.endVisit(this);
        }

        public List<SQLName> getColumns() {
            return this.columns;
        }

        public boolean isCascade() {
            return this.cascade;
        }

        public void setCascade(boolean cascade) {
            this.cascade = cascade;
        }

        public boolean isInvlidate() {
            return this.invlidate;
        }

        public void setInvlidate(boolean invlidate) {
            this.invlidate = invlidate;
        }

        public SQLIntegerExpr getCheckPoint() {
            return this.checkPoint;
        }

        public void setCheckPoint(SQLIntegerExpr checkPoint) {
            this.checkPoint = checkPoint;
        }
    }

    public static class AddConstraint extends SQLObjectImpl {
        private static final long serialVersionUID = 1L;

        private final List<OracleConstraint> constraints = new ArrayList<OracleConstraint>();

        public AddConstraint() {

        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.constraints);
            }
            visitor.endVisit(this);
        }

        public List<OracleConstraint> getConstraints() {
            return this.constraints;
        }
    }
}
