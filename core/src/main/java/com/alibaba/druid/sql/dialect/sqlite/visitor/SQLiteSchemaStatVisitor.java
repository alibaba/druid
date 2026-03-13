package com.alibaba.druid.sql.dialect.sqlite.visitor;

import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

public class SQLiteSchemaStatVisitor extends SchemaStatVisitor implements SQLiteASTVisitor {
    public SQLiteSchemaStatVisitor() {
    }

    public SQLiteSchemaStatVisitor(SchemaRepository repository) {
        super(repository);
    }
}
