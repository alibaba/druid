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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySqlAlterUserStatement extends MySqlStatementImpl implements SQLAlterStatement {

    private boolean ifExists = false;

    private final List<AlterUser> alterUsers = new ArrayList<AlterUser>();

    private PasswordOption passwordOption;

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (AlterUser alterUser : alterUsers) {
                acceptChild(visitor, alterUser.user);
            }
            if (passwordOption != null && passwordOption.getIntervalDays() != null) {
                acceptChild(visitor, passwordOption.getIntervalDays());
            }
        }
        visitor.endVisit(this);
    }

    public boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    public List<SQLExpr> getUsers() {
        List<SQLExpr> users = new ArrayList<SQLExpr>();
        for (AlterUser alterUser : alterUsers) {
            users.add(alterUser.user);
        }
        return users;
    }

    public List<AlterUser> getAlterUsers() {
        return alterUsers;
    }

    public PasswordOption getPasswordOption() {
        return passwordOption;
    }

    public void setPasswordOption(PasswordOption passwordOption) {
        this.passwordOption = passwordOption;
    }

    public static class AuthOption {
        private SQLCharExpr authString;

        public SQLCharExpr getAuthString() {
            return authString;
        }

        public void setAuthString(SQLCharExpr authString) {
            this.authString = authString;
        }
    }

    public static class AlterUser {
        private SQLExpr user;
        private AuthOption authOption;

        public SQLExpr getUser() {
            return user;
        }

        public void setUser(SQLExpr user) {
            this.user = user;
        }

        public AuthOption getAuthOption() {
            return authOption;
        }

        public void setAuthOption(AuthOption authOption) {
            this.authOption = authOption;
        }
    }

    public static class PasswordOption {
        private PasswordExpire expire;
        private SQLIntegerExpr intervalDays;

        public PasswordExpire getExpire() {
            return expire;
        }

        public void setExpire(PasswordExpire expire) {
            this.expire = expire;
        }

        public SQLIntegerExpr getIntervalDays() {
            return intervalDays;
        }

        public void setIntervalDays(SQLIntegerExpr intervalDays) {
            this.intervalDays = intervalDays;
        }
    }

    public enum PasswordExpire {
        PASSWORD_EXPIRE,
        PASSWORD_EXPIRE_DEFAULT,
        PASSWORD_EXPIRE_NEVER,
        PASSWORD_EXPIRE_INTERVAL
    }
}
