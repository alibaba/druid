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
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlCreateTableSpaceStatement extends MySqlStatementImpl implements SQLCreateStatement {
    private SQLName name;
    private SQLExpr addDataFile;
    private SQLExpr initialSize;
    private SQLExpr extentSize;
    private SQLExpr autoExtentSize;
    private SQLExpr fileBlockSize;
    private SQLExpr logFileGroup;
    private SQLExpr maxSize;
    private SQLExpr nodeGroup;
    private boolean wait;
    private SQLExpr comment;
    private SQLExpr engine;

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, addDataFile);
            acceptChild(visitor, initialSize);
            acceptChild(visitor, extentSize);
            acceptChild(visitor, autoExtentSize);
            acceptChild(visitor, fileBlockSize);
            acceptChild(visitor, logFileGroup);
            acceptChild(visitor, maxSize);
            acceptChild(visitor, nodeGroup);
            acceptChild(visitor, comment);
            acceptChild(visitor, engine);
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

    public SQLExpr getAddDataFile() {
        return addDataFile;
    }

    public void setAddDataFile(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.addDataFile = addDataFile;
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

    public SQLExpr getFileBlockSize() {
        return fileBlockSize;
    }

    public void setFileBlockSize(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.fileBlockSize = x;
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

    public void setEngine(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.engine = engine;
    }

    public SQLExpr getLogFileGroup() {
        return logFileGroup;
    }

    public void setLogFileGroup(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.logFileGroup = x;
    }

    public SQLExpr getExtentSize() {
        return extentSize;
    }

    public void setExtentSize(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.extentSize = x;
    }

    public SQLExpr getAutoExtentSize() {
        return autoExtentSize;
    }

    public void setAutoExtentSize(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.autoExtentSize = x;
    }

    public SQLExpr getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.maxSize = x;
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
}
