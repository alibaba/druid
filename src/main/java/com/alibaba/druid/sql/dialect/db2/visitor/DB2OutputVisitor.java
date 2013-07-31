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
package com.alibaba.druid.sql.dialect.db2.visitor;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class DB2OutputVisitor extends SQLASTOutputVisitor implements DB2ASTVisitor {

    public DB2OutputVisitor(Appendable appender){
        super(appender);
    }

    @Override
    public boolean visit(DB2SelectQueryBlock x) {
        this.visit((SQLSelectQueryBlock) x);
        
        if (x.getFirst() != null) {
            println();
            print("FETCH FIRST ");
            x.getFirst().accept(this);
            print(" ROWS ONLY");
        }
        
        return false;
    }

    @Override
    public void endVisit(DB2SelectQueryBlock x) {

    }
}
