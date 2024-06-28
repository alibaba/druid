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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

/**
 * @author lizongbo
 */
public class SQLPartitionOf extends SQLObjectImpl {
    protected SQLExprTableSource parentTable;
    private boolean useDefault;
    private SQLName columnName;
    private SQLName constraintName;
    private SQLExpr checkExpr;
    private SQLExpr defaultExpr;
    private List<SQLExpr> forValuesFrom;
    private List<SQLExpr> forValuesTo;
    private List<SQLExpr> forValuesIn;

    private SQLExpr forValuesModulus;

    private SQLExpr forValuesRemainder;

    public SQLExprTableSource getParentTable() {
        return parentTable;
    }

    public void setParentTable(SQLExprTableSource parentTable) {
        this.parentTable = parentTable;
    }

    public boolean isUseDefault() {
        return useDefault;
    }

    public void setUseDefault(boolean useDefault) {
        this.useDefault = useDefault;
    }

    public List<SQLExpr> getForValuesFrom() {
        return forValuesFrom;
    }

    public void setForValuesFrom(List<SQLExpr> forValuesFrom) {
        this.forValuesFrom = forValuesFrom;
    }

    public List<SQLExpr> getForValuesTo() {
        return forValuesTo;
    }

    public void setForValuesTo(List<SQLExpr> forValuesTo) {
        this.forValuesTo = forValuesTo;
    }

    public List<SQLExpr> getForValuesIn() {
        return forValuesIn;
    }

    public void setForValuesIn(List<SQLExpr> forValuesIn) {
        this.forValuesIn = forValuesIn;
    }

    public SQLName getColumnName() {
        return columnName;
    }

    public void setColumnName(SQLName columnName) {
        this.columnName = columnName;
    }

    public SQLName getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(SQLName constraintName) {
        this.constraintName = constraintName;
    }

    public SQLExpr getCheckExpr() {
        return checkExpr;
    }

    public void setCheckExpr(SQLExpr checkExpr) {
        this.checkExpr = checkExpr;
    }

    public SQLExpr getDefaultExpr() {
        return defaultExpr;
    }

    public void setDefaultExpr(SQLExpr defaultExpr) {
        this.defaultExpr = defaultExpr;
    }

    public SQLExpr getForValuesModulus() {
        return forValuesModulus;
    }

    public void setForValuesModulus(SQLExpr forValuesModulus) {
        this.forValuesModulus = forValuesModulus;
    }

    public SQLExpr getForValuesRemainder() {
        return forValuesRemainder;
    }

    public void setForValuesRemainder(SQLExpr forValuesRemainder) {
        this.forValuesRemainder = forValuesRemainder;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, parentTable);
            if (columnName != null) {
                acceptChild(v, columnName);
            }
            if (constraintName != null) {
                acceptChild(v, constraintName);
            }
            if (checkExpr != null) {
                acceptChild(v, checkExpr);
            }
            if (defaultExpr != null) {
                acceptChild(v, defaultExpr);
            }
            if (forValuesFrom != null) {
                acceptChild(v, forValuesFrom);
            }
            if (forValuesTo != null) {
                acceptChild(v, forValuesTo);
            }
            if (forValuesIn != null) {
                acceptChild(v, forValuesIn);
            }
            if (forValuesModulus != null) {
                acceptChild(v, forValuesModulus);
            }
            if (forValuesRemainder != null) {
                acceptChild(v, forValuesRemainder);
            }
        }
        v.endVisit(this);
    }
}
