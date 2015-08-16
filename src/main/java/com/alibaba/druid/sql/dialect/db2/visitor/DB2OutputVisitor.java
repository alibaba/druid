/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2ValuesStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class DB2OutputVisitor extends SQLASTOutputVisitor implements DB2ASTVisitor {

    public DB2OutputVisitor(Appendable appender){
        super(appender);
    }

    @Override
    public boolean visit(DB2SelectQueryBlock x) {
        this.visit((SQLSelectQueryBlock) x);

        if (x.getFirst() != null) {

            //order by 语句必须在FETCH FIRST ROWS ONLY之前
            SQLObject parent= x.getParent();
            if(parent instanceof SQLSelect)
            {
                SQLOrderBy orderBy= ((SQLSelect) parent).getOrderBy();
                if (orderBy!=null&&orderBy.getItems().size() > 0) {
                    println();
                    print("ORDER BY ");
                    printAndAccept(orderBy.getItems(), ", ");
                    ((SQLSelect) parent).setOrderBy(null);
                }
            }
            println();
            print("FETCH FIRST ");
            x.getFirst().accept(this);
            print(" ROWS ONLY");


        }
        if (x.isForReadOnly()) {
            println();
            print("FOR READ ONLY");
        }

        if (x.getIsolation() != null) {
            println();
            print("WITH ");
            print(x.getIsolation().name());
        }

        if (x.getOptimizeFor() != null) {
            println();
            print("OPTIMIZE FOR ");
            x.getOptimizeFor().accept(this);
        }

        return false;
    }


    @Override
    public void endVisit(DB2SelectQueryBlock x) {

    }

    @Override
    public boolean visit(DB2ValuesStatement x) {
        print("VALUES ");
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public void endVisit(DB2ValuesStatement x) {

    }
}
