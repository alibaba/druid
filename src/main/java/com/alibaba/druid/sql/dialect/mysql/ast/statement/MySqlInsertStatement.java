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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MySqlInsertStatement extends SQLInsertStatement {

    private boolean             lowPriority        = false;
    private boolean             delayed            = false;
    private boolean             highPriority       = false;
    private boolean             ignore             = false;

    private List<ValuesClause>  valuesList         = new ArrayList<ValuesClause>();

    private final List<SQLExpr> duplicateKeyUpdate = new ArrayList<SQLExpr>();

    public List<SQLExpr> getDuplicateKeyUpdate() {
        return duplicateKeyUpdate;
    }

    public ValuesClause getValues() {
        if (valuesList.size() == 0) {
            return null;
        }
        return valuesList.get(0);
    }

    public void setValues(ValuesClause values) {
        if (valuesList.size() == 0) {
            valuesList.add(values);
        } else {
            valuesList.set(0, values);
        }
    }

    public List<ValuesClause> getValuesList() {
        return valuesList;
    }

    public void setValuesList(List<ValuesClause> valuesList) {
		this.valuesList = valuesList;
	}

	public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            throw new IllegalArgumentException("not support visitor type : " + visitor.getClass().getName());
        }
    }

    public void output(StringBuffer buf) {
        new MySqlOutputVisitor(buf).visit(this);
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, getTableSource());
            this.acceptChild(visitor, getColumns());
            this.acceptChild(visitor, getValuesList());
            this.acceptChild(visitor, getQuery());
            this.acceptChild(visitor, getDuplicateKeyUpdate());
        }

        visitor.endVisit(this);
    }
}
