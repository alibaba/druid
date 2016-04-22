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
package com.alibaba.druid.sql.dialect.teradata.ast.expr;

import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class TeradataAnalytic extends SQLOver implements TeradataExpr {

    private TeradataAnalyticWindowing windowing;

    public TeradataAnalytic(){

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((TeradataASTVisitor) visitor);
    }

    public void accept0(TeradataASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.partitionBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.windowing);
        }
        visitor.endVisit(this);
    }

    public TeradataAnalyticWindowing getWindowing() {
        return this.windowing;
    }

    public void setWindowing(TeradataAnalyticWindowing windowing) {
        this.windowing = windowing;
    }
}
