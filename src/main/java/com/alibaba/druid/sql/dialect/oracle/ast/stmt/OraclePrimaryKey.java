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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OraclePrimaryKey extends OracleSQLObjectImpl implements OracleConstraint, SQLPrimaryKey, SQLTableElement {

    private SQLName                name;
    private List<SQLExpr>          columns = new ArrayList<SQLExpr>();

    private OracleUsingIndexClause using;
    private SQLName                exceptionsInto;
    private Boolean                enable;
    private Initially              initially;
    private Boolean                deferrable;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, columns);
            acceptChild(visitor, using);
            acceptChild(visitor, exceptionsInto);
        }
        visitor.endVisit(this);
    }

    public Boolean getDeferrable() {
        return deferrable;
    }

    public void setDeferrable(Boolean deferrable) {
        this.deferrable = deferrable;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public void setColumns(List<SQLExpr> columns) {
        this.columns = columns;
    }

    public OracleUsingIndexClause getUsing() {
        return using;
    }

    public void setUsing(OracleUsingIndexClause using) {
        this.using = using;
    }

    public SQLName getExceptionsInto() {
        return exceptionsInto;
    }

    public void setExceptionsInto(SQLName exceptionsInto) {
        this.exceptionsInto = exceptionsInto;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Initially getInitially() {
        return initially;
    }

    public void setInitially(Initially initially) {
        this.initially = initially;
    }

}
