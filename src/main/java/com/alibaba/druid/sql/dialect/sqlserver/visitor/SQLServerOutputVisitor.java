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
package com.alibaba.druid.sql.dialect.sqlserver.visitor;

import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.Top;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class SQLServerOutputVisitor extends SQLASTOutputVisitor implements SQLServerASTVisitor {

    public SQLServerOutputVisitor(Appendable appender){
        super(appender);
    }

    public boolean visit(SQLServerSelectQueryBlock x) {
        print("SELECT ");

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print("ALL ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print("DISTINCT ");
        } else if (SQLSetQuantifier.UNIQUE == x.getDistionOption()) {
            print("UNIQUE ");
        }

        if (x.getTop() != null) {
            x.getTop().accept(this);
        }

        printSelectList(x.getSelectList());

        if (x.getFrom() != null) {
            println();
            print("FROM ");
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print("WHERE ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        if (x.getGroupBy() != null) {
            println();
            x.getGroupBy().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(SQLServerSelectQueryBlock x) {

    }

    @Override
    public boolean visit(Top x) {
        print("TOP ");
        x.getExpr().accept(this);
        print(" ");
        return false;
    }

    @Override
    public void endVisit(Top x) {

    }

    @Override
    public boolean visit(SQLServerObjectReferenceExpr x) {
        print(x.toString());
        return false;
    }

    @Override
    public void endVisit(SQLServerObjectReferenceExpr x) {

    }

    @Override
    public boolean visit(SQLServerInsertStatement x) {
        print("INSERT INTO ");

        x.getTableName().accept(this);

        if (x.getColumns().size() > 0) {
            incrementIndent();
            print(" (");
            for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print(", ");
                }

                x.getColumns().get(i).accept(this);
            }
            print(")");
            decrementIndent();
        }

        if (x.getValuesList().size() != 0) {
            println();
            print("VALUES ");
            for (int i = 0, size = x.getValuesList().size(); i < size; ++i) {
                if (i != 0) {
                    print(", ");
                }
                x.getValuesList().get(i).accept(this);
            }

        }

        if (x.getQuery() != null) {
            println();
            x.getQuery().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(SQLServerInsertStatement x) {

    }

}
