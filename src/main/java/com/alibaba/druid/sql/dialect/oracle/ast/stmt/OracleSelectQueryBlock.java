/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.ModelClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleSelectQueryBlock extends SQLSelectQueryBlock {

    private final List<SQLCommentHint>         hints = new ArrayList<SQLCommentHint>(1);

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

    public List<SQLCommentHint> getHints() {
        return this.hints;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OracleASTVisitor) {
            accept0((OracleASTVisitor) visitor);
            return;
        }

        super.accept0(visitor);
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
    
    public String toString() {
        return SQLUtils.toOracleString(this);
    }
}
