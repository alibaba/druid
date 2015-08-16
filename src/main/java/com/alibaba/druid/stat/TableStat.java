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
package com.alibaba.druid.stat;

import com.alibaba.druid.util.StringUtils;

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

        private String name;

        public Name(String name){
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public int hashCode() {
            return StringUtils.lowerHashCode(name);
        }

        public boolean equals(Object o) {
            if (!(o instanceof Name)) {
                return false;
            }

            Name other = (Name) o;

            return this.name.equalsIgnoreCase(other.name);
        }

        public String toString() {
            return this.name;
        }
    }

    public static class Relationship {

        private Column left;
        private Column right;
        private String operator;

        public Column getLeft() {
            return left;
        }

        public void setLeft(Column left) {
            this.left = left;
        }

        public Column getRight() {
            return right;
        }

        public void setRight(Column right) {
            this.right = right;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
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

        private Column       column;
        private String       operator;

        private List<Object> values = new ArrayList<Object>();

        public Column getColumn() {
            return column;
        }

        public void setColumn(Column column) {
            this.column = column;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public List<Object> getValues() {
            return values;
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.column.toString());
            stringBuilder.append(' ');
            stringBuilder.append(this.operator);

            if (values.size() == 1) {
                stringBuilder.append(' ');
                stringBuilder.append(String.valueOf(this.values.get(0)));
            } else if (values.size() > 0) {
                stringBuilder.append(" (");
                for (int i = 0; i < values.size(); ++i) {
                    if (i != 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(String.valueOf(values.get(i)));
                }
                stringBuilder.append(")");
            }

            return stringBuilder.toString();
        }
    }

    public static class Column {

        private String              table;
        private String              name;
        private boolean             where;
        private boolean             select;
        private boolean             groupBy;
        private boolean             having;
        private boolean             join;

        private Map<String, Object> attributes = new HashMap<String, Object>();

        public Column(){

        }

        public Column(String table, String name){
            this.table = table;
            this.name = name;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        public int hashCode() {
            int tableHashCode = table != null ? StringUtils.lowerHashCode(table) : 0;
            int nameHashCode = name != null ? StringUtils.lowerHashCode(name) : 0;

            return tableHashCode + nameHashCode;
        }

        public String toString() {
            if (table != null) {
                return table + "." + name;
            }

            return name;
        }

        public boolean equals(Object obj) {

            if (!(obj instanceof  Column)) {
                return false;
            }

            Column column = (Column) obj;

            if (table == null) {
                if (column.getTable() != null) {
                    return false;
                }
            } else {
                if (!table.equalsIgnoreCase(column.getTable())) {
                    return false;
                }
            }

            if (name == null) {
                if (column.getName() != null) {
                    return false;
                }
            } else {
                if (!name.equalsIgnoreCase(column.getName())) {
                    return false;
                }
            }

            return true;
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
        CreateIndex(512)//
        ; //

        public final int mark;

        private Mode(int mark){
            this.mark = mark;
        }
    }
}
