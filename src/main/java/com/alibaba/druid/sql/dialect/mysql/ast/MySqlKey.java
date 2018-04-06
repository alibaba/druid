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
package com.alibaba.druid.sql.dialect.mysql.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.ast.statement.SQLUniqueConstraint;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlKey extends SQLUnique implements SQLUniqueConstraint, SQLTableConstraint {

    private String  indexType;

    private boolean hasConstaint;

    private SQLExpr keyBlockSize;

    public MySqlKey(){
        dbType = JdbcConstants.MYSQL;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        }
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getName());
            acceptChild(visitor, this.getColumns());
            acceptChild(visitor, name);
        }
        visitor.endVisit(this);
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public boolean isHasConstaint() {
        return hasConstaint;
    }

    public void setHasConstaint(boolean hasConstaint) {
        this.hasConstaint = hasConstaint;
    }

    public void cloneTo(MySqlKey x) {
        super.cloneTo(x);
        x.indexType = indexType;
        x.hasConstaint = hasConstaint;
        if (keyBlockSize != null) {
            this.setKeyBlockSize(keyBlockSize.clone());
        }
    }

    public MySqlKey clone() {
        MySqlKey x = new MySqlKey();
        cloneTo(x);
        return x;
    }

    public SQLExpr getKeyBlockSize() {
        return keyBlockSize;
    }

    public void setKeyBlockSize(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.keyBlockSize = x;
    }
}
