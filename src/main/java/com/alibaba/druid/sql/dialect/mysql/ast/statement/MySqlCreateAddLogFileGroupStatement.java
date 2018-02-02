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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlCreateAddLogFileGroupStatement extends MySqlStatementImpl implements SQLAlterStatement {
    private SQLName name;
    private SQLExpr addUndoFile;
    private SQLExpr initialSize;
    private SQLExpr undoBufferSize;
    private SQLExpr redoBufferSize;
    private SQLExpr nodeGroup;
    private boolean wait;
    private SQLExpr comment;
    private SQLExpr engine;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLExpr getAddUndoFile() {
        return addUndoFile;
    }

    public void setAddUndoFile(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.addUndoFile = x;
    }

    public SQLExpr getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.initialSize = x;
    }

    public SQLExpr getUndoBufferSize() {
        return undoBufferSize;
    }

    public void setUndoBufferSize(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.undoBufferSize = x;
    }

    public SQLExpr getRedoBufferSize() {
        return redoBufferSize;
    }

    public void setRedoBufferSize(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.redoBufferSize = x;
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public SQLExpr getEngine() {
        return engine;
    }

    public void setEngine(SQLExpr engine) {
        if (engine != null) {
            engine.setParent(this);
        }
        this.engine = engine;
    }

    public SQLExpr getNodeGroup() {
        return nodeGroup;
    }

    public void setNodeGroup(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.nodeGroup = x;
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.comment = x;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, addUndoFile);
            acceptChild(visitor, initialSize);
            acceptChild(visitor, undoBufferSize);
            acceptChild(visitor, redoBufferSize);
            acceptChild(visitor, nodeGroup);
            acceptChild(visitor, comment);
            acceptChild(visitor, engine);
        }
        visitor.endVisit(this);
    }
}
