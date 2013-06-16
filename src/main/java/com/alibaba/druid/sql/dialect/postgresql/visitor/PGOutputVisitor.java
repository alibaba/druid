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
package com.alibaba.druid.sql.dialect.postgresql.visitor;

import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithQuery;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGParameter;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGFunctionTableSource;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.FetchClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.ForClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.WindowClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGTruncateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class PGOutputVisitor extends SQLASTOutputVisitor implements PGASTVisitor {

    public PGOutputVisitor(Appendable appender){
        super(appender);
    }

    @Override
    public void endVisit(WindowClause x) {

    }

    @Override
    public boolean visit(WindowClause x) {
        print("WINDOW ");
        x.getName().accept(this);
        print(" AS ");
        for (int i = 0; i < x.getDefinition().size(); ++i) {
            if (i != 0) {
                println(", ");
            }
            print("(");
            x.getDefinition().get(i).accept(this);
            print(")");
        }
        return false;
    }

    @Override
    public void endVisit(FetchClause x) {

    }

    @Override
    public boolean visit(FetchClause x) {
        print("FETCH ");
        if (FetchClause.Option.FIRST.equals(x.getOption())) {
            print("FIRST ");
        } else if (FetchClause.Option.NEXT.equals(x.getOption())) {
            print("NEXT ");
        }
        x.getCount().accept(this);
        print(" ROWS ONLY");
        return false;
    }

    @Override
    public void endVisit(ForClause x) {

    }

    @Override
    public boolean visit(ForClause x) {
        print("FOR ");
        if (ForClause.Option.UPDATE.equals(x.getOption())) {
            print("UPDATE ");
        } else if (ForClause.Option.SHARE.equals(x.getOption())) {
            print("SHARE ");
        }

        if (x.getOf().size() > 0) {
            for (int i = 0; i < x.getOf().size(); ++i) {
                if (i != 0) {
                    println(", ");
                }
                x.getOf().get(i).accept(this);
            }
        }

        if (x.isNoWait()) {
            print(" NOWAIT");
        }

        return false;
    }

    @Override
    public void endVisit(PGWithQuery x) {

    }

    @Override
    public boolean visit(PGWithQuery x) {
        x.getName().accept(this);

        if (x.getColumns().size() > 0) {
            print(" (");
            printAndAccept(x.getColumns(), ", ");
            print(")");
        }
        println();
        print("AS");
        println();
        print("(");
        incrementIndent();
        println();
        x.getQuery().accept(this);
        decrementIndent();
        println();
        print(")");

        return false;
    }

    @Override
    public void endVisit(PGWithClause x) {

    }

    @Override
    public boolean visit(PGWithClause x) {
        print("WITH");
        incrementIndent();
        println();
        printlnAndAccept(x.getWithQuery(), ", ");
        decrementIndent();
        return false;
    }

    public boolean visit(PGSelectQueryBlock x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
            println();
        }

        print("SELECT ");

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print("ALL ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print("DISTINCT ");

            if (x.getDistinctOn() != null) {
                print("ON ");
                printAndAccept(x.getDistinctOn(), ", ");
            }
        }

        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            println();
            if (x.getIntoOption() != null) {
                print(x.getIntoOption().name());
                print(" ");
            }

            x.getInto().accept(this);
        }

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

        if (x.getWindow() != null) {
            println();
            x.getWindow().accept(this);
        }

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            println();
            print("LIMIT ");
            x.getLimit().accept(this);
        }

        if (x.getOffset() != null) {
            println();
            print("OFFSET ");
            x.getOffset().accept(this);
            print(" ROWS");
        }

        if (x.getFetch() != null) {
            println();
            x.getFetch().accept(this);
        }

        if (x.getForClause() != null) {
            println();
            x.getForClause().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(PGTruncateStatement x) {

    }

    @Override
    public boolean visit(PGTruncateStatement x) {
        print("TRUNCATE TABLE ");
        if (x.isOnly()) {
            print("ONLY ");
        }

        printlnAndAccept(x.getTableSources(), ", ");

        if (x.getRestartIdentity() != null) {
            if (x.getRestartIdentity().booleanValue()) {
                print(" RESTART IDENTITY");
            } else {
                print(" CONTINUE IDENTITY");
            }
        }

        if (x.getCascade() != null) {
            if (x.getCascade().booleanValue()) {
                print(" CASCADE");
            } else {
                print(" RESTRICT");
            }
        }
        return false;
    }

    @Override
    public void endVisit(PGDeleteStatement x) {

    }

    @Override
    public boolean visit(PGDeleteStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
            println();
        }

        print("DELETE FROM ");

        if (x.isOnly()) {
            print("ONLY ");
        }

        x.getTableName().accept(this);

        if (x.getAlias() != null) {
            print(" AS ");
            print(x.getAlias());
        }

        if (x.getUsing().size() > 0) {
            println();
            print("USING ");
            printAndAccept(x.getUsing(), ", ");
        }

        if (x.getWhere() != null) {
            println();
            print("WHERE ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        if (x.isReturning()) {
            println();
            print("RETURNING *");
        }

        return false;
    }

    @Override
    public void endVisit(PGInsertStatement x) {

    }

    @Override
    public boolean visit(PGInsertStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
            println();
        }

        print("INSERT INTO ");

        x.getTableSource().accept(this);

        if (x.getColumns().size() > 0) {
            incrementIndent();
            println();
            print("(");
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

        if (x.getValues() != null) {
            println();
            print("VALUES ");
            printlnAndAccept(x.getValuesList(), ", ");
        } else {
            if (x.getQuery() != null) {
                println();
                x.getQuery().accept(this);
            }
        }

        if (x.getReturning() != null) {
            println();
            print("RETURNING ");
            x.getReturning().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(PGSelectStatement x) {

    }

    @Override
    public boolean visit(PGSelectStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
            println();
        }

        return visit((SQLSelectStatement) x);
    }

    @Override
    public void endVisit(PGUpdateStatement x) {

    }

    @Override
    public boolean visit(PGUpdateStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
            println();
        }

        print("UPDATE ");

        if (x.isOnly()) {
            print("ONLY ");
        }

        x.getTableSource().accept(this);

        println();
        print("SET ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print(", ");
            }
            x.getItems().get(i).accept(this);
        }

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

        if (x.getReturning().size() > 0) {
            println();
            print("RETURNING ");
            printAndAccept(x.getReturning(), ", ");
        }

        return false;
    }

    @Override
    public void endVisit(PGSelectQueryBlock x) {

    }

    @Override
    public void endVisit(PGParameter x) {

    }

    @Override
    public boolean visit(PGParameter x) {
        x.getName().accept(this);
        print(" ");

        x.getDataType().accept(this);

        return false;
    }

    @Override
    public boolean visit(PGFunctionTableSource x) {
        x.getExpr().accept(this);

        if (x.getAlias() != null) {
            print(" AS ");
            print(x.getAlias());
        }

        if (x.getParameters().size() > 0) {
            print('(');
            printAndAccept(x.getParameters(), ", ");
            print(')');
        }

        return false;
    }

    @Override
    public void endVisit(PGFunctionTableSource x) {

    }

}
