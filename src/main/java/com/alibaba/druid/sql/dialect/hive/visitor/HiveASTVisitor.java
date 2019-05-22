/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.dialect.hive.ast.HiveClusterBy;
import com.alibaba.druid.sql.dialect.hive.ast.HiveClusterByItem;
import com.alibaba.druid.sql.dialect.hive.ast.HiveDistributeBy;
import com.alibaba.druid.sql.dialect.hive.ast.HiveDistributeByItem;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveSelectSortByItem;
import com.alibaba.druid.sql.dialect.hive.ast.HiveShowDatabasesStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveShowTablesStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveSortBy;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface HiveASTVisitor extends SQLASTVisitor {
    boolean visit(HiveCreateTableStatement x);
    void endVisit(HiveCreateTableStatement x);

    boolean visit(HiveMultiInsertStatement x);
    void endVisit(HiveMultiInsertStatement x);

    boolean visit(HiveInsertStatement x);
    void endVisit(HiveInsertStatement x);

    boolean visit(HiveInsert x);
    void endVisit(HiveInsert x);
    
    // sort by
    boolean visit(HiveSortBy x);
    void endVisit(HiveSortBy x);
    
    boolean visit(HiveSelectSortByItem x);
    void endVisit(HiveSelectSortByItem x);
    
    // distribute by
    boolean visit(HiveDistributeBy x);
    void endVisit(HiveDistributeBy x);
    
    boolean visit(HiveDistributeByItem x);
    void endVisit(HiveDistributeByItem x);
    
    // cluster by
    boolean visit(HiveClusterBy x);
    void endVisit(HiveClusterBy x);
    
    boolean visit(HiveClusterByItem x);
    void endVisit(HiveClusterByItem x);
    
    // show databases
    boolean visit(HiveShowDatabasesStatement x);
    void endVisit(HiveShowDatabasesStatement x);
    
    // show tables
    boolean visit(HiveShowTablesStatement x);
    void endVisit(HiveShowTablesStatement x);
    
}
