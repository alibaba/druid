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
package com.alibaba.druid.sql.dialect.presto.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class PrestoPrepareStatement extends SQLStatementImpl implements PrestoSQLStatement {
    private SQLName name;
    private SQLSelect select;
    private HiveInsertStatement insert;

    public PrestoPrepareStatement() {
    }

    public PrestoPrepareStatement(SQLName name) {
        this.name = name;
        dbType = DbType.presto;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLSelect getSelect() {
        return select;
    }

    public void setSelect(SQLSelect select) {
        this.select = select;
    }

    public HiveInsertStatement getInsert() {
        return insert;
    }

    public void setInsert(HiveInsertStatement insert) {
        this.insert = insert;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof PrestoASTVisitor) {
            this.accept0((PrestoASTVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    @Override
    public void accept0(PrestoASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            if (this.select != null) {
                acceptChild(visitor, select);
            }
            if (this.insert != null) {
                acceptChild(visitor, insert);
            }
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (name != null) {
            children.add(name);
        }
        if (this.select != null) {
            children.add(select);
        }
        if (this.insert != null) {
            children.add(insert);
        }
        return children;
    }
}
