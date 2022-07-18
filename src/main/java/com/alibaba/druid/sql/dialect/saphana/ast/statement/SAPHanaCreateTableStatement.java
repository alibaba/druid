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
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.saphana.visitor.SAPHanaASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * @author nukiyoam
 */
public class SAPHanaCreateTableStatement extends SQLCreateTableStatement implements SAPHanaStatement {

    public SAPHanaCreateTableStatement() {
        super(DbType.sap_hana);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof SAPHanaASTVisitor) {
            accept0((SAPHanaASTVisitor)visitor);
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

            for (final SQLTableElement element : tableElementList) {
                if (element != null) {
                    element.accept(visitor);
                }
            }

            if (like != null) {
                like.accept(visitor);
            }

            if (select != null) {
                select.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    @Override
    public void simplify() {
        tableOptions.clear();
        tblProperties.clear();
        super.simplify();
    }

    public void cloneTo(SAPHanaCreateTableStatement x) {
        super.cloneTo(x);
        if (partitioning != null) {
            x.setPartitioning(partitioning.clone());
        }
        if (like != null) {
            x.setLike(like.clone());
        }
    }

    @Override
    public SAPHanaCreateTableStatement clone() {
        SAPHanaCreateTableStatement x = new SAPHanaCreateTableStatement();
        cloneTo(x);
        return x;
    }

}
