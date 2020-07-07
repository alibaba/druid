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
package com.alibaba.druid.sql.dialect.impala.stmt;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.impala.visitor.ImpalaASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.mysql.cj.jdbc.StatementImpl;

import java.util.ArrayList;
import java.util.List;

public class ImpalaMetaStatement extends MySqlStatementImpl {


    private Token metaType;

    private List<SQLAssignItem>  partitions = new ArrayList<SQLAssignItem>();


    public ImpalaMetaStatement(Token metaType){
        this.dbType = JdbcConstants.IMPALA;
        this.metaType = metaType;
    }

    public ImpalaMetaStatement() {
        this.dbType = JdbcConstants.IMPALA;
    }



    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSource = tableSource;
    }

    public void setTableSource(SQLName tableName) {
        this.setTableSource(new SQLExprTableSource(tableName));
    }


    protected SQLExprTableSource tableSource;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof ImpalaASTVisitor) {
            accept0((ImpalaASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    protected void accept0(ImpalaASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, partitions);
        }
        visitor.endVisit(this);
    }

    public void addPartition(SQLAssignItem partition) {
        if (partition != null) {
            partition.setParent(this);
        }
        this.partitions.add(partition);
    }
    public Token getMetaType() {
        return metaType;
    }

    public List<SQLAssignItem> getPartitions() {
        return partitions;
    }


}
