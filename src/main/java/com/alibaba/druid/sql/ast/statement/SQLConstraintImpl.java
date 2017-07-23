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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;

import java.util.List;

public abstract class SQLConstraintImpl extends SQLObjectImpl implements SQLConstraint {
    protected String dbType;

    protected SQLName name;
    private Boolean enable;
    private Boolean validate;
    private Boolean rely;

    public List<SQLCommentHint> hints;

    public SQLConstraintImpl(){

    }

    public void cloneTo(SQLConstraintImpl x) {
        if (name != null) {
            x.setName(name.clone());
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
        this.name = name;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void cloneTo(SQLConstraint x) {
        if (name != null) {
            x.setName(name.clone());
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

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public void simplify() {
        if (this.name instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identExpr = (SQLIdentifierExpr) this.name;
            String columnName = identExpr.getName();
            columnName = SQLUtils.normalize(columnName, dbType);
            identExpr.setName(columnName);
        }
    }
}
