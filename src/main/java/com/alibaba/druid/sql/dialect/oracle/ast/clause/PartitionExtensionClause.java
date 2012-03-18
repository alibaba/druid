package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class PartitionExtensionClause extends OracleSQLObjectImpl {

    private static final long   serialVersionUID = 1L;

    private boolean             subPartition;
    private SQLName             partition;
    private final List<SQLName> target           = new ArrayList<SQLName>();

    public boolean isSubPartition() {
        return subPartition;
    }

    public void setSubPartition(boolean subPartition) {
        this.subPartition = subPartition;
    }

    public SQLName getPartition() {
        return partition;
    }

    public void setPartition(SQLName partition) {
        this.partition = partition;
    }

    public List<SQLName> getFor() {
        return target;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, partition);
            acceptChild(visitor, target);
        }
        visitor.endVisit(this);
    }

}
