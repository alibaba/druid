/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleCreateDatabaseDbLinkStatement extends OracleStatementImpl {

    private boolean shared;
    private boolean _public;

    private SQLName name;

    private SQLName user;

    private String  password;

    private SQLExpr using;

    private SQLExpr authenticatedUser;
    private String  authenticatedPassword;

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean isPublic() {
        return _public;
    }

    public void setPublic(boolean value) {
        this._public = value;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLName getUser() {
        return user;
    }

    public void setUser(SQLName user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SQLExpr getUsing() {
        return using;
    }

    public void setUsing(SQLExpr using) {
        this.using = using;
    }

    public SQLExpr getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(SQLExpr authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public String getAuthenticatedPassword() {
        return authenticatedPassword;
    }

    public void setAuthenticatedPassword(String authenticatedPassword) {
        this.authenticatedPassword = authenticatedPassword;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, user);
            acceptChild(visitor, using);
            acceptChild(visitor, authenticatedUser);
        }
        visitor.endVisit(this);
    }
}
