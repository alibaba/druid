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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleAlterIndexStatement extends OracleStatementImpl implements OracleAlterStatement {

    private SQLName name;

    private SQLName renameTo;

    private boolean compile;

    private Boolean enable;

    private Boolean monitoringUsage;

    private Rebuild rebuild;

    private SQLExpr parallel;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, renameTo);
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

    public static class Rebuild extends OracleSQLObjectImpl {

        private SQLObject option;

        public SQLObject getOption() {
            return option;
        }

        public void setOption(SQLObject option) {
            this.option = option;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, option);
            }
            visitor.endVisit(this);
        }

    }
}
