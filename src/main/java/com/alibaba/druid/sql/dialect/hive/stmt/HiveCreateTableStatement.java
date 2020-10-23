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
package com.alibaba.druid.sql.dialect.hive.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HiveCreateTableStatement extends SQLCreateTableStatement {
    protected List<SQLExpr>          skewedBy        = new ArrayList<SQLExpr>();
    protected List<SQLExpr>          skewedByOn      = new ArrayList<SQLExpr>();
    protected Map<String, SQLObject> serdeProperties = new LinkedHashMap<String, SQLObject>();
    protected SQLExpr                metaLifeCycle;

    protected boolean                likeQuery       = false; // for DLA

    protected List<SQLAssignItem>    mappedBy        = new ArrayList<SQLAssignItem>(1);
    protected SQLExpr                intoBuckets;
    protected SQLExpr                using;

    public HiveCreateTableStatement() {
        this.dbType = DbType.hive;
    }

    public HiveCreateTableStatement(DbType dbType) {
        this.dbType = dbType;
    }

    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v);
        }
        v.endVisit(this);
    }

    protected void acceptChild(SQLASTVisitor v) {
        super.acceptChild(v);

        acceptChild(v, skewedBy);
        acceptChild(v, skewedByOn);
        for (SQLObject item : serdeProperties.values()) {
            acceptChild(v, item);
        }
        acceptChild(v, metaLifeCycle);
        acceptChild(v, intoBuckets);
    }


    public void cloneTo(HiveCreateTableStatement x) {
        super.cloneTo(x);
        for (SQLExpr item : skewedBy) {
            x.addSkewedBy(item.clone());
        }
        for (SQLExpr item : skewedByOn) {
            x.addSkewedByOn(item.clone());
        }
        for (Map.Entry<String, SQLObject> entry : serdeProperties.entrySet()) {
            SQLObject entryValue = entry.getValue().clone();
            entryValue.setParent(x);
            x.serdeProperties.put(entry.getKey(), entryValue);
        }
        if (metaLifeCycle != null) {
            x.setMetaLifeCycle(metaLifeCycle.clone());
        }

        x.setLikeQuery(this.likeQuery);

        if (mappedBy != null) {
            for (SQLAssignItem item : mappedBy) {
                SQLAssignItem item2 = item.clone();
                item2.setParent(this);
                x.mappedBy.add(item2);
            }
        }

        if (intoBuckets != null) {
            x.intoBuckets = intoBuckets.clone();
        }

        if (using != null) {
            x.setUsing(using.clone());
        }
    }

    public HiveCreateTableStatement clone() {
        HiveCreateTableStatement x = new HiveCreateTableStatement();
        cloneTo(x);
        return x;
    }

    public List<SQLExpr> getSkewedBy() {
        return skewedBy;
    }

    public void addSkewedBy(SQLExpr item) {
        item.setParent(this);
        this.skewedBy.add(item);
    }

    public List<SQLExpr> getSkewedByOn() {
        return skewedByOn;
    }

    public void addSkewedByOn(SQLExpr item) {
        item.setParent(this);
        this.skewedByOn.add(item);
    }

    public Map<String, SQLObject> getSerdeProperties() {
        return serdeProperties;
    }

    public SQLExpr getMetaLifeCycle() {
        return metaLifeCycle;
    }

    public void setMetaLifeCycle(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.metaLifeCycle = x;
    }

    public boolean isLikeQuery() {
        return likeQuery;
    }

    public void setLikeQuery(boolean likeQuery) {
        this.likeQuery = likeQuery;
    }

    public List<SQLAssignItem> getMappedBy() {
        return mappedBy;
    }

    public SQLExpr getIntoBuckets()
    {
        return intoBuckets;
    }

    public void setIntoBuckets(SQLExpr intoBuckets)
    {
        this.intoBuckets = intoBuckets;
    }

    public SQLExpr getUsing() {
        return using;
    }

    public void setUsing(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.using = x;
    }
}
