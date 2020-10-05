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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLExplainStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MySqlExplainStatement extends SQLExplainStatement implements MySqlStatement {
    private boolean describe;
    private SQLName tableName;
    private SQLName columnName;
    private SQLExpr wild;
    private SQLExpr connectionId;

    private boolean distributeInfo = false; // for ads

    public MySqlExplainStatement() {
        super (DbType.mysql);
    }

    public MySqlExplainStatement(DbType dbType) {
        super (dbType);
    }


    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            // tbl_name [col_name | wild]
            if (tableName != null) {
                tableName.accept(visitor);

                if (columnName != null) {
                    columnName.accept(visitor);
                } else if (wild != null) {
                    wild.accept(visitor);
                }
            } else {
                // {explainable_stmt | FOR CONNECTION connection_id}
                if (connectionId != null) {
                    connectionId.accept(visitor);
                } else {
                    if (statement != null) {
                        statement.accept(visitor);
                    }
                }
            }
        }

        visitor.endVisit(this);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public String toString() {
        return SQLUtils.toMySqlString(this);
    }

    public boolean isDescribe() {
        return describe;
    }

    public void setDescribe(boolean describe) {
        this.describe = describe;
    }

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        this.tableName = tableName;
    }

    public SQLName getColumnName() {
        return columnName;
    }

    public void setColumnName(SQLName columnName) {
        this.columnName = columnName;
    }

    public SQLExpr getWild() {
        return wild;
    }

    public void setWild(SQLExpr wild) {
        this.wild = wild;
    }

    public SQLExpr getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(SQLExpr connectionId) {
        this.connectionId = connectionId;
    }

    public boolean isDistributeInfo() {
        return distributeInfo;
    }

    public void setDistributeInfo(boolean distributeInfo) {
        this.distributeInfo = distributeInfo;
    }
}
