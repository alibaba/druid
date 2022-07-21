/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.alibaba.druid.sql.dialect.saphana.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaInsertStatement;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaSelectQueryBlock;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaUpdateStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

/**
 * @author nukiyoam
 */
public class SAPHanaOutputVisitor extends SQLASTOutputVisitor implements SAPHanaASTVisitor {
    public SAPHanaOutputVisitor(Appendable appender) {
        super(appender, DbType.sap_hana);
    }

    public SAPHanaOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
        this.dbType = DbType.sap_hana;
    }

    @Override
    public boolean visit(SAPHanaSelectQueryBlock x) {
        print0(ucase ? "SELECT " : "select ");

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (SQLSetQuantifier.UNIQUE == x.getDistionOption()) {
            print0(ucase ? "UNIQUE " : "unique ");
        }

        printSelectList(x.getSelectList());

        SQLExprTableSource into = x.getInto();
        if (into != null) {
            println();
            print0(ucase ? "INTO " : "into ");
            printTableSource(into);
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            printTableSource(from);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            printExpr(where);
        }

        SQLSelectGroupByClause groupBy = x.getGroupBy();
        if (groupBy != null) {
            println();
            visit(groupBy);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            visit(orderBy);
        }

        printFetchFirst(x);

        return false;
    }

    @Override
    public boolean visit(SAPHanaInsertStatement x) {
        print0(ucase ? "INSERT " : "insert ");

        print0(ucase ? "INTO " : "into ");

        x.getTableSource().accept(this);

        printInsertColumns(x.getColumns());

        if (!x.getValuesList().isEmpty()) {
            println();
            print0(ucase ? "VALUES " : "values ");
            for (int i = 0, size = x.getValuesList().size(); i < size; ++i) {
                if (i != 0) {
                    print(',');
                    println();
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
    public boolean visit(SAPHanaUpdateStatement x) {
        print0(ucase ? "UPDATE " : "update ");

        printTableSource(x.getTableSource());

        println();
        print0(ucase ? "SET " : "set ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            SQLUpdateSetItem item = x.getItems().get(i);
            visit(item);
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            printTableSource(from);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            indentCount++;
            print0(ucase ? "WHERE " : "where ");
            printExpr(where);
            indentCount--;
        }

        return false;
    }
}
