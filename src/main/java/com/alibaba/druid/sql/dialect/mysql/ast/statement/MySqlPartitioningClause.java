package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLPartitioningClause;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;

@SuppressWarnings("serial")
public abstract class MySqlPartitioningClause extends MySqlObjectImpl implements SQLPartitioningClause {

    private List<MySqlPartitioningDef> partitions = new ArrayList<MySqlPartitioningDef>();

    public List<MySqlPartitioningDef> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<MySqlPartitioningDef> partitions) {
        this.partitions = partitions;
    }

}
