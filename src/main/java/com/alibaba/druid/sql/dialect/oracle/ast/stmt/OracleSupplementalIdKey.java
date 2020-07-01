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

import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

/**
 * Created by wenshao on 20/05/2017.
 */
public class OracleSupplementalIdKey extends OracleSQLObjectImpl implements SQLTableElement {

    private boolean all;
    private boolean primaryKey;
    private boolean unique;
    private boolean uniqueIndex;
    private boolean foreignKey;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    public boolean isUniqueIndex() {
        return uniqueIndex;
    }

    public void setUniqueIndex(boolean uniqueIndex) {
        this.uniqueIndex = uniqueIndex;
    }

    public OracleSupplementalIdKey clone() {
        OracleSupplementalIdKey x = new OracleSupplementalIdKey();
        x.all = all;
        x.primaryKey = primaryKey;
        x.unique = unique;
        x.uniqueIndex = uniqueIndex;
        x.foreignKey = foreignKey;
        return x;
    }
}
