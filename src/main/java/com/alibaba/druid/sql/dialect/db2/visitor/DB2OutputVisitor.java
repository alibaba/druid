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
package com.alibaba.druid.sql.dialect.db2.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIntervalExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntervalUnit;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2CreateTableStatement;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2ValuesStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class DB2OutputVisitor extends SQLASTOutputVisitor implements DB2ASTVisitor {

    public DB2OutputVisitor(Appendable appender){
        super(appender, DbType.db2);
    }

    public DB2OutputVisitor(Appendable appender, boolean parameterized){
        super(appender, parameterized);
        this.dbType = DbType.db2;
    }

    @Override
    public boolean visit(DB2SelectQueryBlock x) {
        this.visit((SQLSelectQueryBlock) x);

        if (x.isForReadOnly()) {
            println();
            print0(ucase ? "FOR READ ONLY" : "for read only");
        }

        if (x.getIsolation() != null) {
            println();
            print0(ucase ? "WITH " : "with ");
            print0(x.getIsolation().name());
        }

        if (x.getOptimizeFor() != null) {
            println();
            print0(ucase ? "OPTIMIZE FOR " : "optimize for ");
            x.getOptimizeFor().accept(this);
        }

        return false;
    }


    @Override
    public void endVisit(DB2SelectQueryBlock x) {

    }

    @Override
    public boolean visit(DB2ValuesStatement x) {
        print0(ucase ? "VALUES " : "values ");
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public void endVisit(DB2ValuesStatement x) {

    }

    @Override
    public boolean visit(DB2CreateTableStatement x) {
        printCreateTable(x, true);

        if (x.isDataCaptureNone()) {
            println();
            print("DATA CAPTURE NONE");
        } else if (x.isDataCaptureChanges()) {
            println();
            print("DATA CAPTURE CHANGES");
        }

        SQLName tablespace = x.getTablespace();
        if (tablespace != null) {
            println();
            print("IN ");
            tablespace.accept(this);
        }

        SQLName indexIn = x.getIndexIn();
        if (indexIn != null) {
            println();
            print("INDEX IN ");
            indexIn.accept(this);
        }

        SQLName database = x.getDatabase();
        if (database != null) {
            println();
            print("IN DATABASE ");
            database.accept(this);
        }

        SQLName validproc = x.getValidproc();
        if (validproc != null) {
            println();
            print("VALIDPROC ");
            validproc.accept(this);
        }

        SQLPartitionBy partitionBy = x.getPartitioning();
        if (partitionBy != null) {
            println();
            print0(ucase ? "PARTITION BY " : "partition by ");
            partitionBy.accept(this);
        }

        Boolean compress = x.getCompress();
        if (compress != null) {
            println();
            if (compress.booleanValue()) {
                print0(ucase ? "COMPRESS YES" : "compress yes");
            } else {
                print0(ucase ? "COMPRESS NO" : "compress no");
            }
        }

        return false;
    }

    @Override
    public void endVisit(DB2CreateTableStatement x) {

    }

    protected void printOperator(SQLBinaryOperator operator) {
        if (operator == SQLBinaryOperator.Concat) {
            print0(ucase ? "CONCAT" : "concat");
        } else {
            print0(ucase ? operator.name : operator.name_lcase);
        }
    }

    public boolean visit(SQLIntervalExpr x) {
        SQLExpr value = x.getValue();
        value.accept(this);

        SQLIntervalUnit unit = x.getUnit();
        if (unit != null) {
            print(' ');
            print0(ucase ? unit.name : unit.name_lcase);
            print(ucase ? 'S' : 's');
        }
        return false;
    }

    public boolean visit(SQLColumnDefinition.Identity x) {
        print0(ucase ? "GENERATED ALWAYS AS IDENTITY" : "generated always as identity");

        final Integer seed = x.getSeed();
        final Integer increment = x.getIncrement();
        final Integer minValue = x.getMinValue();
        final Integer maxValue = x.getMaxValue();

        if (seed != null || increment != null || x.isCycle() || minValue != null || maxValue != null) {
            print0(" (");
        }

        if (seed != null) {
            print0(ucase ? "START WITH " : "start with ");
            print(seed);
        }

        if (increment != null) {
            if (seed != null) {
                print0(", ");
            }
            print0(ucase ? "INCREMENT BY " : "increment by ");
            print(increment);
        }

        if (x.isCycle()) {
            if (seed != null || increment != null) {
                print0(", ");
            }
            print0(ucase ? "CYCLE" : "cycle");
        }

        if (minValue != null) {
            if (seed != null || increment != null || x.isCycle()) {
                print0(", ");
            }
            print0(ucase ? "MINVALUE " : "minvalue ");
            print(minValue);
        }

        if (maxValue != null) {
            if (seed != null || increment != null || x.isCycle() || minValue != null) {
                print0(", ");
            }
            print0(ucase ? "MAXVALUE " : "maxvalue ");
            print(maxValue);
        }

        if (seed != null || increment != null || x.isCycle() || minValue != null || maxValue != null) {
            print(')');
        }

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        print0(ucase ? "ADD COLUMNS " : "add columns ");
        printAndAccept(x.getColumns(), ", ");
        return false;
    }
}
