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
package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleAnalytic extends SQLObjectImpl {

    private static final long serialVersionUID = 1L;
    private final List<SQLExpr> partitionBy = new ArrayList<SQLExpr>();
    private OracleOrderBy orderBy;
    private OracleAnalyticWindowing windowing;

    public OracleAnalytic() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.partitionBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.windowing);
        }
        visitor.endVisit(this);
    }

    public OracleOrderBy getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(OracleOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public OracleAnalyticWindowing getWindowing() {
        return this.windowing;
    }

    public void setWindowing(OracleAnalyticWindowing windowing) {
        this.windowing = windowing;
    }

    public List<SQLExpr> getPartitionBy() {
        return this.partitionBy;
    }
}
