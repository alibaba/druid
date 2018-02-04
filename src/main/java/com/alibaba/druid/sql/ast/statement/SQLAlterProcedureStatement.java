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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleStatementImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterProcedureStatement extends SQLStatementImpl implements SQLAlterStatement {

    private SQLExpr name;

    private boolean compile       = false;
    private boolean reuseSettings = false;

    private SQLExpr comment;
    private boolean languageSql;
    private boolean containsSql;
    private SQLExpr sqlSecurity;

    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr name) {
        this.name = name;
    }

    public boolean isCompile() {
        return compile;
    }

    public void setCompile(boolean compile) {
        this.compile = compile;
    }

    public boolean isReuseSettings() {
        return reuseSettings;
    }

    public void setReuseSettings(boolean reuseSettings) {
        this.reuseSettings = reuseSettings;
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

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }
}
