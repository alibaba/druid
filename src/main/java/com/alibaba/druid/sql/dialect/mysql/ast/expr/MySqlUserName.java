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
package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.util.Collections;
import java.util.List;

public class MySqlUserName extends MySqlExprImpl implements SQLName, Cloneable {

    private String userName;
    private String host;
    private String identifiedBy;

    private long   userNameHashCod64;
    private long   hashCode64;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;

        this.hashCode64 = 0;
        this.userNameHashCod64 = 0;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;

        this.hashCode64 = 0;
        this.userNameHashCod64 = 0;
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public String getSimpleName() {
        return userName + '@' + host;
    }

    public String getIdentifiedBy() {
        return identifiedBy;
    }

    public void setIdentifiedBy(String identifiedBy) {
        this.identifiedBy = identifiedBy;
    }

    public String toString() {
        return getSimpleName();
    }

    public MySqlUserName clone() {
        MySqlUserName x = new MySqlUserName();

        x.userName     = userName;
        x.host         = host;
        x.identifiedBy = identifiedBy;

        return x;
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }

    public long nameHashCode64() {
        if (userNameHashCod64 == 0
                && userName != null) {
            userNameHashCod64 = FnvHash.hashCode64(userName);
        }
        return userNameHashCod64;
    }

    @Override
    public long hashCode64() {
        if (hashCode64 == 0) {
            if (host != null) {
                long hash = FnvHash.hashCode64(host);
                hash ^= '@';
                hash *= 0x100000001b3L;
                hash = FnvHash.hashCode64(hash, userName);

                hashCode64 = hash;
            } else {
                hashCode64 = nameHashCode64();
            }
        }

        return hashCode64;
    }
}
