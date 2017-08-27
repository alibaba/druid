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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FNVUtils;

public class SQLIdentifierExpr extends SQLExprImpl implements SQLName {

    protected String          name;

    private transient String  lowerName;
    protected transient long  hash_lower;
    private transient Boolean parameter;

    private transient SQLColumnDefinition resolvedColumn;
    private transient SQLTableSource resolvedTableSource;

    public SQLIdentifierExpr(){

    }

    public SQLIdentifierExpr(String name){
        this.name = name;
    }

    public SQLIdentifierExpr(String name, long hash_lower){
        this.name = name;
        this.hash_lower = hash_lower;
    }

    public String getSimpleName() {
        return name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.lowerName = null;
        this.hash_lower = 0L;
    }

    public String getLowerName() {
        if (lowerName == null && name != null) {
            lowerName = name.toLowerCase();
        }
        return lowerName;
    }

    public long name_hash_lower() {
        if (hash_lower == 0
                && name != null) {
            final int len = name.length();

            boolean quote = false;

            String name = this.name;
            if (len > 2) {
                char c0 = name.charAt(0);
                char c1 = name.charAt(len - 1);
                if ((c0 == '`' && c1 == '`')
                        || (c0 == '"' && c1 == '"')
                        || (c0 == '[' && c1 == ']')) {
                    quote = true;
                }
            }
            if (quote) {
                hash_lower = FNVUtils.fnv_64_lower(name, 1, len -1);
            } else {
                hash_lower = FNVUtils.fnv_64_lower(name);
            }
        }
        return hash_lower;
    }

    public Boolean isParameter() {
        return parameter;
    }

    public void setParameter(Boolean parameter) {
        this.parameter = parameter;
    }

    public void output(StringBuffer buf) {
        buf.append(this.name);
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SQLIdentifierExpr)) {
            return false;
        }
        SQLIdentifierExpr other = (SQLIdentifierExpr) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public boolean equals(SQLIdentifierExpr other) {
        if (name == other.name) {
            return true;
        }

        if (name == null || other.name == null) {
            return false;
        }

        return name.equals(other.name);
    }

    public boolean equalsIgnoreCase(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SQLIdentifierExpr)) {
            return false;
        }
        SQLIdentifierExpr other = (SQLIdentifierExpr) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }

        } else if (!SQLUtils.normalize(name).equalsIgnoreCase(SQLUtils.normalize(other.name))) {
            return false;
        }
        return true;
    }

    public String toString() {
        return this.name;
    }

    public SQLIdentifierExpr clone() {
        SQLIdentifierExpr x = new SQLIdentifierExpr(this.name);
        x.resolvedColumn = resolvedColumn;
        x.resolvedTableSource = resolvedTableSource;
        return x;
    }

    public String normalizedName() {
        return SQLUtils.normalize(name);
    }

    public SQLColumnDefinition getResolvedColumn() {
        return resolvedColumn;
    }

    public void setResolvedColumn(SQLColumnDefinition resolvedColumn) {
        this.resolvedColumn = resolvedColumn;
    }

    public SQLTableSource getResolvedTableSource() {
        return resolvedTableSource;
    }

    public void setResolvedTableSource(SQLTableSource resolvedTableSource) {
        this.resolvedTableSource = resolvedTableSource;
    }

    public SQLDataType computeDataType() {
        if (resolvedColumn != null) {
            return resolvedColumn.getDataType();
        }

        if (resolvedTableSource != null
                && resolvedTableSource instanceof SQLSubqueryTableSource) {
            SQLSelect select = ((SQLSubqueryTableSource) resolvedTableSource).getSelect();
            SQLSelectQueryBlock queryBlock = select.getFirstQueryBlock();
            if (queryBlock == null) {
                return null;
            }
            SQLSelectItem selectItem = queryBlock.findSelectItem(name_hash_lower());
            if (selectItem != null) {
                return selectItem.computeDataType();
            }
        }

        return null;
    }

    public boolean nameEquals(String name) {
        return SQLUtils.nameEquals(this.name, name);
    }
}
