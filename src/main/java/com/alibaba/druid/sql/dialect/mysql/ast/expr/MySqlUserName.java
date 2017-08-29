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
package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.util.FNVUtils;

public class MySqlUserName extends MySqlExprImpl implements SQLName, Cloneable {

    private String userName;
    private String host;

    protected transient long userName_hash;

    protected long hashCode64;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;

        hashCode64 = 0;
        userName_hash = 0;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;

        hashCode64 = 0;
        userName_hash = 0;
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public String getSimpleName() {
        return userName + '@' + host;
    }

    public String toString() {
        return getSimpleName();
    }

    public MySqlUserName clone() {
        MySqlUserName x = new MySqlUserName();
        x.userName = userName;
        x.host = host;
        return x;
    }

    public long name_hash_lower() {
        if (userName_hash == 0
                && userName != null) {
            final int len = userName.length();

            boolean quote = false;

            String name = this.userName;
            if (len > 2) {
                char c0 = name.charAt(0);
                char c1 = name.charAt(len - 1);
                if (c0 == c1
                        && (c0 == '`' || c1 == '"')) {
                    quote = true;
                }
            }
            if (quote) {
                userName_hash = FNVUtils.fnv_64_lower(name, 1, len -1);
            } else {
                userName_hash = FNVUtils.fnv_64_lower(name);
            }
        }
        return userName_hash;
    }

    @Override
    public long hashCode64() {
        if (hashCode64 == 0) {
            if (host != null) {
                hashCode64 = FNVUtils.fnv_64_lower(host, userName);
            } else {
                hashCode64 = name_hash_lower();
            }
        }
        return hashCode64;
    }
}
