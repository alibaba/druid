/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement.PartitionedBy;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveShowTablesStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class HiveOutputVisitor extends SQLASTOutputVisitor implements HiveASTVisitor {

    public HiveOutputVisitor(Appendable appender){
        super(appender);
    }

    @Override
    public void endVisit(HiveCreateTableStatement x) {
        
    }

    @Override
    public boolean visit(HiveCreateTableStatement x) {
        visit((SQLCreateTableStatement) x);
        
        if (x.getPartitionedBy() != null) {
            println();
            x.getPartitionedBy().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(PartitionedBy x) {
        
    }

    @Override
    public boolean visit(PartitionedBy x) {
        print("PARTITIONED BY (");
        print(x.getName());
        print(" ");
        x.getType().accept(this);
        print(")");

        return false;
    }

    @Override
    public void endVisit(HiveShowTablesStatement x) {
        
    }

    @Override
    public boolean visit(HiveShowTablesStatement x) {
        print("SHOW TABLES");
        if (x.getPattern() != null) {
            print(" ");
            x.getPattern().accept(this);
        }
        return false;
    }

}
