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

import com.alibaba.druid.sql.ast.SQLParameter;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLBlockStatement extends SQLStatementImpl {
    private String             labelName;
    private String             endLabel;
    private List<SQLParameter> parameters    = new ArrayList<SQLParameter>();
    private List<SQLStatement> statementList = new ArrayList<SQLStatement>();
    public SQLStatement        exception;
    private boolean            endOfCommit;

    public SQLBlockStatement() {

    }

    public List<SQLStatement> getStatementList() {
        return statementList;
    }

    public void setStatementList(List<SQLStatement> statementList) {
        this.statementList = statementList;
    }
    
    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, parameters);
            acceptChild(visitor, statementList);
            acceptChild(visitor, exception);
        }
        visitor.endVisit(this);
    }

    public List<SQLParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<SQLParameter> parameters) {
        this.parameters = parameters;
    }

    public SQLStatement getException() {
        return exception;
    }

    public void setException(SQLStatement exception) {
        if (exception != null) {
            exception.setParent(this);
        }
        this.exception = exception;
    }

    public String getEndLabel() {
        return endLabel;
    }

    public void setEndLabel(String endLabel) {
        this.endLabel = endLabel;
    }

    public SQLBlockStatement clone() {
        SQLBlockStatement x = new SQLBlockStatement();
        x.labelName = labelName;
        x.endLabel = endLabel;

        for (SQLParameter p : parameters) {
            SQLParameter p2 = p.clone();
            p2.setParent(x);
            x.parameters.add(p2);
        }

        for (SQLStatement stmt : statementList) {
            SQLStatement stmt2 = stmt.clone();
            stmt2.setParent(x);
            x.statementList.add(stmt2);
        }

        if (exception != null) {
            x.setException(exception.clone());
        }

        return x;
    }

    public SQLParameter findParameter(long hash) {
        for (SQLParameter param : this.parameters) {
            if (param.getName().nameHashCode64() == hash) {
                return param;
            }
        }

        return null;
    }

    public boolean isEndOfCommit() {
        return endOfCommit;
    }

    public void setEndOfCommit(boolean value) {
        this.endOfCommit = value;
    }
}
