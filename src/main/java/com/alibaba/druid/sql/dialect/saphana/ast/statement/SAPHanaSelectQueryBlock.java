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

import java.util.Objects;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLWindow;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.saphana.ast.SAPHanaObject;
import com.alibaba.druid.sql.dialect.saphana.visitor.SAPHanaASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * @author nukiyoam
 */
public class SAPHanaSelectQueryBlock extends SQLSelectQueryBlock implements SAPHanaObject {

    public SAPHanaSelectQueryBlock() {
        dbType = DbType.sap_hana;
    }

    @Override
    public SAPHanaSelectQueryBlock clone() {
        SAPHanaSelectQueryBlock x = new SAPHanaSelectQueryBlock();
        cloneTo(x);
        return x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SAPHanaSelectQueryBlock that = (SAPHanaSelectQueryBlock)o;

        if (!Objects.equals(hints, that.hints)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (hints != null ? hints.hashCode() : 0);
        return result;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof SAPHanaASTVisitor) {
            accept0((SAPHanaASTVisitor)visitor);
            return;
        }

        super.accept0(visitor);
    }

    @Override
    public void accept0(SAPHanaASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (SQLSelectItem item : this.selectList) {
                if (item != null) {
                    item.accept(visitor);
                }
            }

            if (this.from != null) {
                this.from.accept(visitor);
            }

            if (this.into != null) {
                this.into.accept(visitor);
            }

            if (this.where != null) {
                this.where.accept(visitor);
            }

            if (this.startWith != null) {
                this.startWith.accept(visitor);
            }

            if (this.connectBy != null) {
                this.connectBy.accept(visitor);
            }

            if (this.groupBy != null) {
                this.groupBy.accept(visitor);
            }

            if (this.windows != null) {
                for (SQLWindow item : windows) {
                    item.accept(visitor);
                }
            }

            if (this.orderBy != null) {
                this.orderBy.accept(visitor);
            }

            if (this.distributeBy != null) {
                for (SQLSelectOrderByItem item : distributeBy) {
                    item.accept(visitor);
                }
            }

            if (this.sortBy != null) {
                for (SQLSelectOrderByItem item : sortBy) {
                    item.accept(visitor);
                }
            }

            if (this.waitTime != null) {
                this.waitTime.accept(visitor);
            }

            if (this.limit != null) {
                this.limit.accept(visitor);
            }

        }

        visitor.endVisit(this);
    }

    public String toString() {
        // FIXME: 2022/7/18 sap hana output visitor
        return SQLUtils.toMySqlString(this);
    }

}
