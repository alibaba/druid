/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.alibaba.druid.sql.dialect.saphana.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.saphana.visitor.SAPHanaASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * @author nukiyoam
 */
public class SAPHanaInsertStatement extends SQLInsertStatement implements SAPHanaStatement {
    public SAPHanaInsertStatement() {
        dbType = DbType.sap_hana;
    }

    public void cloneTo(SAPHanaInsertStatement x) {
        super.cloneTo(x);
        x.overwrite = overwrite;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof SAPHanaASTVisitor) {
            accept0((SAPHanaASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    @Override
    public void accept0(SAPHanaASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (tableSource != null) {
                tableSource.accept(visitor);
            }

            for (SQLExpr column : columns) {
                if (column != null) {
                    column.accept(visitor);
                }
            }

            for (ValuesClause values : valuesList) {
                if (values != null) {
                    values.accept(visitor);
                }
            }

            if (query != null) {
                query.accept(visitor);
            }
        }

        visitor.endVisit(this);
    }

    @Override
    public SQLInsertStatement clone() {
        SAPHanaInsertStatement x = new SAPHanaInsertStatement();
        cloneTo(x);
        return x;
    }

    @Override
    public boolean isOverwrite() {
        return overwrite;
    }

    @Override
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
}
