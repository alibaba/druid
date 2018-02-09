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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class SQLTimestampExpr extends SQLExprImpl implements SQLValuableExpr {
    public static final SQLDataType DEFAULT_DATA_TYPE = new SQLCharacterDataType("datetime");

    protected String  literal;
    protected String  timeZone;
    protected boolean withTimeZone = false;

    public SQLTimestampExpr(){
        
    }

    public SQLTimestampExpr(String literal){
        this.literal = literal;
    }


    public SQLTimestampExpr clone() {
        SQLTimestampExpr x = new SQLTimestampExpr();
        x.literal = literal;
        x.timeZone = timeZone;
        x.withTimeZone = withTimeZone;
        return x;
    }

    public String getValue() {
        return literal;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isWithTimeZone() {
        return withTimeZone;
    }

    public void setWithTimeZone(boolean withTimeZone) {
        this.withTimeZone = withTimeZone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((literal == null) ? 0 : literal.hashCode());
        result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
        result = prime * result + (withTimeZone ? 1231 : 1237);
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
        SQLTimestampExpr other = (SQLTimestampExpr) obj;
        if (literal == null) {
            if (other.literal != null) {
                return false;
            }
        } else if (!literal.equals(other.literal)) {
            return false;
        }
        if (timeZone == null) {
            if (other.timeZone != null) {
                return false;
            }
        } else if (!timeZone.equals(other.timeZone)) {
            return false;
        }
        if (withTimeZone != other.withTimeZone) {
            return false;
        }
        return true;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public String toString() {
        return SQLUtils.toSQLString(this, null);
    }

    public SQLDataType computeDataType() {
        return DEFAULT_DATA_TYPE;
    }

    @Override
    public List getChildren() {
        return Collections.emptyList();
    }
}
