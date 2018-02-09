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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.util.Collections;
import java.util.List;

public class SQLMapDataType extends SQLObjectImpl implements SQLDataType {
    private String dbType;
    private SQLDataType keyType;
    private SQLDataType valueType;

    public SQLMapDataType() {

    }

    public SQLMapDataType(SQLDataType keyType, SQLDataType valueType) {
        this.setKeyType(keyType);
        this.setValueType(valueType);
    }

    public SQLMapDataType(SQLDataType keyType, SQLDataType valueType, String dbType) {
        this.setKeyType(keyType);
        this.setValueType(valueType);
        this.dbType = dbType;
    }

    @Override
    public String getName() {
        return "MAP";
    }

    @Override
    public long nameHashCode64() {
        return FnvHash.Constants.MAP;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SQLExpr> getArguments() {
        return Collections.emptyList();
    }

    @Override
    public Boolean getWithTimeZone() {
        return null;
    }

    @Override
    public void setWithTimeZone(Boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWithLocalTimeZone() {
        return false;
    }

    @Override
    public void setWithLocalTimeZone(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDbType(String dbType) {
        dbType = dbType;
    }

    @Override
    public String getDbType() {
        return dbType;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, keyType);
            acceptChild(visitor, valueType);
        }
        visitor.endVisit(this);
    }

    public SQLMapDataType clone() {
        SQLMapDataType x = new SQLMapDataType();
        x.dbType = dbType;

        if (keyType != null) {
            x.setKeyType(keyType.clone());
        }

        if (valueType != null) {
            x.setValueType(valueType.clone());
        }

        return x;
    }

    public SQLDataType getKeyType() {
        return keyType;
    }

    public void setKeyType(SQLDataType x) {
        if (x != null) {
            x.setParent(this);
        }
        this.keyType = x;
    }

    public SQLDataType getValueType() {
        return valueType;
    }

    public void setValueType(SQLDataType x) {
        if (x != null) {
            x.setParent(this);
        }
        this.valueType = x;
    }
}
