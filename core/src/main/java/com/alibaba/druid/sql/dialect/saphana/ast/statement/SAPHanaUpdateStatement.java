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
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.saphana.visitor.SAPHanaASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * @author nukiyoam
 */
public class SAPHanaUpdateStatement extends SQLUpdateStatement implements SAPHanaStatement {
    public SAPHanaUpdateStatement() {
        super(DbType.sap_hana);
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

            if (from != null) {
                from.accept(visitor);
            }

            for (SQLUpdateSetItem item : items) {
                if (item != null) {
                    item.accept(visitor);
                }
            }

            if (where != null) {
                where.accept(visitor);
            }

            if (orderBy != null) {
                orderBy.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
