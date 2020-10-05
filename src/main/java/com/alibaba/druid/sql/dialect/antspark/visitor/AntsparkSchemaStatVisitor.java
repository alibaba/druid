/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.antspark.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.antspark.ast.AntsparkCreateTableStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

/**
 *
 * @author peiheng.qph
 * @version $Id: AntsparkSchemaStatVisitor.java, v 0.1 2018年09月16日 23:09 peiheng.qph Exp $
 */
public class AntsparkSchemaStatVisitor extends SchemaStatVisitor implements AntsparkVisitor {
    {
        dbType = DbType.antspark;
    }

    public AntsparkSchemaStatVisitor() {
        super(DbType.antspark);
    }

    public AntsparkSchemaStatVisitor(SchemaRepository repository) {
        super(repository);
    }

    @Override
    public boolean visit(AntsparkCreateTableStatement x) {
        return super.visit((SQLCreateTableStatement) x);
    }

    @Override
    public void endVisit(AntsparkCreateTableStatement x) {
        super.endVisit((SQLCreateTableStatement) x);

    }
}