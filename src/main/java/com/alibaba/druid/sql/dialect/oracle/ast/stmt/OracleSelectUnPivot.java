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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot.Item;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleSelectUnPivot extends OracleSelectPivotBase {

    private NullsIncludeType                   nullsIncludeType;
    private final List<SQLExpr>                items   = new ArrayList<SQLExpr>();

    private final List<OracleSelectPivot.Item> pivotIn = new ArrayList<Item>();

    public OracleSelectUnPivot(){

    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.pivotIn);
        }
        visitor.endVisit(this);
    }

    public List<OracleSelectPivot.Item> getPivotIn() {
        return this.pivotIn;
    }

    public List<SQLExpr> getItems() {
        return this.items;
    }
    
    public void addItem(SQLExpr item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public NullsIncludeType getNullsIncludeType() {
        return this.nullsIncludeType;
    }

    public void setNullsIncludeType(NullsIncludeType nullsIncludeType) {
        this.nullsIncludeType = nullsIncludeType;
    }

    public static enum NullsIncludeType {
        INCLUDE_NULLS, EXCLUDE_NULLS;

        public static String toString(NullsIncludeType type, boolean ucase) {
            if (INCLUDE_NULLS.equals(type)) {
                return ucase ? "INCLUDE NULLS" : "include nulls";
            }
            if (EXCLUDE_NULLS.equals(type)) {
                return ucase ? "EXCLUDE NULLS" : "exclude nulls";
            }

            throw new IllegalArgumentException();
        }
    }
}
