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
package com.alibaba.druid.sql.dialect.oscar.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.*;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oscar.ast.stmt.*;

import java.util.List;

public class OscarPermissionOutputVisitor extends OscarOutputVisitor {
    public OscarPermissionOutputVisitor(StringBuilder appender) {
        super(appender);
        this.dbType = DbType.oscar;
    }

    public OscarPermissionOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        this.dbType = DbType.oscar;
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        String catalog = x.getCatalog();
        String schema = x.getSchema();
        String tableName = x.getTableName();
        List<SQLName> columns1 = x.getColumns();
        int sourceColumn = x.getSourceColumn();

        //SQLExpr sqlExpr = SQLUtils.toSQLExpr("id=3", dbType);

        print(" (");
        print0(ucase ? "SELECT " : "select ");
        print('*');
        print0(ucase ? " FROM " : " from ");
        printTableSourceExpr(x.getExpr());
        //print0(ucase ? " WHERE " : " where ");
        //printExpr(sqlExpr);
        print(')');

        final SQLTableSampling sampling = x.getSampling();
        if (sampling != null) {
            print(' ');
            sampling.accept(this);
        }

        String alias = x.getAlias();
        List<SQLName> columns = x.getColumnsDirect();
        if (alias != null) {
            print(' ');
            if (columns != null && columns.size() > 0) {
                print0(ucase ? " AS " : " as ");
            }
            print0(alias);
        }

        if (columns != null && columns.size() > 0) {
            print(" (");
            printAndAccept(columns, ", ");
            print(')');
        }

        for (int i = 0; i < x.getHintsSize(); ++i) {
            print(' ');
            x.getHints().get(i).accept(this);
        }

        if (x.getPartitionSize() > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printlnAndAccept(x.getPartitions(), ", ");
            print(')');
        }

        return false;
    }
}
