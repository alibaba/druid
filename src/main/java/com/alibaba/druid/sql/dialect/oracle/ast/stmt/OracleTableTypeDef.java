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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleTableTypeDef extends OracleTypeDef {

    private static final long serialVersionUID = 1L;

    private boolean           ref              = false;
    private SQLName           name;
    private boolean           type             = false;

    private boolean           notNull          = false;

    public OracleTableTypeDef(){

    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
        }

        visitor.endVisit(this);
    }

    public boolean isNotNull() {
        return this.notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public SQLName getName() {
        return this.name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public boolean isType() {
        return this.type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public boolean isRef() {
        return this.ref;
    }

    public void setRef(boolean ref) {
        this.ref = ref;
    }

    public void output(StringBuffer buf) {
        if (this.ref) {
            buf.append("REF ");
        }

        buf.append(super.toString());
    }
}
