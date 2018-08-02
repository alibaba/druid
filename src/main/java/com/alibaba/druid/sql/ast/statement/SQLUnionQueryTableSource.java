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

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLUnionQueryTableSource extends SQLTableSourceImpl {

    private SQLUnionQuery union;

    public SQLUnionQueryTableSource(){

    }

    public SQLUnionQueryTableSource(String alias){
        super(alias);
    }

    public SQLUnionQueryTableSource(SQLUnionQuery union, String alias){
        super(alias);
        this.setUnion(union);
    }

    public SQLUnionQueryTableSource(SQLUnionQuery union){
        this.setUnion(union);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, union);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("(");
        this.union.output(buf);
        buf.append(")");
    }

    public SQLUnionQuery getUnion() {
        return union;
    }

    public void setUnion(SQLUnionQuery union) {
        if (union != null) {
            union.setParent(this);
        }
        this.union = union;
    }
}
