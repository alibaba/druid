package com.alibaba.druid.sql.dialect.clickhouse.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKAlterTableUpdateStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class CKStatVisitor extends SchemaStatVisitor implements CKVisitor {
    {
        dbType = DbType.spark;
    }

    public CKStatVisitor() {
        super(DbType.spark);
    }

    public CKStatVisitor(SchemaRepository repository) {
        super(repository);
    }

    @Override
    public boolean visit(CKAlterTableUpdateStatement x) {
        TableStat stat = this.getTableStat(x.getTableName());
        for (SQLUpdateSetItem column : x.getItems()) {
            this.addColumn(x.getTableName(), column.getColumn().toString());
        }
        stat.incrementUpdateCount();
        return false;
    }
}
