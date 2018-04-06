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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class SQLDateExpr extends SQLExprImpl implements SQLLiteralExpr, SQLValuableExpr {
    public static final SQLDataType DEFAULT_DATA_TYPE = new SQLCharacterDataType("date");

    private SQLExpr literal;

    public SQLDateExpr(){

    }

    public SQLDateExpr(String literal) {
        this.setLiteral(literal);
    }

    public SQLExpr getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        setLiteral(new SQLCharExpr(literal));
    }

    public void setLiteral(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.literal = x;
    }

    public String getValue() {
        if (literal instanceof SQLCharExpr) {
            return ((SQLCharExpr) literal).getText();
        }
        return null;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((literal == null) ? 0 : literal.hashCode());
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
        SQLDateExpr other = (SQLDateExpr) obj;
        if (literal == null) {
            if (other.literal != null) {
                return false;
            }
        } else if (!literal.equals(other.literal)) {
            return false;
        }
        return true;
    }

    public SQLDateExpr clone() {
        SQLDateExpr x = new SQLDateExpr();

        if (this.literal != null) {
            x.setLiteral(literal.clone());
        }

        return x;
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }
}
