package com.alibaba.druid.sql.dialect.clickhouse.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseAlterTableUpdateStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class ClickSchemaStatVisitor extends SchemaStatVisitor implements ClickhouseVisitor {
    {
        dbType = DbType.antspark;
    }

    public ClickSchemaStatVisitor() {
        super(DbType.antspark);
    }

    public ClickSchemaStatVisitor(SchemaRepository repository) {
        super(repository);
    }

    @Override
    public boolean visit(ClickhouseAlterTableUpdateStatement x) {
        TableStat stat = this.getTableStat(x.getTableName());
        for (SQLUpdateSetItem column : x.getItems()) {
            this.addColumn(x.getTableName(), column.getColumn().toString());
        }
        stat.incrementUpdateCount();
        return false;
    }
}
