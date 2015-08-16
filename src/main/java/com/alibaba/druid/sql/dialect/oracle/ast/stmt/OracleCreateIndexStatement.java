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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateIndexStatement extends SQLCreateIndexStatement implements OracleDDLStatement {

    private boolean online            = false;

    private boolean indexOnlyTopLevel = false;

    private boolean noParallel;

    private SQLExpr parallel;

    private SQLName tablespace;

    private SQLExpr ptcfree;
    private SQLExpr pctused;
    private SQLExpr initrans;
    private SQLExpr maxtrans;

    private Boolean enable            = null;

    private boolean computeStatistics = false;
    
    public OracleCreateIndexStatement() {
        super (JdbcConstants.ORACLE);
    }

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName tablespace) {
        this.tablespace = tablespace;
    }

    public SQLExpr getParallel() {
        return parallel;
    }

    public void setParallel(SQLExpr parallel) {
        this.parallel = parallel;
    }

    public boolean isNoParallel() {
        return noParallel;
    }

    public void setNoParallel(boolean noParallel) {
        this.noParallel = noParallel;
    }

    public boolean isIndexOnlyTopLevel() {
        return indexOnlyTopLevel;
    }

    public void setIndexOnlyTopLevel(boolean indexOnlyTopLevel) {
        this.indexOnlyTopLevel = indexOnlyTopLevel;
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getName());
            acceptChild(visitor, getTable());
            acceptChild(visitor, getItems());
            acceptChild(visitor, getTablespace());
            acceptChild(visitor, parallel);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getPtcfree() {
        return ptcfree;
    }

    public void setPtcfree(SQLExpr ptcfree) {
        this.ptcfree = ptcfree;
    }

    public SQLExpr getPctused() {
        return pctused;
    }

    public void setPctused(SQLExpr pctused) {
        this.pctused = pctused;
    }

    public SQLExpr getInitrans() {
        return initrans;
    }

    public void setInitrans(SQLExpr initrans) {
        this.initrans = initrans;
    }

    public SQLExpr getMaxtrans() {
        return maxtrans;
    }

    public void setMaxtrans(SQLExpr maxtrans) {
        this.maxtrans = maxtrans;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public boolean isComputeStatistics() {
        return computeStatistics;
    }

    public void setComputeStatistics(boolean computeStatistics) {
        this.computeStatistics = computeStatistics;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}
