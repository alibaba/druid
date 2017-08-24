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

import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLDataTypeImpl extends SQLObjectImpl implements SQLDataType {

    protected String              name;
    protected final List<SQLExpr> arguments = new ArrayList<SQLExpr>();
    private Boolean withTimeZone;

    private boolean withLocalTimeZone = false;

    public SQLDataTypeImpl(){

    }

    public SQLDataTypeImpl(String name){

        this.name = name;
    }

    public SQLDataTypeImpl(String name, int precision) {
        this(name);
        addArgument(new SQLIntegerExpr(precision));
    }

    public SQLDataTypeImpl(String name, int precision, int scale) {
        this(name);
        addArgument(new SQLIntegerExpr(precision));
        addArgument(new SQLIntegerExpr(scale));
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.arguments);
        }

        visitor.endVisit(this);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SQLExpr> getArguments() {
        return this.arguments;
    }
    
    public void addArgument(SQLExpr argument) {
        if (argument != null) {
            argument.setParent(this);
        }
        this.arguments.add(argument);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLDataTypeImpl dataType = (SQLDataTypeImpl) o;

        if (name != null ? !name.equals(dataType.name) : dataType.name != null) return false;
        if (arguments != null ? !arguments.equals(dataType.arguments) : dataType.arguments != null) return false;
        return withTimeZone != null ? withTimeZone.equals(dataType.withTimeZone) : dataType.withTimeZone == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        result = 31 * result + (withTimeZone != null ? withTimeZone.hashCode() : 0);
        return result;
    }

    @Override
    public Boolean getWithTimeZone() {
        return withTimeZone;
    }

    public void setWithTimeZone(Boolean withTimeZone) {
        this.withTimeZone = withTimeZone;
    }

    public boolean isWithLocalTimeZone() {
        return withLocalTimeZone;
    }

    public void setWithLocalTimeZone(boolean withLocalTimeZone) {
        this.withLocalTimeZone = withLocalTimeZone;
    }

    public SQLDataTypeImpl clone() {
        SQLDataTypeImpl x = new SQLDataTypeImpl();

        cloneTo(x);

        return x;
    }

    public void cloneTo(SQLDataTypeImpl x) {
        x.name = name;

        for (SQLExpr arg : arguments) {
            x.addArgument(arg.clone());
        }

        x.withTimeZone = withTimeZone;
        x.withLocalTimeZone = withLocalTimeZone;
    }
}
