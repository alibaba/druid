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
package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

public class PGTruncateStatement extends SQLTruncateStatement implements PGSQLStatement{

    private static final long  serialVersionUID = 1L;

    private boolean            only;
    private Boolean            restartIdentity;
    private Boolean            cascade;

    public boolean isOnly() {
        return only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public Boolean getRestartIdentity() {
        return restartIdentity;
    }

    public void setRestartIdentity(Boolean restartIdentity) {
        this.restartIdentity = restartIdentity;
    }

    public Boolean getCascade() {
        return cascade;
    }

    public void setCascade(Boolean cascade) {
        this.cascade = cascade;
    }

    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSources);
        }
        visitor.endVisit(this);
    }
}
