package com.alibaba.druid.sql.dialect.bigquery.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

public class BigQuerySchemaStatVisitor extends SchemaStatVisitor implements BigQueryVisitor {
    public BigQuerySchemaStatVisitor() {
        super(DbType.bigquery);
    }

    public BigQuerySchemaStatVisitor(SchemaRepository repository) {
        super(repository);
    }
}
