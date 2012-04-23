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
package com.alibaba.druid.sql.dialect.postgresql.ast.expr;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGAnalyticWindowing;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGOrderBy;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PGAnalytic extends SQLObjectImpl implements PGExpr {

    private static final long       serialVersionUID = 1L;
    private final List<SQLExpr>     partitionBy      = new ArrayList<SQLExpr>();
    private PGOrderBy           orderBy;


	public PGOrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(PGOrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public PGAnalytic(){

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((PGASTVisitor) visitor);
    }

    public void accept0(PGASTVisitor visitor) {
//        if (visitor.visit(this)) {
//            acceptChild(visitor, this.partitionBy);
//            acceptChild(visitor, this.orderBy);
//        }
//        visitor.endVisit(this);
    }


    public List<SQLExpr> getPartitionBy() {
        return this.partitionBy;
    }
}
