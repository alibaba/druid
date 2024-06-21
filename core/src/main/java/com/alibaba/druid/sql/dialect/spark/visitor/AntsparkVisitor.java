/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.spark.visitor;

import com.alibaba.druid.sql.dialect.spark.ast.SparkCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * @author peiheng.qph
 * @version $Id: AntsparkVisitor.java, v 0.1 2018年09月14日 15:07 peiheng.qph Exp $
 */
public interface AntsparkVisitor extends SQLASTVisitor {
    boolean visit(SparkCreateTableStatement x);

    void endVisit(SparkCreateTableStatement x);
}
