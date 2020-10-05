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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleSelectSubqueryTableSource extends SQLSubqueryTableSource implements OracleSelectTableSource {

    protected OracleSelectPivotBase pivot;


    public OracleSelectSubqueryTableSource(){
    }

    public OracleSelectSubqueryTableSource(String alias){
        super(alias);
    }

    public OracleSelectSubqueryTableSource(SQLSelect select, String alias){
        super(select, alias);
    }

    public OracleSelectSubqueryTableSource(SQLSelect select){
        super(select);
    }

    public OracleSelectPivotBase getPivot() {
        return pivot;
    }

    public void setPivot(OracleSelectPivotBase pivot) {
        this.pivot = pivot;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getHints());
            acceptChild(visitor, this.select);
            acceptChild(visitor, this.pivot);
            acceptChild(visitor, this.flashback);
        }
        visitor.endVisit(this);
    }

    public String toString () {
        return SQLUtils.toOracleString(this);
    }

    public OracleSelectSubqueryTableSource clone() {
        OracleSelectSubqueryTableSource x = new OracleSelectSubqueryTableSource();
        cloneTo(x);

        if (pivot != null) {
            setParent(pivot.clone());
        }

        return x;
    }
}
