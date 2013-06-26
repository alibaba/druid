/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlCreateUserStatement extends MySqlStatementImpl {

    private static final long       serialVersionUID = 1L;

    private List<UserSpecification> users            = new ArrayList<UserSpecification>(2);

    public List<UserSpecification> getUsers() {
        return users;
    }

    public void setUsers(List<UserSpecification> users) {
        this.users = users;
    }
    
    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, users);
        }
        visitor.endVisit(this);
    }

    public static class UserSpecification extends MySqlObjectImpl {

        private static final long serialVersionUID = 1L;

        private SQLExpr           user;
        private SQLExpr           password;
        private SQLExpr           authPlugin;

        public SQLExpr getUser() {
            return user;
        }

        public void setUser(SQLExpr user) {
            this.user = user;
        }

        public SQLExpr getPassword() {
            return password;
        }

        public void setPassword(SQLExpr password) {
            this.password = password;
        }

        public SQLExpr getAuthPlugin() {
            return authPlugin;
        }

        public void setAuthPlugin(SQLExpr authPlugin) {
            this.authPlugin = authPlugin;
        }

        @Override
        public void accept0(MySqlASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, user);
                acceptChild(visitor, password);
                acceptChild(visitor, authPlugin);
            }
            visitor.endVisit(this);
        }

    }
}
