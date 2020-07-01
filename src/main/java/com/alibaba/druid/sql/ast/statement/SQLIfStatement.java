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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLIfStatement extends SQLStatementImpl {

    private SQLExpr            condition;
    private List<SQLStatement> statements = new ArrayList<SQLStatement>();
    private List<ElseIf>       elseIfList = new ArrayList<ElseIf>();
    private Else               elseItem;

    public SQLIfStatement clone() {
        SQLIfStatement x = new SQLIfStatement();

        for (SQLStatement stmt : statements) {
            SQLStatement stmt2 = stmt.clone();
            stmt2.setParent(x);
            x.statements.add(stmt2);
        }
        for (ElseIf o : elseIfList) {
            ElseIf o2 = o.clone();
            o2.setParent(x);
            x.elseIfList.add(o2);
        }
        if (elseItem != null) {
            x.setElseItem(elseItem.clone());
        }

        return x;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, condition);
            acceptChild(visitor, statements);
            acceptChild(visitor, elseIfList);
            acceptChild(visitor, elseItem);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getCondition() {
        return condition;
    }

    public void setCondition(SQLExpr condition) {
        if (condition != null) {
            condition.setParent(this);
        }
        this.condition = condition;
    }

    public List<SQLStatement> getStatements() {
        return statements;
    }

    public void addStatement(SQLStatement statement) {
        if (statement != null) {
            statement.setParent(this);
        }
        this.statements.add(statement);
    }

    public List<ElseIf> getElseIfList() {
        return elseIfList;
    }

    public Else getElseItem() {
        return elseItem;
    }

    public void setElseItem(Else elseItem) {
        if (elseItem != null) {
            elseItem.setParent(this);
        }
        this.elseItem = elseItem;
    }

    public static class ElseIf extends SQLObjectImpl {

        private SQLExpr            condition;
        private List<SQLStatement> statements = new ArrayList<SQLStatement>();

        @Override
        public void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, condition);
                acceptChild(visitor, statements);
            }
            visitor.endVisit(this);
        }

        public List<SQLStatement> getStatements() {
            return statements;
        }

        public void setStatements(List<SQLStatement> statements) {
            this.statements = statements;
        }

        public SQLExpr getCondition() {
            return condition;
        }

        public void setCondition(SQLExpr condition) {
            if (condition != null) {
                condition.setParent(this);
            }
            this.condition = condition;
        }

        public ElseIf clone() {
            ElseIf x = new ElseIf();

            if (condition != null) {
                x.setCondition(condition.clone());
            }
            for (SQLStatement stmt : statements) {
                SQLStatement stmt2 = stmt.clone();
                stmt2.setParent(x);
                x.statements.add(stmt2);
            }

            return x;
        }
    }

    public static class Else extends SQLObjectImpl {

        private List<SQLStatement> statements = new ArrayList<SQLStatement>();

        @Override
        public void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, statements);
            }
            visitor.endVisit(this);
        }

        public List<SQLStatement> getStatements() {
            return statements;
        }

        public void setStatements(List<SQLStatement> statements) {
            this.statements = statements;
        }

        public Else clone() {
            Else x = new Else();
            for (SQLStatement stmt : statements) {
                SQLStatement stmt2 = stmt.clone();
                stmt2.setParent(x);
                x.statements.add(stmt2);
            }
            return x;
        }
    }
}
