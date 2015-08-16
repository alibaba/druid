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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLCreateTableStatement extends SQLStatementImpl implements SQLDDLStatement {

    protected boolean               ifNotExiists     = false;
    protected Type                  type;
    protected SQLExprTableSource    tableSource;

    protected List<SQLTableElement> tableElementList = new ArrayList<SQLTableElement>();

    // for postgresql
    private SQLExprTableSource      inherits;

    public SQLCreateTableStatement(){

    }

    public SQLCreateTableStatement(String dbType){
        super(dbType);
    }

    public SQLName getName() {
        if (tableSource == null) {
            return null;
        }

        return (SQLName) tableSource.getExpr();
    }

    public void setName(SQLName name) {
        this.setTableSource(new SQLExprTableSource(name));
    }

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSource = tableSource;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        GLOBAL_TEMPORARY, LOCAL_TEMPORARY
    }

    public List<SQLTableElement> getTableElementList() {
        return tableElementList;
    }

    public boolean isIfNotExiists() {
        return ifNotExiists;
    }

    public void setIfNotExiists(boolean ifNotExiists) {
        this.ifNotExiists = ifNotExiists;
    }

    public SQLExprTableSource getInherits() {
        return inherits;
    }

    public void setInherits(SQLExprTableSource inherits) {
        if (inherits != null) {
            inherits.setParent(this);
        }
        this.inherits = inherits;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSource);
            this.acceptChild(visitor, tableElementList);
            this.acceptChild(visitor, inherits);
        }
        visitor.endVisit(this);
    }
}
