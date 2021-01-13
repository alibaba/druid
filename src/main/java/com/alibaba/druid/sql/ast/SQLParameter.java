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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public final class SQLParameter extends SQLObjectImpl implements SQLObjectWithDataType {
    private SQLName                  name;
    private SQLDataType              dataType;
    private SQLExpr                  defaultValue;
    private ParameterType            paramType;
    private boolean                  noCopy = false;
    private boolean                  constant = false;
    private SQLName                  cursorName;
    private final List<SQLParameter> cursorParameters = new ArrayList<SQLParameter>();
    private boolean                  order;
    private boolean                  map;
    private boolean                  member;

    public SQLExpr getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(SQLExpr deaultValue) {
        if (deaultValue != null) {
            deaultValue.setParent(this);
        }
        this.defaultValue = deaultValue;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLDataType getDataType() {
        return dataType;
    }

    public void setDataType(SQLDataType dataType) {
        if (dataType != null) {
            dataType.setParent(this);
        }
        this.dataType = dataType;
    }
    
    public ParameterType getParamType() {
        return paramType;
    }

    public void setParamType(ParameterType paramType) {
        this.paramType = paramType;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, dataType);
            acceptChild(visitor, defaultValue);
        }
        visitor.endVisit(this);
    }
    
    public static enum ParameterType {
        DEFAULT, //
        IN, // in
        OUT, // out
        INOUT// inout
    }

    public boolean isNoCopy() {
        return noCopy;
    }

    public void setNoCopy(boolean noCopy) {
        this.noCopy = noCopy;
    }

    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    public List<SQLParameter> getCursorParameters() {
        return cursorParameters;
    }

    public SQLName getCursorName() {
        return cursorName;
    }

    public void setCursorName(SQLName cursorName) {
        if (cursorName != null) {
            cursorName.setParent(this);
        }
        this.cursorName = cursorName;
    }

    public SQLParameter clone() {
        SQLParameter x = new SQLParameter();
        if (name != null) {
            x.setName(name.clone());
        }
        if (dataType != null) {
            x.setDataType(dataType.clone());
        }
        if (defaultValue != null) {
            x.setDefaultValue(defaultValue.clone());
        }
        x.paramType = paramType;
        x.noCopy = noCopy;
        x.constant = constant;
        x.order = order;
        x.map = map;
        if (cursorName != null) {
            x.setCursorName(cursorName.clone());
        }
        for (SQLParameter p : cursorParameters) {
            SQLParameter p2 = p.clone();
            p2.setParent(x);
            x.cursorParameters.add(p2);
        }
        return x;
    }

    public boolean isOrder() {
        return order;
    }

    public void setOrder(boolean order) {
        this.order = order;
    }

    public boolean isMap() {
        return map;
    }

    public void setMap(boolean map) {
        this.map = map;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }
}
