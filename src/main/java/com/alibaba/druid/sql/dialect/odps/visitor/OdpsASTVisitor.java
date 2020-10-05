/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.odps.visitor;

import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;
import com.alibaba.druid.sql.dialect.odps.ast.*;

public interface OdpsASTVisitor extends HiveASTVisitor {

    void endVisit(OdpsCreateTableStatement x);

    boolean visit(OdpsCreateTableStatement x);

    void endVisit(OdpsUDTFSQLSelectItem x);
    
    boolean visit(OdpsUDTFSQLSelectItem x);

    void endVisit(OdpsSetLabelStatement x);
    
    boolean visit(OdpsSetLabelStatement x);
    
    void endVisit(OdpsSelectQueryBlock x);
    
    boolean visit(OdpsSelectQueryBlock x);

    void endVisit(OdpsAddStatisticStatement x);
    
    boolean visit(OdpsAddStatisticStatement x);
    
    void endVisit(OdpsRemoveStatisticStatement x);
    
    boolean visit(OdpsRemoveStatisticStatement x);
    
    void endVisit(OdpsStatisticClause.TableCount x);
    
    boolean visit(OdpsStatisticClause.TableCount x);
    
    void endVisit(OdpsStatisticClause.ExpressionCondition x);
    
    boolean visit(OdpsStatisticClause.ExpressionCondition x);
    
    void endVisit(OdpsStatisticClause.NullValue x);
    
    boolean visit(OdpsStatisticClause.NullValue x);

    void endVisit(OdpsStatisticClause.DistinctValue x);

    boolean visit(OdpsStatisticClause.DistinctValue x);

    void endVisit(OdpsStatisticClause.ColumnSum x);
    
    boolean visit(OdpsStatisticClause.ColumnSum x);
    
    void endVisit(OdpsStatisticClause.ColumnMax x);
    
    boolean visit(OdpsStatisticClause.ColumnMax x);
    
    void endVisit(OdpsStatisticClause.ColumnMin x);
    
    boolean visit(OdpsStatisticClause.ColumnMin x);
    
    void endVisit(OdpsReadStatement x);
    
    boolean visit(OdpsReadStatement x);
    
    void endVisit(OdpsShowGrantsStmt x);
    
    boolean visit(OdpsShowGrantsStmt x);
    
    void endVisit(OdpsListStmt x);
    
    boolean visit(OdpsListStmt x);
    
    void endVisit(OdpsGrantStmt x);
    
    boolean visit(OdpsGrantStmt x);

    boolean visit(OdpsAddTableStatement x);
    void endVisit(OdpsAddTableStatement x);

    boolean visit(OdpsAddFileStatement x);
    void endVisit(OdpsAddFileStatement x);

    boolean visit(OdpsAddUserStatement x);
    void endVisit(OdpsAddUserStatement x);

    boolean visit(OdpsRemoveUserStatement x);
    void endVisit(OdpsRemoveUserStatement x);

    boolean visit(OdpsAlterTableSetChangeLogs x);
    void endVisit(OdpsAlterTableSetChangeLogs x);

    boolean visit(OdpsCountStatement x);
    void endVisit(OdpsCountStatement x);

    boolean visit(OdpsQueryAliasStatement x);
    void endVisit(OdpsQueryAliasStatement x);

    boolean visit(OdpsTransformExpr x);
    void endVisit(OdpsTransformExpr x);

    boolean visit(OdpsExstoreStatement x);
    void endVisit(OdpsExstoreStatement x);

}
