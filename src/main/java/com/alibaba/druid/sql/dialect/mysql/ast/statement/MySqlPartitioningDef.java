/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlPartitioningDef extends MySqlObjectImpl {

    private SQLName name;

    private Values  values;

    private SQLExpr dataDirectory;
    private SQLExpr indexDirectory;
    private SQLName tableSpace;
    private SQLExpr maxRows;
    private SQLExpr minRows;
    private SQLExpr engine;
    private SQLExpr comment;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, values);
            acceptChild(visitor, dataDirectory);
            acceptChild(visitor, indexDirectory);
            acceptChild(visitor, tableSpace);
            acceptChild(visitor, maxRows);
            acceptChild(visitor, minRows);
            acceptChild(visitor, engine);
            acceptChild(visitor, comment);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getIndexDirectory() {
        return indexDirectory;
    }

    public void setIndexDirectory(SQLExpr indexDirectory) {
        if (indexDirectory != null) {
            indexDirectory.setParent(this);
        }
        this.indexDirectory = indexDirectory;
    }

    public SQLExpr getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(SQLExpr dataDirectory) {
        if (dataDirectory != null) {
            dataDirectory.setParent(this);
        }
        this.dataDirectory = dataDirectory;
    }

    public Values getValues() {
        return values;
    }

    public void setValues(Values values) {
        this.values = values;
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

    public SQLName getTableSpace() {
        return tableSpace;
    }

    public void setTableSpace(SQLName tableSpace) {
        if (tableSpace != null) {
            tableSpace.setParent(this);
        }
        this.tableSpace = tableSpace;
    }

    public SQLExpr getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(SQLExpr maxRows) {
        if (maxRows != null) {
            maxRows.setParent(this);
        }
        this.maxRows = maxRows;
    }

    public SQLExpr getMinRows() {
        return minRows;
    }

    public void setMinRows(SQLExpr minRows) {
        if (minRows != null) {
            minRows.setParent(this);
        }
        this.minRows = minRows;
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

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    public static abstract class Values extends MySqlObjectImpl {

        private final List<SQLExpr> items = new ArrayList<SQLExpr>();

        public List<SQLExpr> getItems() {
            return items;
        }
    }

    public static class LessThanValues extends Values {

        @Override
        public void accept0(MySqlASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, getItems());
            }
            visitor.endVisit(this);
        }

    }

    public static class InValues extends Values {

        @Override
        public void accept0(MySqlASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, getItems());
            }
            visitor.endVisit(this);
        }

    }
}
