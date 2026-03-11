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
package com.alibaba.druid.sql.dialect.dm.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.dm.visitor.DMASTVisitor;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableItem;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class DMAlterTableOption extends OracleAlterTableItem {
    public enum OptionType {
        PARALLEL,
        NOPARALLEL,
        READ_ONLY,
        READ_WRITE,
        AUTO_INCREMENT,
        ENABLE_ALL_TRIGGERS,
        DISABLE_ALL_TRIGGERS
    }

    private OptionType optionType;
    private SQLExpr value;

    public DMAlterTableOption() {
    }

    public DMAlterTableOption(OptionType optionType) {
        this.optionType = optionType;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (!(visitor instanceof DMASTVisitor)) {
            return;
        }

        DMASTVisitor dmVisitor = (DMASTVisitor) visitor;
        if (dmVisitor.visit(this)) {
            acceptChild(visitor, value);
        }
        dmVisitor.endVisit(this);
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        if (value != null) {
            value.setParent(this);
        }
        this.value = value;
    }
}
