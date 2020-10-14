/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.antspark.visitor;

import com.alibaba.druid.sql.dialect.antspark.ast.AntsparkCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 *
 * @author peiheng.qph
 * @version $Id: AntsparkVisitor.java, v 0.1 2018年09月14日 15:07 peiheng.qph Exp $
 */
public interface AntsparkVisitor extends SQLASTVisitor {
    boolean visit(AntsparkCreateTableStatement x);
    void endVisit(AntsparkCreateTableStatement x);
}