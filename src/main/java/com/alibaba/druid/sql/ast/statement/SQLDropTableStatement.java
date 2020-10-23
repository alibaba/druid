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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLDropTableStatement extends SQLStatementImpl implements SQLDropStatement {
    private List<SQLCommentHint> hints;

    protected List<SQLExprTableSource> tableSources = new ArrayList<SQLExprTableSource>();
    protected boolean                  purge;
    protected boolean                  cascade      = false;
    protected boolean                  restrict     = false;
    protected boolean                  ifExists     = false;
    private boolean                    temporary    = false;
    private boolean                    external     = false;
    private boolean                    isDropPartition;
    private SQLExpr                    where;

    public SQLDropTableStatement(){

    }
    
    public SQLDropTableStatement(DbType dbType){
        super (dbType);
    }

    public SQLDropTableStatement(SQLName name, DbType dbType){
        this(new SQLExprTableSource(name), dbType);
    }
    
    public SQLDropTableStatement(SQLName name){
        this (name, null);
    }
    
    public SQLDropTableStatement(SQLExprTableSource tableSource){
        this (tableSource, null);
    }

    public SQLDropTableStatement(SQLExprTableSource tableSource, DbType dbType){
        this (dbType);
        this.tableSources.add(tableSource);
    }

    public List<SQLExprTableSource> getTableSources() {
        return tableSources;
    }
    
    public void addPartition(SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSources.add(tableSource);
    }

    public void setName(SQLName name) {
        this.addTableSource(new SQLExprTableSource(name));
    }

    public void addTableSource(SQLName name) {
        this.addTableSource(new SQLExprTableSource(name));
    }

    public void addTableSource(SQLExprTableSource tableSource) {
        tableSources.add(tableSource);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSources);
        }
        visitor.endVisit(this);
    }

    @Override
    public List getChildren() {
        return this.tableSources;
    }

    public boolean isPurge() {
        return purge;
    }

    public void setPurge(boolean purge) {
        this.purge = purge;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    public boolean isCascade() {
        return cascade;
    }

    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    public boolean isRestrict() {
        return restrict;
    }

    public void setRestrict(boolean restrict) {
        this.restrict = restrict;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public boolean isDropPartition() {
        return isDropPartition;
    }

    public void setDropPartition(boolean dropPartition) {
        isDropPartition = dropPartition;
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.where = x;
    }
}
