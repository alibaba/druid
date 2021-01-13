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
package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.ast.statement.SQLObjectType;
import com.alibaba.druid.sql.ast.statement.SQLPrivilegeItem;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OdpsGrantStmt extends SQLGrantStatement {

    private SQLObjectType subjectType;

    private boolean       isSuper = false;

    private boolean       isLabel = false;
    private SQLExpr       label;
    private List<SQLName> columns = new ArrayList<SQLName>(); ;
    private SQLExpr       expire;

    public OdpsGrantStmt(){
        super(DbType.odps);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OdpsASTVisitor) visitor);
    }

    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, resource);
            acceptChild(visitor, users);
        }
        visitor.endVisit(this);
    }

    public SQLObjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SQLObjectType subjectType) {
        this.subjectType = subjectType;
    }

    public boolean isSuper() {
        return isSuper;
    }

    public void setSuper(boolean isSuper) {
        this.isSuper = isSuper;
    }

    public boolean isLabel() {
        return isLabel;
    }

    public void setLabel(boolean isLabel) {
        this.isLabel = isLabel;
    }

    public SQLExpr getLabel() {
        return label;
    }

    public void setLabel(SQLExpr label) {
        this.label = label;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public void setColumnList(List<SQLName> columns) {
        this.columns = columns;
    }

    public SQLExpr getExpire() {
        return expire;
    }

    public void setExpire(SQLExpr expire) {
        this.expire = expire;
    }

}
