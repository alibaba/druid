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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterFunctionStatement extends SQLStatementImpl {
    private SQLName name;

    private boolean debug;
    private boolean reuseSettings;

    private SQLExpr comment;
    private boolean languageSql;
    private boolean containsSql;
    private SQLExpr sqlSecurity;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
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

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    public boolean isReuseSettings() {
        return reuseSettings;
    }

    public void setReuseSettings(boolean x) {
        this.reuseSettings = x;
    }

    public boolean isLanguageSql() {
        return languageSql;
    }

    public void setLanguageSql(boolean languageSql) {
        this.languageSql = languageSql;
    }

    public boolean isContainsSql() {
        return containsSql;
    }

    public void setContainsSql(boolean containsSql) {
        this.containsSql = containsSql;
    }

    public SQLExpr getSqlSecurity() {
        return sqlSecurity;
    }

    public void setSqlSecurity(SQLExpr sqlSecurity) {
        if (sqlSecurity != null) {
            sqlSecurity.setParent(this);
        }
        this.sqlSecurity = sqlSecurity;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, comment);
            acceptChild(visitor, sqlSecurity);
        }
        visitor.endVisit(this);
    }
}
