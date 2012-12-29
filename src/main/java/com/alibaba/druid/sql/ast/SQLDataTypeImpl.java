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
package com.alibaba.druid.sql.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDataTypeImpl extends SQLObjectImpl implements SQLDataType {

    private static final long     serialVersionUID = -2783296007802532452L;

    protected String              name;
    protected final List<SQLExpr> arguments        = new ArrayList<SQLExpr>();

    public SQLDataTypeImpl(){

    }

    public SQLDataTypeImpl(String name){

        this.name = name;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.arguments);
        }

        visitor.endVisit(this);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SQLExpr> getArguments() {
        return this.arguments;
    }

    public void output(StringBuffer buf) {
        buf.append(this.name);
        if (this.arguments.size() > 0) {
            buf.append("(");
            int i = 0;
            for (int size = this.arguments.size(); i < size; ++i) {
                if (i != 0) {
                    buf.append(", ");
                }
                ((SQLExpr) this.arguments.get(i)).output(buf);
            }
            buf.append(")");
        }
    }
}
