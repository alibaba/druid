package com.alibaba.druid.stat;

public class TableStat {

    int selectCount = 0;
    int updateCount = 0;
    int deleteCount = 0;
    int insertCount = 0;

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
        if (insertCount > 0) {
            buf.append("C");
        }
        if (updateCount > 0) {
            buf.append("U");
        }
        if (selectCount > 0) {
            buf.append("R");
        }
        if (deleteCount > 0) {
            buf.append("D");
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
            return this.name.toLowerCase().hashCode();
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

    public static class Column {

        private String table;
        private String name;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int hashCode() {
            int tableHashCode = table != null ? table.toLowerCase().hashCode() : 0;
            int nameHashCode = name != null ? name.toLowerCase().hashCode() : 0;

            return tableHashCode + nameHashCode;
        }

        public String toString() {
            if (table != null) {
                return table + "." + name;
            }

            return name;
        }

        public boolean equals(Object obj) {
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
        Insert(1), Update(2), Delete(4), Select(8);

        public final int mark;

        private Mode(int mark){
            this.mark = mark;
        }
    }
}
