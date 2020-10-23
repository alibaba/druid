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
package com.alibaba.druid.sql.dialect.blink.vsitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLReplaceStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.blink.ast.BlinkCreateTableStatement;
import com.alibaba.druid.sql.dialect.h2.visitor.H2ASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;

public class BlinkOutputVisitor extends SQLASTOutputVisitor implements BlinkVisitor {
    public BlinkOutputVisitor(Appendable appender) {
        super(appender);
    }

    public BlinkOutputVisitor(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public BlinkOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(BlinkCreateTableStatement x) {
        super.visit((SQLCreateTableStatement) x);
        return false;
    }

    protected void printTableElements(List<SQLTableElement> tableElementList) {
        int size = tableElementList.size();
        if (size == 0) {
            return;
        }

        BlinkCreateTableStatement stmt = (BlinkCreateTableStatement) tableElementList.get(0).getParent();

        print0(" (");

        this.indentCount++;
        println();
        for (int i = 0; i < size; ++i) {
            SQLTableElement element = tableElementList.get(i);
            element.accept(this);

            if (i != size - 1) {
                print(',');
            }
            if (this.isPrettyFormat() && element.hasAfterComment()) {
                print(' ');
                printlnComment(element.getAfterCommentsDirect());
            }

            if (i != size - 1) {
                println();
            }
        }

        if (stmt.getPeriodFor() != null) {
            print(',');
            println();
            print0(ucase ? "PERIOD FOR " : "period for ");
            stmt.getPeriodFor().accept(this);
        }
        this.indentCount--;
        println();
        print(')');
    }

    @Override
    public void endVisit(BlinkCreateTableStatement x) {

    }
}
