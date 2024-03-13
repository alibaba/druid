package com.alibaba.druid.sql.dialect.clickhouse.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

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
}
