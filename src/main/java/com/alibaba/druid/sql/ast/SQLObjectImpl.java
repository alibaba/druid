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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObject;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGSQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SQLObjectImpl implements SQLObject {

    protected SQLObject           parent;
    protected Map<String, Object> attributes;
    protected SQLCommentHint      hint;

    protected int sourceLine;
    protected int sourceColumn;

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

    protected abstract void accept0(SQLASTVisitor v);

    protected final void acceptChild(SQLASTVisitor visitor, List<? extends SQLObject> children) {
        if (children == null) {
            return;
        }

        for (int i = 0; i < children.size(); i++) {
            acceptChild(visitor, children.get(i));
        }
    }

    protected final void acceptChild(SQLASTVisitor visitor, SQLObject child) {
        if (child == null) {
            return;
        }

        child.accept(visitor);
    }

    public void output(StringBuffer buf) {
        output((Appendable) buf);
    }

    public void output(Appendable buf) {
        DbType dbType = null;
        if (this instanceof OracleSQLObject) {
            dbType = DbType.oracle;
        } else if (this instanceof MySqlObject) {
            dbType = DbType.mysql;
        } else if (this instanceof PGSQLObject) {
            dbType = DbType.postgresql;
        } else if (this instanceof SQLDbTypedObject) {
            dbType = ((SQLDbTypedObject) this).getDbType();
        }

        accept(
                SQLUtils.createOutputVisitor(buf, dbType)
        );
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

    public boolean containsAttribute(String name) {
        if (attributes == null) {
            return false;
        }

        return attributes.containsKey(name);
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
    
    @SuppressWarnings("unchecked")
    public void addBeforeComment(String comment) {
        if (comment == null) {
            return;
        }
        
        if (attributes == null) {
            attributes = new HashMap<String, Object>(1);
        }
        
        List<String> comments = (List<String>) attributes.get("rowFormat.before_comment");
        if (comments == null) {
            comments = new ArrayList<String>(2);
            attributes.put("rowFormat.before_comment", comments);
        }
        
        comments.add(comment);
    }
    
    @SuppressWarnings("unchecked")
    public void addBeforeComment(List<String> comments) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>(1);
        }
        
        List<String> attrComments = (List<String>) attributes.get("rowFormat.before_comment");
        if (attrComments == null) {
            attributes.put("rowFormat.before_comment", comments);
        } else {
            attrComments.addAll(comments);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getBeforeCommentsDirect() {
        if (attributes == null) {
            return null;
        }
        
        return (List<String>) attributes.get("rowFormat.before_comment");
    }
    
    @SuppressWarnings("unchecked")
    public void addAfterComment(String comment) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>(1);
        }
        
        List<String> comments = (List<String>) attributes.get("rowFormat.after_comment");
        if (comments == null) {
            comments = new ArrayList<String>(2);
            attributes.put("rowFormat.after_comment", comments);
        }
        
        comments.add(comment);
    }
    
    @SuppressWarnings("unchecked")
    public void addAfterComment(List<String> comments) {
        if (comments == null) {
            return;
        }

        if (attributes == null) {
            attributes = new HashMap<String, Object>(1);
        }
        
        List<String> attrComments = (List<String>) attributes.get("rowFormat.after_comment");
        if (attrComments == null) {
            attributes.put("rowFormat.after_comment", comments);
        } else {
            attrComments.addAll(comments);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getAfterCommentsDirect() {
        if (attributes == null) {
            return null;
        }
        
        return (List<String>) attributes.get("rowFormat.after_comment");
    }
    
    public boolean hasBeforeComment() {
        if (attributes == null) {
            return false;
        }

        List<String> comments = (List<String>) attributes.get("rowFormat.before_comment");

        if (comments == null) {
            return false;
        }
        
        return !comments.isEmpty();
    }
    
    public boolean hasAfterComment() {
        if (attributes == null) {
            return false;
        }

        List<String> comments = (List<String>) attributes.get("rowFormat.after_comment");
        if (comments == null) {
            return false;
        }
        
        return !comments.isEmpty();
    }

    public SQLObject clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public SQLDataType computeDataType() {
        return null;
    }

    public int getSourceLine() {
        return sourceLine;
    }

    public void setSourceLine(int sourceLine) {
        this.sourceLine = sourceLine;
    }

    public int getSourceColumn() {
        return sourceColumn;
    }

    public void setSourceColumn(int sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public SQLCommentHint getHint() {
        return hint;
    }

    public void setHint(SQLCommentHint hint) {
        this.hint = hint;
    }
}
