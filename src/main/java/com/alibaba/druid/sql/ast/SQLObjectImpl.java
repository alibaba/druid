/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public abstract class SQLObjectImpl implements SQLObject, Serializable {

    private static final long   serialVersionUID = 5569722716326763762L;

    private SQLObject           parent;

    private Map<String, Object> attributes;

    public SQLObjectImpl(){
    }

    public final void accept(SQLASTVisitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException();
        }

        visitor.preVisit(this);

        accept0(visitor);

        visitor.postVisit(this);
    }

    protected abstract void accept0(SQLASTVisitor visitor);

    protected final void acceptChild(SQLASTVisitor visitor, List<? extends SQLObject> children) {
        if (children == null) {
            return;
        }
        
        for (SQLObject child : children) {
            acceptChild(visitor, child);
        }
    }

    protected final void acceptChild(SQLASTVisitor visitor, SQLObject child) {
        if (child == null) {
            return;
        }

        child.accept(visitor);
    }

    public void output(StringBuffer buf) {
        buf.append(super.toString());
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        output(buf);
        return buf.toString();
    }

    public SQLObject getParent() {
        return parent;
    }

    public void setParent(SQLObject parent) {
        this.parent = parent;
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, Object>(1);
        }

        return attributes;
    }

    public Object getAttribute(String name) {
        if (attributes == null) {
            return null;
        }

        return attributes.get(name);
    }

    public void putAttribute(String name, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>(1);
        }

        attributes.put(name, value);
    }

    public Map<String, Object> getAttributesDirect() {
        return attributes;
    }
}
