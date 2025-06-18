package com.alibaba.druid.sql.dialect.synapse.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;

public class SynapseOutputVisitor extends SQLServerOutputVisitor implements SynapseASTVisitor {
    public SynapseOutputVisitor(StringBuilder appender) {
        super(appender);
        dbType = DbType.synapse;
    }

    public SynapseOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.synapse;
    }
}
