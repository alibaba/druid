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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLTruncateStatement extends SQLStatementImpl {

    protected List<SQLExprTableSource> tableSources               = new ArrayList<SQLExprTableSource>(2);

    private boolean                    purgeSnapshotLog           = false;

    private boolean                    only;
    private Boolean                    restartIdentity;
    private Boolean                    cascade;

    // db2
    private boolean                    dropStorage                = false;
    private boolean                    reuseStorage               = false;
    private boolean                    immediate                  = false;
    private boolean                    ignoreDeleteTriggers       = false;
    private boolean                    restrictWhenDeleteTriggers = false;
    private boolean                    continueIdentity           = false;

    public SQLTruncateStatement(){

    }

    public SQLTruncateStatement(String dbType){
        super(dbType);
    }

    public List<SQLExprTableSource> getTableSources() {
        return tableSources;
    }

    public void setTableSources(List<SQLExprTableSource> tableSources) {
        this.tableSources = tableSources;
    }

    public void addTableSource(SQLName name) {
        SQLExprTableSource tableSource = new SQLExprTableSource(name);
        tableSource.setParent(this);
        this.tableSources.add(tableSource);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSources);
        }
        visitor.endVisit(this);
    }

    public boolean isPurgeSnapshotLog() {
        return purgeSnapshotLog;
    }

    public void setPurgeSnapshotLog(boolean purgeSnapshotLog) {
        this.purgeSnapshotLog = purgeSnapshotLog;
    }

    public boolean isOnly() {
        return only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public Boolean getRestartIdentity() {
        return restartIdentity;
    }

    public void setRestartIdentity(Boolean restartIdentity) {
        this.restartIdentity = restartIdentity;
    }

    public Boolean getCascade() {
        return cascade;
    }

    public void setCascade(Boolean cascade) {
        this.cascade = cascade;
    }

    public boolean isDropStorage() {
        return dropStorage;
    }

    public void setDropStorage(boolean dropStorage) {
        this.dropStorage = dropStorage;
    }

    public boolean isReuseStorage() {
        return reuseStorage;
    }

    public void setReuseStorage(boolean reuseStorage) {
        this.reuseStorage = reuseStorage;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public boolean isIgnoreDeleteTriggers() {
        return ignoreDeleteTriggers;
    }

    public void setIgnoreDeleteTriggers(boolean ignoreDeleteTriggers) {
        this.ignoreDeleteTriggers = ignoreDeleteTriggers;
    }

    public boolean isRestrictWhenDeleteTriggers() {
        return restrictWhenDeleteTriggers;
    }

    public void setRestrictWhenDeleteTriggers(boolean restrictWhenDeleteTriggers) {
        this.restrictWhenDeleteTriggers = restrictWhenDeleteTriggers;
    }

    public boolean isContinueIdentity() {
        return continueIdentity;
    }

    public void setContinueIdentity(boolean continueIdentity) {
        this.continueIdentity = continueIdentity;
    }

    @Override
    public List getChildren() {
        return tableSources;
    }
}
