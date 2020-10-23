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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleStatementImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLAlterIndexStatement extends SQLStatementImpl implements SQLAlterStatement {
    private SQLName name;
    private SQLName renameTo;
    private SQLExprTableSource table;
    private boolean compile;
    private Boolean enable;
    protected boolean unusable;
    private Boolean monitoringUsage;
    private Rebuild rebuild;
    private SQLExpr parallel;
    private List<SQLAssignItem> partitions = new ArrayList<SQLAssignItem>();
    protected SQLPartitionBy    dbPartitionBy;

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, renameTo);
            acceptChild(visitor, table);
            acceptChild(visitor, partitions);
            acceptChild(visitor, rebuild);
            acceptChild(visitor, parallel);
        }
        visitor.endVisit(this);
    }

    public SQLName getRenameTo() {
        return renameTo;
    }

    public void setRenameTo(SQLName renameTo) {
        this.renameTo = renameTo;
    }

    public SQLExpr getParallel() {
        return parallel;
    }

    public void setParallel(SQLExpr parallel) {
        this.parallel = parallel;
    }

    public Boolean getMonitoringUsage() {
        return monitoringUsage;
    }

    public void setMonitoringUsage(Boolean monitoringUsage) {
        this.monitoringUsage = monitoringUsage;
    }

    public Rebuild getRebuild() {
        return rebuild;
    }

    public void setRebuild(Rebuild rebuild) {
        this.rebuild = rebuild;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public boolean isCompile() {
        return compile;
    }

    public void setCompile(boolean compile) {
        this.compile = compile;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public boolean isUnusable() {
        return unusable;
    }

    public void setUnusable(boolean unusable) {
        this.unusable = unusable;
    }

    public static class Rebuild extends SQLObjectImpl {

        private SQLObject option;

        public SQLObject getOption() {
            return option;
        }

        public void setOption(SQLObject option) {
            this.option = option;
        }

        @Override
        public void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, option);
            }
            visitor.endVisit(this);
        }

    }

    public SQLExprTableSource getTable() {
        return table;
    }

    public void setTable(SQLName x) {
        setTable(new SQLExprTableSource(x));
    }

    public void setTable(SQLExprTableSource x) {
        if (x != null) {
            x.setParent(this);
        }
        this.table = x;
    }

    public List<SQLAssignItem> getPartitions() {
        return partitions;
    }

    public SQLPartitionBy getDbPartitionBy() {
        return dbPartitionBy;
    }

    public void setDbPartitionBy(SQLPartitionBy x) {
        if (x != null) {
            x.setParent(this);
        }
        this.dbPartitionBy = x;
    }
}
