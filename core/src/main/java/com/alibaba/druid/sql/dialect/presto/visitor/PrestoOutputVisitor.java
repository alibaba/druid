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
package com.alibaba.druid.sql.dialect.presto.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLDecimalExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoAlterFunctionStatement;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoAlterSchemaStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.math.BigDecimal;
import java.util.List;

/**
 * presto 的输出的视图信息
 *
 * @author zhangcanlong
 * @since 2022-01-07
 */
public class PrestoOutputVisitor extends SQLASTOutputVisitor implements PrestoVisitor {
    {
        dbType = DbType.presto;
    }

    public PrestoOutputVisitor(StringBuilder appender) {
        super(appender);
    }

    public PrestoOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLLimit x) {
        if (x.getOffset() != null) {
            this.print0(this.ucase ? " OFFSET " : " offset ");
            x.getOffset().accept(this);
        }
        this.print0(this.ucase ? " LIMIT " : " limit ");
        x.getRowCount().accept(this);
        return false;
    }

    public boolean visit(SQLDecimalExpr x) {
        BigDecimal value = x.getValue();
        print0(ucase ? "DECIMAL '" : "decimal '");
        print(value.toString());
        print('\'');

        return false;
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        /*
            https://prestodb.io/docs/current/sql/create-table.html
            CREATE TABLE [ IF NOT EXISTS ]
            table_name (
              { column_name data_type [ COMMENT comment ] [ WITH ( property_name = expression [, ...] ) ]
              | LIKE existing_table_name [ { INCLUDING | EXCLUDING } PROPERTIES ] }
              [, ...]
            )
            [ COMMENT table_comment ]
            [ WITH ( property_name = expression [, ...] ) ]

            https://prestodb.io/docs/current/sql/create-table-as.html
            CREATE TABLE [ IF NOT EXISTS ] table_name [ ( column_alias, ... ) ]
            [ COMMENT table_comment ]
            [ WITH ( property_name = expression [, ...] ) ]
            AS query
            [ WITH [ NO ] DATA ]
         */

        printCreateTable(x, false);

        List<SQLAssignItem> options = x.getTableOptions();
        if (options.size() > 0) {
            println();
            print0(ucase ? "WITH (" : "with (");
            printAndAccept(options, ", ");
            print(')');
        }

        SQLSelect select = x.getSelect();
        if (select != null) {
            println();
            print0(ucase ? "AS" : "as");

            println();
            visit(select);
        }
        return false;
    }

    @Override
    public boolean visit(PrestoAlterFunctionStatement x) {
        print0(ucase ? "ALTER FUNCTION " : "alter function ");
        x.getName().accept(this);

        if (x.isCalledOnNullInput()) {
            print0(" CALLED ON NULL INPUT");
        }

        if (x.isReturnsNullOnNullInput()) {
            print0(" RETURNS NULL ON NULL INPUT");
        }
        return false;
    }

    @Override
    public boolean visit(PrestoAlterSchemaStatement x) {
        print0(ucase ? "ALTER SCHEMA " : "alter achema ");
        x.getSchemaName().accept(this);

        print0(ucase ? " RENAME TO " : " rename to ");
        x.getNewName().accept(this);
        return false;
    }

    @Override
    protected void printTableOptionsPrefix(SQLCreateTableStatement x) {
        println();
        print0(ucase ? "WITH (" : "with (");
        incrementIndent();
        println();
    }

    @Override
    public boolean visit(SQLArrayExpr x) {
        print0(ucase ? "ARRAY[" : "array[");
        printAndAccept(x.getValues(), ", ");
        print(']');
        return false;
    }
}
