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

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 20/05/2017.
 */
public class OracleSupplementalLogGrp extends OracleSQLObjectImpl implements SQLTableElement {
    private SQLName group;
    private List<SQLName> columns = new ArrayList<SQLName>();
    private boolean always = false;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, group);
            acceptChild(visitor, columns);
        }
        visitor.endVisit(this);
    }

    public SQLName getGroup() {
        return group;
    }

    public void setGroup(SQLName group) {
        if (group != null) {
            group.setParent(this);
        }
        this.group = group;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public void addColumn(SQLName column) {
        if (column != null) {
            column.setParent(this);
        }
        this.columns.add(column);
    }

    public boolean isAlways() {
        return always;
    }

    public void setAlways(boolean always) {
        this.always = always;
    }

    public OracleSupplementalLogGrp clone() {
        OracleSupplementalLogGrp x = new OracleSupplementalLogGrp();
        if (group != null) {
            x.setGroup(group.clone());
        }
        for (SQLName column : columns) {
            SQLName c2 = column.clone();
            c2.setParent(x);
            x.columns.add(c2);
        }
        x.always = always;
        return x;
    }
}
