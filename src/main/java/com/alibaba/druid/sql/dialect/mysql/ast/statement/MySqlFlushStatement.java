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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/08/2017.
 */
public class MySqlFlushStatement extends MySqlStatementImpl {
    private boolean noWriteToBinlog = false;
    private boolean local = false;

    private final List<SQLExprTableSource> tables = new ArrayList<SQLExprTableSource>();

    private boolean withReadLock = false;
    private boolean forExport;

    private boolean binaryLogs;
    private boolean desKeyFile;
    private boolean engineLogs;
    private boolean errorLogs;
    private boolean generalLogs;
    private boolean hots;
    private boolean logs;
    private boolean privileges;
    private boolean optimizerCosts;
    private boolean queryCache;
    private boolean relayLogs;
    private SQLExpr relayLogsForChannel;
    private boolean slowLogs;
    private boolean status;
    private boolean userResources;
    private boolean tableOption;

    public boolean isNoWriteToBinlog() {
        return noWriteToBinlog;
    }

    public void setNoWriteToBinlog(boolean noWriteToBinlog) {
        this.noWriteToBinlog = noWriteToBinlog;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public List<SQLExprTableSource> getTables() {
        return tables;
    }

    public boolean isWithReadLock() {
        return withReadLock;
    }

    public void setWithReadLock(boolean withReadLock) {
        this.withReadLock = withReadLock;
    }

    public boolean isForExport() {
        return forExport;
    }

    public void setForExport(boolean forExport) {
        this.forExport = forExport;
    }

    public boolean isBinaryLogs() {
        return binaryLogs;
    }

    public void setBinaryLogs(boolean binaryLogs) {
        this.binaryLogs = binaryLogs;
    }

    public boolean isDesKeyFile() {
        return desKeyFile;
    }

    public void setDesKeyFile(boolean desKeyFile) {
        this.desKeyFile = desKeyFile;
    }

    public boolean isEngineLogs() {
        return engineLogs;
    }

    public void setEngineLogs(boolean engineLogs) {
        this.engineLogs = engineLogs;
    }

    public boolean isGeneralLogs() {
        return generalLogs;
    }

    public void setGeneralLogs(boolean generalLogs) {
        this.generalLogs = generalLogs;
    }

    public boolean isHots() {
        return hots;
    }

    public void setHots(boolean hots) {
        this.hots = hots;
    }

    public boolean isLogs() {
        return logs;
    }

    public void setLogs(boolean logs) {
        this.logs = logs;
    }

    public boolean isPrivileges() {
        return privileges;
    }

    public void setPrivileges(boolean privileges) {
        this.privileges = privileges;
    }

    public boolean isOptimizerCosts() {
        return optimizerCosts;
    }

    public void setOptimizerCosts(boolean optimizerCosts) {
        this.optimizerCosts = optimizerCosts;
    }

    public boolean isQueryCache() {
        return queryCache;
    }

    public void setQueryCache(boolean queryCache) {
        this.queryCache = queryCache;
    }

    public boolean isRelayLogs() {
        return relayLogs;
    }

    public void setRelayLogs(boolean relayLogs) {
        this.relayLogs = relayLogs;
    }

    public SQLExpr getRelayLogsForChannel() {
        return relayLogsForChannel;
    }

    public void setRelayLogsForChannel(SQLExpr relayLogsForChannel) {
        this.relayLogsForChannel = relayLogsForChannel;
    }

    public boolean isSlowLogs() {
        return slowLogs;
    }

    public void setSlowLogs(boolean showLogs) {
        this.slowLogs = showLogs;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isUserResources() {
        return userResources;
    }

    public void setUserResources(boolean userResources) {
        this.userResources = userResources;
    }

    public boolean isErrorLogs() {
        return errorLogs;
    }

    public void setErrorLogs(boolean errorLogs) {
        this.errorLogs = errorLogs;
    }

    public boolean isTableOption() {
        return tableOption;
    }

    public void setTableOption(boolean tableOption) {
        this.tableOption = tableOption;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tables);
            acceptChild(visitor, relayLogsForChannel);
        }
        visitor.endVisit(this);
    }

    public void addTable(SQLName name) {
        if (name == null) {
            return;
        }
        this.addTable(new SQLExprTableSource(name));
    }

    public void addTable(SQLExprTableSource table) {
        if (table == null) {
            return;
        }
        table.setParent(this);
        this.tables.add(table);
    }
}
