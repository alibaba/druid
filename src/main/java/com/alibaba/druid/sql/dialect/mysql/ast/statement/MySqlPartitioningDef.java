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

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, values);
            acceptChild(visitor, dataDirectory);
            acceptChild(visitor, indexDirectory);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getIndexDirectory() {
        return indexDirectory;
    }

    public void setIndexDirectory(SQLExpr indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

    public SQLExpr getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(SQLExpr dataDirectory) {
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
        this.name = name;
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
