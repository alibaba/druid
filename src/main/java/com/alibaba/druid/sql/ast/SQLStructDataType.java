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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SQLStructDataType extends SQLObjectImpl implements SQLDataType {
    private String dbType;
    private List<Field> fields = new ArrayList<Field>();

    public SQLStructDataType() {

    }

    public SQLStructDataType(String dbType) {
        this.dbType = dbType;
    }

    @Override
    public String getName() {
        return "STRUCT";
    }

    @Override
    public long nameHashCode64() {
        return FnvHash.Constants.STRUCT;
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
            acceptChild(visitor, fields);
        }
        visitor.endVisit(this);
    }

    public SQLStructDataType clone() {
        SQLStructDataType x = new SQLStructDataType(dbType);

        for (Field field : fields) {
            x.addField(field.name, field.dataType.clone());
        }

        return x;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void addField(SQLName name, SQLDataType dataType) {
        Field field = new Field(name, dataType);
        field.setParent(this);
        fields.add(field);
    }

    public static class Field extends SQLObjectImpl {
        private SQLName name;
        private SQLDataType dataType;

        public Field(SQLName name, SQLDataType dataType) {
            setName(name);
            setDataType(dataType);
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, name);
                acceptChild(visitor, dataType);
            }
            visitor.endVisit(this);
        }

        public SQLName getName() {
            return name;
        }

        public void setName(SQLName x) {
            if (x != null) {
                x.setParent(this);
            }
            this.name = x;
        }

        public SQLDataType getDataType() {
            return dataType;
        }

        public void setDataType(SQLDataType x) {
            if (x != null) {
                x.setParent(this);
            }
            this.dataType = x;
        }
    }
}
