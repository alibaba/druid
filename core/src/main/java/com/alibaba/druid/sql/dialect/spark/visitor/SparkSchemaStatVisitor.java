/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.spark.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.spark.ast.SparkCreateTableStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;

/**
 * @author peiheng.qph
 * @version $Id: AntsparkSchemaStatVisitor.java, v 0.1 2018年09月16日 23:09 peiheng.qph Exp $
 */
public class SparkSchemaStatVisitor extends HiveSchemaStatVisitor implements SparkVisitor {
    {
        dbType = DbType.spark;
    }

    public SparkSchemaStatVisitor() {
        super(DbType.spark);
    }

    public SparkSchemaStatVisitor(SchemaRepository repository) {
        super(repository);
    }

    @Override
    public boolean visit(SparkCreateTableStatement x) {
        return super.visit((SQLCreateTableStatement) x);
    }
}
