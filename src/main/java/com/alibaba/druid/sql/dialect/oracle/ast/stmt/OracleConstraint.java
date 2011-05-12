/*
 * Copyright 2011 Alibaba Group.
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
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;

public abstract class OracleConstraint extends OracleSQLObject {

    private static final long       serialVersionUID = 1L;

    protected OracleConstraintState state;
    protected SQLName               name;

    public OracleConstraint(){

    }

    public SQLName getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null) this.name = null;
        else this.name = new SQLIdentifierExpr(name);
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public OracleConstraintState getState() {
        return this.state;
    }

    public void setState(OracleConstraintState state) {
        this.state = state;
    }
}
