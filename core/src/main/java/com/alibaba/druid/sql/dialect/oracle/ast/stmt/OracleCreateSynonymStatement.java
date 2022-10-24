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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleCreateSynonymStatement extends OracleStatementImpl implements SQLCreateStatement {
    private boolean orReplace;
    private SQLName name;
    private boolean isPublic;
    private SQLName object;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, object);
        }
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean value) {
        isPublic = value;
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

    public SQLName getObject() {
        return object;
    }

    public void setObject(SQLName object) {
        if (object != null) {
            object.setParent(this);
        }
        this.object = object;
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }
}
