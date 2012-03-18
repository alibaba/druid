package com.alibaba.druid.sql.dialect.hive.ast.stmt;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveSQLObjectImpl;
import com.alibaba.druid.sql.dialect.hive.ast.HiveStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class HiveCreateTableStatement extends SQLCreateTableStatement implements HiveStatement {

    private static final long serialVersionUID = 1L;

    private PartitionedBy     partitionedBy;

    public PartitionedBy getPartitionedBy() {
        return partitionedBy;
    }

    public void setPartitionedBy(PartitionedBy partitionedBy) {
        this.partitionedBy = partitionedBy;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((HiveASTVisitor) visitor);
    }

    @Override
    public void accept0(HiveASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSource);
            this.acceptChild(visitor, tableElementList);
            this.acceptChild(visitor, partitionedBy);
        }
        visitor.endVisit(this);
    }

    public static class PartitionedBy extends HiveSQLObjectImpl {

        private static final long serialVersionUID = 1L;
        private String            name;
        private SQLDataType       type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SQLDataType getType() {
            return type;
        }

        public void setType(SQLDataType type) {
            this.type = type;
        }

        @Override
        public void accept0(HiveASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, type);
            }
            visitor.endVisit(this);
        }

    }

}
