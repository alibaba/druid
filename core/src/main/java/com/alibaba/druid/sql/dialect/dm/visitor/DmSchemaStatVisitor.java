package com.alibaba.druid.sql.dialect.dm.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

public class DmSchemaStatVisitor extends SchemaStatVisitor implements DmASTVisitor {
    public DmSchemaStatVisitor() {
        super(DbType.dm);
    }

    public DmSchemaStatVisitor(SchemaRepository repository) {
        super(repository);
    }
}
