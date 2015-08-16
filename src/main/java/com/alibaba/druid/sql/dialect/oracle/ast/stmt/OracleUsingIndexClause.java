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
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleUsingIndexClause extends OracleSQLObjectImpl {

    private SQLName             index;
    private SQLName             tablespace;

    private SQLExpr             ptcfree;
    private SQLExpr             pctused;
    private SQLExpr             initrans;
    private SQLExpr             maxtrans;

    private Boolean             enable            = null;

    private boolean             computeStatistics = false;

    private OracleStorageClause storage;

    public OracleUsingIndexClause(){

    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, index);
            acceptChild(visitor, tablespace);
            acceptChild(visitor, storage);
        }
        visitor.endVisit(this);
    }

    public OracleStorageClause getStorage() {
        return storage;
    }

    public void setStorage(OracleStorageClause storage) {
        this.storage = storage;
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

    public SQLName getIndex() {
        return index;
    }

    public void setIndex(SQLName index) {
        this.index = index;
    }

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName tablespace) {
        this.tablespace = tablespace;
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

}
