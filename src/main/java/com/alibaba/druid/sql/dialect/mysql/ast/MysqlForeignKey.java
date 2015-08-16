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
package com.alibaba.druid.sql.dialect.mysql.ast;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * @author kiki
 */
public class MysqlForeignKey extends SQLForeignKeyImpl {

    private SQLName indexName;

    private boolean hasConstraint;

    private Match   referenceMatch;

    private On      referenceOn;

    private Option  referenceOption;

    public SQLName getIndexName() {
        return indexName;
    }

    public void setIndexName(SQLName indexName) {
        this.indexName = indexName;
    }

    public boolean isHasConstraint() {
        return hasConstraint;
    }

    public void setHasConstraint(boolean hasConstraint) {
        this.hasConstraint = hasConstraint;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        }
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getName());
            acceptChild(visitor, this.getReferencedTableName());
            acceptChild(visitor, this.getReferencingColumns());
            acceptChild(visitor, this.getReferencedColumns());

            acceptChild(visitor, indexName);
        }
        visitor.endVisit(this);
    }

    public Match getReferenceMatch() {
        return referenceMatch;
    }

    public void setReferenceMatch(Match referenceMatch) {
        this.referenceMatch = referenceMatch;
    }

    public On getReferenceOn() {
        return referenceOn;
    }

    public void setReferenceOn(On referenceOn) {
        this.referenceOn = referenceOn;
    }

    public Option getReferenceOption() {
        return referenceOption;
    }

    public void setReferenceOption(Option referenceOption) {
        this.referenceOption = referenceOption;
    }

    public static enum Option {

        RESTRICT("RESTRICT"), CASCADE("CASCADE"), SET_NULL("SET NULL"), NO_ACTION("NO ACTION");

        private String text;

        Option(String text){
            this.text = text;
        }

        public String getText() {
            return text;
        }

    }

    public static enum Match {
        FULL, PARTIAL, SIMPLE;
    }

    public static enum On {
        DELETE, UPDATE;
    }
}
