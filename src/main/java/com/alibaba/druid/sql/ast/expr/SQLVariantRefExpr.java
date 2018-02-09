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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLVariantRefExpr extends SQLExprImpl {

    private String  name;

    private boolean global = false;

    private boolean session = false;

    private int     index  = -1;

    public SQLVariantRefExpr(String name){
        this.name = name;
    }

    public SQLVariantRefExpr(String name, boolean global){
        this.name = name;
        this.global = global;
    }

    public SQLVariantRefExpr(String name, boolean global,boolean session){
        this.name = name;
        this.global = global;
        this.session = session;
    }

    public SQLVariantRefExpr(){

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void output(StringBuffer buf) {
        buf.append(this.name);
    }


    public boolean isSession() {
        return session;
    }

    public void setSession(boolean session) {
        this.session = session;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SQLVariantRefExpr)) {
            return false;
        }
        SQLVariantRefExpr other = (SQLVariantRefExpr) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public SQLVariantRefExpr clone() {
        SQLVariantRefExpr var =  new SQLVariantRefExpr(name, global);
        var.index = index;

        if (attributes != null) {
            var.attributes = new HashMap<String, Object>(attributes.size());
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();

                if (v instanceof SQLObject) {
                    var.attributes.put(k, ((SQLObject) v).clone());
                } else {
                    var.attributes.put(k, v);
                }
            }
        }

        return var;
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }
}
