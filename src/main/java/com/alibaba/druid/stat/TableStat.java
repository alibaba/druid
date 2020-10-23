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
package com.alibaba.druid.stat;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableStat {
    int selectCount      = 0;
    int updateCount      = 0;
    int deleteCount      = 0;
    int insertCount      = 0;
    int dropCount        = 0;
    int mergeCount       = 0;
    int createCount      = 0;
    int alterCount       = 0;
    int createIndexCount = 0;
    int dropIndexCount   = 0;
    int referencedCount  = 0;

    public int getReferencedCount() {
        return referencedCount;
    }

    public void incrementReferencedCount() {
        referencedCount++;
    }

    public int getDropIndexCount() {
        return dropIndexCount;
    }

    public void incrementDropIndexCount() {
        this.dropIndexCount++;
    }

    public int getCreateIndexCount() {
        return createIndexCount;
    }

    public void incrementCreateIndexCount() {
        createIndexCount++;
    }

    public int getAlterCount() {
        return alterCount;
    }

    public void incrementAlterCount() {
        this.alterCount++;
    }

    public int getCreateCount() {
        return createCount;
    }

    public void incrementCreateCount() {
        this.createCount++;
    }

    public int getMergeCount() {
        return mergeCount;
    }

    public void incrementMergeCount() {
        this.mergeCount++;
    }

    public int getDropCount() {
        return dropCount;
    }

    public void incrementDropCount() {
        dropCount++;
    }

    public void setDropCount(int dropCount) {
        this.dropCount = dropCount;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void incrementSelectCount() {
        selectCount++;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void incrementUpdateCount() {
        updateCount++;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public void incrementDeleteCount() {
        this.deleteCount++;
    }

    public void setDeleteCount(int deleteCount) {
        this.deleteCount = deleteCount;
    }

    public void incrementInsertCount() {
        this.insertCount++;
    }

    public int getInsertCount() {
        return insertCount;
    }

    public void setInsertCount(int insertCount) {
        this.insertCount = insertCount;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(4);
        if (mergeCount > 0) {
            buf.append("Merge");
        }
        if (insertCount > 0) {
            buf.append("Insert");
        }
        if (updateCount > 0) {
            buf.append("Update");
        }
        if (selectCount > 0) {
            buf.append("Select");
        }
        if (deleteCount > 0) {
            buf.append("Delete");
        }
        if (dropCount > 0) {
            buf.append("Drop");
        }
        if (createCount > 0) {
            buf.append("Create");
        }
        if (alterCount > 0) {
            buf.append("Alter");
        }
        if (createIndexCount > 0) {
            buf.append("CreateIndex");
        }
        if (dropIndexCount > 0) {
            buf.append("DropIndex");
        }

        return buf.toString();
    }

    public static class Name {
        private final String name;
        private final long   hashCode64;

        public Name(String name){
            this(name, FnvHash.hashCode64(name));
        }

        public Name(String name, long hashCode64){
            this.name  = name;
            this.hashCode64 = hashCode64;
        }

        public String getName() {
            return this.name;
        }

        public int hashCode() {
            long value = hashCode64();
            return (int)(value ^ (value >>> 32));
        }

        public long hashCode64() {
            return hashCode64;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Name)) {
                return false;
            }

            Name other = (Name) o;
            return this.hashCode64 == other.hashCode64;
        }

        public String toString() {
            return SQLUtils.normalize(this.name);
        }
    }

    public static class Relationship {
        private Column left;
        private Column right;
        private String operator;

        public Relationship(Column left, Column right, String operator) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        public Column getLeft() {
            return left;
        }

        public Column getRight() {
            return right;
        }

        public String getOperator() {
            return operator;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((left == null) ? 0 : left.hashCode());
            result = prime * result + ((operator == null) ? 0 : operator.hashCode());
            result = prime * result + ((right == null) ? 0 : right.hashCode());
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
            if (getClass() != obj.getClass()) {
                return false;
            }
            Relationship other = (Relationship) obj;
            if (left == null) {
                if (other.left != null) {
                    return false;
                }
            } else if (!left.equals(other.left)) {
                return false;
            }
            if (operator == null) {
                if (other.operator != null) {
                    return false;
                }
            } else if (!operator.equals(other.operator)) {
                return false;
            }
            if (right == null) {
                if (other.right != null) {
                    return false;
                }
            } else if (!right.equals(other.right)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return left + " " + operator + " " + right;
        }

    }

    public static class Condition {

        private final Column       column;
        private final String       operator;
        private final List<Object> values = new ArrayList<Object>();

        public Condition(Column column, String operator) {
            this.column = column;
            this.operator = operator;
        }

        public Column getColumn() {
            return column;
        }

        public String getOperator() {
            return operator;
        }

        public List<Object> getValues() {
            return values;
        }

        public void addValue(Object value) {
            this.values.add(value);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((column == null) ? 0 : column.hashCode());
            result = prime * result + ((operator == null) ? 0 : operator.hashCode());
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
            if (getClass() != obj.getClass()) {
                return false;
            }
            Condition other = (Condition) obj;
            if (column == null) {
                if (other.column != null) {
                    return false;
                }
            } else if (!column.equals(other.column)) {
                return false;
            }
            if (operator == null) {
                if (other.operator != null) {
                    return false;
                }
            } else if (!operator.equals(other.operator)) {
                return false;
            }
            return true;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(this.column.toString());
            buf.append(' ');
            buf.append(this.operator);

            if (values.size() == 1) {
                buf.append(' ');
                buf.append(String.valueOf(this.values.get(0)));
            } else if (values.size() > 0) {
                buf.append(" (");
                for (int i = 0; i < values.size(); ++i) {
                    if (i != 0) {
                        buf.append(", ");
                    }
                    Object val = values.get(i);
                    if (val instanceof String) {
                        String jsonStr = JSONUtils.toJSONString(val);
                        buf.append(jsonStr);
                    } else {
                        buf.append(String.valueOf(val));
                    }
                }
                buf.append(")");
            }

            return buf.toString();
        }
    }

    public static class Column {
        private final     String              table;
        private final     String              name;
        private final     long                hashCode64;

        private           boolean             where;
        private           boolean             select;
        private           boolean             groupBy;
        private           boolean             having;
        private           boolean             join;
        private           boolean             primaryKey; // for ddl
        private           boolean             unique; //
        private           boolean             update;
        private           Map<String, Object> attributes = new HashMap<String, Object>();
        private transient String              fullName;

        /**
         * @since 1.0.20
         */
        private           String              dataType;

        public Column(String table, String name){
            this(table, name, null);
        }

        public Column(String table, String name, DbType dbType){
            this.table = table;
            this.name = name;

            int p = table.indexOf('.');
            if (p != -1) {
                if (dbType == null) {
                    if (table.indexOf('`') != -1) {
                        dbType = DbType.mysql;
                    }
                    else if (table.indexOf('[') != -1) {
                        dbType = DbType.sqlserver;
                    }
                    else if (table.indexOf('@') != -1) {
                        dbType = DbType.oracle;
                    }
                }
                SQLExpr owner = SQLUtils.toSQLExpr(table, dbType);
                hashCode64 = new SQLPropertyExpr(owner, name).hashCode64();
            } else {
                hashCode64 = FnvHash.hashCode64(table, name);
            }
        }

        public Column(String table, String name, long hashCode64){
            this.table = table;
            this.name = name;
            this.hashCode64 = hashCode64;
        }

        public String getTable() {
            return table;
        }

        public String getFullName() {
            if (fullName == null) {
                if (table == null) {
                    fullName = name;
                } else {
                    fullName = table + '.' + name;
                }
            }

            return fullName;
        }

        public long hashCode64() {
            return hashCode64;
        }

        public boolean isWhere() {
            return where;
        }

        public void setWhere(boolean where) {
            this.where = where;
        }

        public boolean isSelect() {
            return select;
        }

        public void setSelec(boolean select) {
            this.select = select;
        }

        public boolean isGroupBy() {
            return groupBy;
        }

        public void setGroupBy(boolean groupBy) {
            this.groupBy = groupBy;
        }

        public boolean isHaving() {
            return having;
        }

        public boolean isJoin() {
            return join;
        }

        public void setJoin(boolean join) {
            this.join = join;
        }

        public void setHaving(boolean having) {
            this.having = having;
        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }

        public void setPrimaryKey(boolean primaryKey) {
            this.primaryKey = primaryKey;
        }

        public boolean isUnique() {
            return unique;
        }

        public void setUnique(boolean unique) {
            this.unique = unique;
        }

        public boolean isUpdate() {
            return update;
        }

        public void setUpdate(boolean update) {
            this.update = update;
        }

        public String getName() {
            return name;
        }

        /**
         * @since 1.0.20
         */
        public String getDataType() {
            return dataType;
        }

        /**
         * @since 1.0.20
         */
        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        public int hashCode() {
            long hash = hashCode64();
            return (int)(hash ^ (hash >>> 32));
        }

        public String toString() {
            if (table != null) {
                return SQLUtils.normalize(table) + "." + SQLUtils.normalize(name);
            }

            return SQLUtils.normalize(name);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Column)) {
                return false;
            }

            Column column = (Column) obj;
            return hashCode64 == column.hashCode64;
        }
    }

    public static enum Mode {
        Insert(1), //
        Update(2), //
        Delete(4), //
        Select(8), //
        Merge(16), //
        Truncate(32), //
        Alter(64), //
        Drop(128), //
        DropIndex(256), //
        CreateIndex(512), //
        Replace(1024),
        ; //

        public final int mark;

        private Mode(int mark){
            this.mark = mark;
        }
    }
}
