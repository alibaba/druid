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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.repository.SchemaObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FNVUtils;

public class SQLExprTableSource extends SQLTableSourceImpl {

    protected SQLExpr     expr;
    private List<SQLName> partitions;
    private SchemaObject  schemaObject;

    public SQLExprTableSource(){

    }

    public SQLExprTableSource(SQLExpr expr){
        this(expr, null);
    }

    public SQLExprTableSource(SQLExpr expr, String alias){
        this.setExpr(expr);
        this.setAlias(alias);
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    public SQLName getName() {
        if (expr instanceof SQLName) {
            return (SQLName) expr;
        }
        return null;
    }

    public String getSchema() {
        if (expr == null) {
            return null;
        }

        if (expr instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) expr).getOwnernName();
        }

        return null;
    }

    public void setSchema(String schema) {
        if (expr instanceof SQLIdentifierExpr) {
            if (schema == null) {
                return;
            }

            String ident = ((SQLIdentifierExpr) expr).getName();
            this.setExpr(new SQLPropertyExpr(schema, ident));
        } else if (expr instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
            if (schema == null) {
                setExpr(new SQLIdentifierExpr(propertyExpr.getName()));
            } else {
                propertyExpr.setOwner(schema);
            }
        }
    }

    public List<SQLName> getPartitions() {
        if (this.partitions == null) {
            this.partitions = new ArrayList<SQLName>(2);
        }
        
        return partitions;
    }
    
    public int getPartitionSize() {
        if (this.partitions == null) {
            return 0;
        }
        return this.partitions.size();
    }

    public void addPartition(SQLName partition) {
        if (partition != null) {
            partition.setParent(this);
        }
        
        if (this.partitions == null) {
            this.partitions = new ArrayList<SQLName>(2);
        }
        this.partitions.add(partition);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        this.expr.output(buf);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLExprTableSource that = (SQLExprTableSource) o;

        if (expr != null ? !expr.equals(that.expr) : that.expr != null) return false;
        return partitions != null ? partitions.equals(that.partitions) : that.partitions == null;
    }

    @Override
    public int hashCode() {
        int result = expr != null ? expr.hashCode() : 0;
        result = 31 * result + (partitions != null ? partitions.hashCode() : 0);
        return result;
    }

    public String computeAlias() {
        String alias = this.getAlias();

        if (alias == null) {
            if (expr instanceof SQLName) {
                alias =((SQLName) expr).getSimpleName();
            }
        }

        return SQLUtils.normalize(alias);
    }

    public SQLExprTableSource clone() {
        SQLExprTableSource x = new SQLExprTableSource();
        cloneTo(x);
        return x;
    }

    public void cloneTo(SQLExprTableSource x) {
        x.alias = alias;

        if (expr != null) {
            x.expr = expr.clone();
        }

        if (partitions != null) {
            for (SQLName p : partitions) {
                SQLName p1 = p.clone();
                x.addPartition(p1);
            }
        }
    }

    public SchemaObject getSchemaObject() {
        return schemaObject;
    }

    public void setSchemaObject(SchemaObject schemaObject) {
        this.schemaObject = schemaObject;
    }

    public boolean containsAlias(String alias) {
        if (SQLUtils.nameEquals(this.alias, alias)) {
            return true;
        }

        String name = null;
        if (expr instanceof SQLIdentifierExpr) {
            name = ((SQLIdentifierExpr) expr).getName();
        } else if (expr instanceof SQLPropertyExpr) {
            name = ((SQLPropertyExpr) expr).getName();
        }

        if (name != null && SQLUtils.nameEquals(name, alias)) {
            return true;
        }

        return false;
    }

    public boolean containsAlias(long aliasHash) {
        if (this.alias_hash() == aliasHash) {
            return true;
        }

        if (expr instanceof SQLName) {
            long exprNameHash = ((SQLName) expr).name_hash_lower();
            return exprNameHash == alias_hash;
        }

        return false;
    }

    public SQLColumnDefinition findColumn(String columnName) {
        if (columnName == null) {
            return null;
        }

        long hash = FNVUtils.fnv_64_lower_normalized(columnName);
        return findColumn(hash);
    }

    public SQLColumnDefinition findColumn(long columnNameHash) {
        if (schemaObject == null) {
            return null;
        }

        SQLStatement stmt = schemaObject.getStatement();
        if (stmt instanceof SQLCreateTableStatement) {
            SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) stmt;
            return createTableStmt.findColumn(columnNameHash);
        }
        return null;
    }

    public SQLTableSource findTableSourceWithColumn(String columnName) {
        if (columnName == null) {
            return null;
        }

        long hash = FNVUtils.fnv_64_lower_normalized(columnName);
        return findTableSourceWithColumn(hash);
    }

    public SQLTableSource findTableSourceWithColumn(long columnName_hash) {
        if (schemaObject == null) {
            return null;
        }

        SQLStatement stmt = schemaObject.getStatement();
        if (stmt instanceof SQLCreateTableStatement) {
            SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) stmt;
            if (createTableStmt.findColumn(columnName_hash) != null) {
                return this;
            }
        }

        return null;
    }

    public SQLTableSource findTableSource(long alias_hash) {
        if (alias_hash == 0) {
            return null;
        }

        if (alias_hash() == alias_hash) {
            return this;
        }

        if (expr instanceof SQLName) {
            long exprNameHash = ((SQLName) expr).name_hash_lower();
            if (exprNameHash == alias_hash) {
                return this;
            }
        }

        return null;
    }
}
