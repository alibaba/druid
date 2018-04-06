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
package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OracleAnalytic extends SQLOver implements OracleExpr {

    private OracleAnalyticWindowing windowing;

    public OracleAnalytic(){

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.partitionBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.windowing);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        children.addAll(this.partitionBy);
        if (this.orderBy != null) {
            children.add(orderBy);
        }
        if (this.windowing != null) {
            children.add(windowing);
        }
        return children;
    }

    public OracleAnalyticWindowing getWindowing() {
        return this.windowing;
    }

    public OracleAnalytic clone() {
        OracleAnalytic x = new OracleAnalytic();

        cloneTo(x);

        if (windowing != null) {
            x.setWindowing(windowing.clone());
        }

        return x;
    }

    public void setWindowing(OracleAnalyticWindowing windowing) {
        this.windowing = windowing;
    }

    public SQLDataType computeDataType() {
        return null;
    }
}
