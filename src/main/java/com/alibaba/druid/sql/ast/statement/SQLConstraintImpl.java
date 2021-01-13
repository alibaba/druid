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
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;

import java.util.List;

public abstract class SQLConstraintImpl extends SQLObjectImpl implements SQLConstraint, SQLDbTypedObject {
    protected DbType  dbType;
    private SQLName name;
    protected Boolean enable;
    protected Boolean validate;
    protected Boolean rely;
    private SQLExpr comment;

    public List<SQLCommentHint> hints;

    public SQLConstraintImpl(){

    }

    public void cloneTo(SQLConstraintImpl x) {
        if (getName() != null) {
            x.setName(getName().clone());
        }

        x.enable = enable;
        x.validate = validate;
        x.rely = rely;
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }


    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public void setName(String name) {
        this.setName(new SQLIdentifierExpr(name));
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void cloneTo(SQLConstraint x) {
        if (getName() != null) {
            x.setName(getName().clone());
        }
    }

    public Boolean getValidate() {
        return validate;
    }

    public void setValidate(Boolean validate) {
        this.validate = validate;
    }

    public Boolean getRely() {
        return rely;
    }

    public void setRely(Boolean rely) {
        this.rely = rely;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.comment = x;
    }

    public void simplify() {
        if (getName() instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) getName();
            String columnName = identExpr.getName();

            String normalized = SQLUtils.normalize(columnName, dbType);
            if (columnName != normalized) {
                this.setName(normalized);
            }
        }
    }

    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (getName() == expr) {
            setName((SQLName) target);
            return true;
        }

        if (getComment() == expr) {
            setComment(target);
            return true;
        }
        return false;
    }
}
