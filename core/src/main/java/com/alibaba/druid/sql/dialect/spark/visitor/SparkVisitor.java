/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.spark.visitor;

import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.dialect.spark.ast.SparkCreateTableStatement;

/**
 * @author peiheng.qph
 * @version $Id: AntsparkVisitor.java, v 0.1 2018年09月14日 15:07 peiheng.qph Exp $
 */
public interface SparkVisitor extends HiveASTVisitor {
    boolean visit(SparkCreateTableStatement x);

    void endVisit(SparkCreateTableStatement x);
}
