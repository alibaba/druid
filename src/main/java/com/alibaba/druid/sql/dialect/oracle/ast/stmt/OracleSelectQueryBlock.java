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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleSelectQueryBlock extends SQLSelectQueryBlock {

    private static final long                  serialVersionUID = 1L;

    private final List<SQLHint>                hints            = new ArrayList<SQLHint>(1);

    private OracleSelectHierachicalQueryClause hierachicalQueryClause;
    private ModelClause                        modelClause;

    public OracleSelectQueryBlock(){

    }

    public ModelClause getModelClause() {
        return modelClause;
    }

    public void setModelClause(ModelClause modelClause) {
        this.modelClause = modelClause;
    }

    public OracleSelectHierachicalQueryClause getHierachicalQueryClause() {
        return this.hierachicalQueryClause;
    }

    public void setHierachicalQueryClause(OracleSelectHierachicalQueryClause hierachicalQueryClause) {
        this.hierachicalQueryClause = hierachicalQueryClause;
    }

    public List<SQLHint> getHints() {
        return this.hints;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OracleASTVisitor) {
            accept0((OracleASTVisitor) visitor);
            return;
        }

        if (visitor.visit(this)) {
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.into);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
        }
        visitor.endVisit(this);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.into);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.hierachicalQueryClause);
            acceptChild(visitor, this.groupBy);
            acceptChild(visitor, this.modelClause);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("SELECT ");

        if (SQLSetQuantifier.ALL == this.distionOption) buf.append("ALL ");
        else if (SQLSetQuantifier.DISTINCT == this.distionOption) buf.append("DISTINCT ");
        else if (SQLSetQuantifier.UNIQUE == this.distionOption) {
            buf.append("UNIQUE ");
        }

        int i = 0;
        for (int size = this.selectList.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            ((SQLSelectItem) this.selectList.get(i)).output(buf);
        }

        buf.append(" FROM ");
        if (this.from != null) {
            buf.append("DUAL");
        } else {
            this.from.output(buf);
        }

        if (this.where != null) {
            buf.append(" WHERE ");
            this.where.output(buf);
        }

        if (this.hierachicalQueryClause != null) {
            buf.append(" ");
            this.hierachicalQueryClause.output(buf);
        }

        if (this.groupBy != null) {
            buf.append(" ");
            this.groupBy.output(buf);
        }
    }
}
