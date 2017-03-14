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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlCreateTableStatement extends SQLCreateTableStatement implements MySqlStatement {

    private Map<String, SQLObject> tableOptions = new LinkedHashMap<String, SQLObject>();

    private SQLPartitionBy  partitioning;

    private List<SQLCommentHint>   hints        = new ArrayList<SQLCommentHint>();

    private List<SQLCommentHint>   optionHints  = new ArrayList<SQLCommentHint>();

    private SQLExprTableSource     like;
    
    private SQLName                tableGroup;

    public MySqlCreateTableStatement(){
        super (JdbcConstants.MYSQL);
    }

    public SQLExprTableSource getLike() {
        return like;
    }

    public void setLike(SQLName like) {
        this.setLike(new SQLExprTableSource(like));
    }

    public void setLike(SQLExprTableSource like) {
        if (like != null) {
            like.setParent(this);
        }
        this.like = like;
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public void setTableOptions(Map<String, SQLObject> tableOptions) {
        this.tableOptions = tableOptions;
    }

    public SQLPartitionBy getPartitioning() {
        return partitioning;
    }

    public void setPartitioning(SQLPartitionBy partitioning) {
        this.partitioning = partitioning;
    }

    public Map<String, SQLObject> getTableOptions() {
        return tableOptions;
    }

    @Deprecated
    public SQLSelect getQuery() {
        return select;
    }

    @Deprecated
    public void setQuery(SQLSelect query) {
        this.select = query;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            throw new IllegalArgumentException("not support visitor type : " + visitor.getClass().getName());
        }
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, getHints());
            this.acceptChild(visitor, getTableSource());
            this.acceptChild(visitor, getTableElementList());
            this.acceptChild(visitor, getLike());
            this.acceptChild(visitor, getSelect());
        }
        visitor.endVisit(this);
    }

    public static class TableSpaceOption extends MySqlObjectImpl {

        private SQLName name;
        private SQLExpr storage;

        public SQLName getName() {
            return name;
        }

        public void setName(SQLName name) {
            this.name = name;
        }

        public SQLExpr getStorage() {
            return storage;
        }

        public void setStorage(SQLExpr storage) {
            this.storage = storage;
        }

        @Override
        public void accept0(MySqlASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, getName());
                acceptChild(visitor, getStorage());
            }
            visitor.endVisit(this);
        }

    }

    public List<SQLCommentHint> getOptionHints() {
        return optionHints;
    }

    public void setOptionHints(List<SQLCommentHint> optionHints) {
        this.optionHints = optionHints;
    }

    
    public SQLName getTableGroup() {
        return tableGroup;
    }

    
    public void setTableGroup(SQLName tableGroup) {
        this.tableGroup = tableGroup;
    }
}
