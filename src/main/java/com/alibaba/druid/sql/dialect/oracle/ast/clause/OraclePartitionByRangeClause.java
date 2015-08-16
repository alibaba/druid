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
package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPartitioningClause;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OraclePartitionByRangeClause extends OracleSQLObjectImpl implements SQLPartitioningClause {

    private List<SQLName>                 columns = new ArrayList<SQLName>();

    private SQLExpr                       interval;
    private List<SQLName>                 storeIn = new ArrayList<SQLName>();

    private List<OracleRangeValuesClause> ranges  = new ArrayList<OracleRangeValuesClause>();

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columns);
            acceptChild(visitor, interval);
            acceptChild(visitor, storeIn);
            acceptChild(visitor, ranges);
        }
        visitor.endVisit(this);
    }

    public List<OracleRangeValuesClause> getRanges() {
        return ranges;
    }

    public void setRanges(List<OracleRangeValuesClause> ranges) {
        this.ranges = ranges;
    }

    public List<SQLName> getStoreIn() {
        return storeIn;
    }

    public void setStoreIn(List<SQLName> storeIn) {
        this.storeIn = storeIn;
    }

    public SQLExpr getInterval() {
        return interval;
    }

    public void setInterval(SQLExpr interval) {
        this.interval = interval;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public void setColumns(List<SQLName> columns) {
        this.columns = columns;
    }

}
