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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLParameter;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleCreateTypeStatement extends OracleStatementImpl implements SQLCreateStatement {
    private boolean            orReplace;
    private SQLName            name;
    private SQLName            authId;
    private boolean            force;
    private SQLName            oid;
    private boolean            body;
    private boolean            object;
    private boolean            paren;
    private Boolean            isFinal;
    private Boolean            instantiable;

    private SQLName            under;

    private List<SQLParameter> parameters = new ArrayList<SQLParameter>();

    private SQLDataType        tableOf;

    private SQLExpr            varraySizeLimit;
    private SQLDataType        varrayDataType;

    private String             wrappedSource;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, authId);
            acceptChild(visitor, oid);
            acceptChild(visitor, under);
            acceptChild(visitor, parameters);
            acceptChild(visitor, tableOf);
            acceptChild(visitor, varraySizeLimit);
            acceptChild(visitor, varrayDataType);
        }
        visitor.endVisit(this);
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

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public SQLName getOid() {
        return oid;
    }

    public void setOid(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.oid = x;
    }

    public SQLName getAuthId() {
        return authId;
    }

    public void setAuthId(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.authId = x;
    }

    public List<SQLParameter> getParameters() {
        return parameters;
    }

    public boolean isBody() {
        return body;
    }

    public void setBody(boolean body) {
        this.body = body;
    }

    public Boolean getFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public Boolean getInstantiable() {
        return instantiable;
    }

    public void setInstantiable(boolean instantiable) {
        this.instantiable = instantiable;
    }

    public SQLDataType getTableOf() {
        return tableOf;
    }

    public void setTableOf(SQLDataType x) {
        if (x != null) {
            x.setParent(this);
        }
        this.tableOf = x;
    }

    public SQLExpr getVarraySizeLimit() {
        return varraySizeLimit;
    }

    public void setVarraySizeLimit(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.varraySizeLimit = x;
    }

    public SQLDataType getVarrayDataType() {
        return varrayDataType;
    }

    public void setVarrayDataType(SQLDataType x) {
        if (x != null) {
            x.setParent(this);
        }
        this.varrayDataType = x;
    }

    public SQLName getUnder() {
        return under;
    }

    public void setUnder(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.under = x;
    }

    public boolean isObject() {
        return object;
    }

    public void setObject(boolean object) {
        this.object = object;
    }

    public boolean isParen() {
        return paren;
    }

    public void setParen(boolean paren) {
        this.paren = paren;
    }

    public String getWrappedSource() {
        return wrappedSource;
    }

    public void setWrappedSource(String wrappedSource) {
        this.wrappedSource = wrappedSource;
    }
}
