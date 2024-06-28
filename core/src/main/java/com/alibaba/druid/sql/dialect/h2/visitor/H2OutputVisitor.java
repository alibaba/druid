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
package com.alibaba.druid.sql.dialect.h2.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.util.List;

public class H2OutputVisitor extends SQLASTOutputVisitor implements H2ASTVisitor {
    private static final Log LOG = LogFactory.getLog(H2OutputVisitor.class);

    public H2OutputVisitor(StringBuilder appender) {
        super(appender, DbType.h2);
    }

    public H2OutputVisitor(StringBuilder appender, DbType dbType) {
        super(appender, dbType);
    }

    public H2OutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
        dbType = DbType.h2;
    }

    public boolean visit(SQLReplaceStatement x) {
        print0(ucase ? "MERGE INTO " : "merge into ");

        printTableSourceExpr(x.getTableName());

        List<SQLExpr> columns = x.getColumns();
        if (columns.size() > 0) {
            print0(ucase ? " KEY (" : " key (");
            for (int i = 0, size = columns.size(); i < size; ++i) {
                if (i != 0) {
                    print0(", ");
                }

                SQLExpr columnn = columns.get(i);
                printExpr(columnn, parameterized);
            }
            print(')');
        }

        List<SQLInsertStatement.ValuesClause> valuesClauseList = x.getValuesList();
        if (!valuesClauseList.isEmpty()) {
            println();
            print0(ucase ? "VALUES " : "values ");
            int size = valuesClauseList.size();
            if (size == 0) {
                print0("()");
            } else {
                for (int i = 0; i < size; ++i) {
                    if (i != 0) {
                        print0(", ");
                    }
                    visit(valuesClauseList.get(i));
                }
            }
        }

        SQLQueryExpr query = x.getQuery();
        if (query != null) {
            visit(query);
        }

        return false;
    }

    @Override
    public boolean visit(SQLCreateDatabaseStatement x) {
        /*
        https://h2database.com/html/commands.html#create_schema
        CREATE SCHEMA [ IF NOT EXISTS ]
        { name [ AUTHORIZATION ownerName ] | [ AUTHORIZATION ownerName ] }
        [ WITH tableEngineParamName [,...] ]
         */

        printUcase("CREATE SCHEMA ");

        if (x.isIfNotExists()) {
            printUcase("IF NOT EXISTS ");
        }
        x.getName().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLCreateTableStatement x) {
        /*
        https://h2database.com/html/commands.html#create_table
        CREATE [ CACHED | MEMORY ] [ { TEMP } | [ GLOBAL | LOCAL ] TEMPORARY ]
        TABLE [ IF NOT EXISTS ] [schemaName.]tableName
        [ ( { columnName [columnDefinition] | tableConstraintDefinition } [,...] ) ]
        [ ENGINE tableEngineName ]
        [ WITH tableEngineParamName [,...] ]
        [ NOT PERSISTENT ] [ TRANSACTIONAL ]
        [ AS query [ WITH [ NO ] DATA ] ]
         */

        printCreateTable(x, true);
        return false;
    }

    protected void printTableElements(List<SQLTableElement> tableElementList) {
        int size = tableElementList.size();
        if (size == 0) {
            return;
        }

        print0(" (");

        this.indentCount++;
        println();
        for (int i = 0; i < size; ++i) {
            SQLTableElement element = tableElementList.get(i);
            if (element instanceof SQLPrimaryKey) {
                SQLName name = ((SQLPrimaryKey) element).getName();
                if (name != null) {
                    printUcase("CONSTRAINT ");
                    name.accept(this);
                    print(' ');
                }
                printUcase("PRIMARY KEY ");
                acceptChildName(((SQLPrimaryKey) element).getColumns());
            } else if (element instanceof SQLUnique) {
                if ("UNIQUE".equalsIgnoreCase(((SQLUnique) element).getIndexDefinition().getType())) {
                    printUcase("UNIQUE ");
                }
                printUcase("KEY ");
                print(addIndexNameSuffixForH2(((SQLUnique) element).getName()));
                acceptChildName(((SQLUnique) element).getColumns());
            } else if (element instanceof MySqlTableIndex) {
                visit((MySqlTableIndex) element);
            } else {
                element.accept(this);
            }

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
        this.indentCount--;
        println();
        print(')');
    }

    private String addIndexNameSuffixForH2(SQLName name) {
        String simpleName = name.getSimpleName();
        if (simpleName.endsWith("`")) {
            return simpleName.replaceFirst("`$", System.nanoTime() + "`");
        } else {
            return simpleName + System.nanoTime();
        }
    }

    public boolean visit(MySqlTableIndex x) {
        print0(ucase ? "INDEX" : "index");
        if (x.getName() != null) {
            print(' ');
            x.getName().accept(this);
        }

        print('(');
        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(')');

        return false;
    }

    private void acceptChildName(List<SQLSelectOrderByItem> children) {
        if (children == null) {
            return;
        }

        print('(');
        for (int i = 0; i < children.size(); i++) {
            SQLSelectOrderByItem child = children.get(i);
            if (child == null) {
                continue;
            }
            if (child.getExpr() instanceof SQLMethodInvokeExpr) {
                print0(((SQLMethodInvokeExpr) child.getExpr()).getMethodName());
            } else {
                print0(child.getExpr().toString());
            }
            if (i != children.size() - 1) {
                print(',');
            }
        }
        print(')');
    }

    @Override
    public boolean visit(SQLCreateIndexStatement x) {
        /* h2 CREATE INDEX BNF: https://h2database.com/html/commands.html#create_index
        CREATE [ UNIQUE | SPATIAL ] INDEX
        [ [ IF NOT EXISTS ] [schemaName.]indexName ]
        ON [schemaName.]tableName ( indexColumn [,...] )
        [ INCLUDE ( indexColumn [,...] ) ]
         */

        printUcase("CREATE ");

        String type = x.getType();
        if ("UNIQUE".equalsIgnoreCase(type) || "SPATIAL".equalsIgnoreCase(type)) {
            printUcase(type + ' ');
        }

        printUcase("INDEX ");

        if (x.isIfNotExists()) {
            printUcase("IF NOT EXISTS ");
        }

        x.getName().accept(this);

        printUcase(" ON ");
        x.getTable().accept(this);
        print0(" (");
        printAndAccept(x.getItems(), ", ");
        print(')');

        return false;
    }

    @Override
    public boolean visit(SQLSetStatement x) {
        return false;
    }

    @Override
    public boolean visit(SQLHexExpr x) {
        print0("X'");
        print0(x.getHex());
        print0("'");
        return false;
    }

    @Override
    public boolean visit(SQLBinaryExpr x) {
        print0(x.getText());
        return false;
    }

    @Override
    protected void printChars(String text) {
        if ("0000-00-00 00:00:00".equals(text)) {
            // TODO: this is not correct because '0000-00-00 00:00:00' could be a real string value
            LOG.warn("Replacing '0000-00-00 00:00:00' with valid H2 datetime (unsafe replacement)");
            text = "0001-01-01 00:00:00";
        }
        super.printChars(text);
    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        /*
        https://h2database.com/html/commands.html#alter_table_alter_column
        ALTER TABLE [ IF EXISTS ] [schemaName.]tableName
        ALTER COLUMN [ IF EXISTS ] columnName
        { { columnDefinition }
        | { RENAME TO name }
        | SET GENERATED { ALWAYS | BY DEFAULT } [ alterIdentityColumnOption [...] ]
        | alterIdentityColumnOption [...]
        | DROP IDENTITY
        | { SELECTIVITY int }
        | { SET DEFAULT expression }
        | { DROP DEFAULT }
        | DROP EXPRESSION
        | { SET ON UPDATE expression }
        | { DROP ON UPDATE }
        | { SET DEFAULT ON NULL }
        | { DROP DEFAULT ON NULL }
        | { SET NOT NULL }
        | { DROP NOT NULL } | { SET NULL }
        | { SET DATA TYPE dataTypeOrDomain [ USING newValueExpression ] }
        | { SET { VISIBLE | INVISIBLE } } }
         */

        printAlterTable(x);
        this.indentCount++;
        for (int i = 0; i < x.getItems().size(); ++i) {
            SQLAlterTableItem item = x.getItems().get(i);
            println();
            accept(item);
            if (i + 1 < x.getItems().size()) {
                SQLAlterTableItem later = x.getItems().get(i + 1);
                if (later instanceof MySqlAlterTableModifyColumn) {
                    print(";\n");
                    printAlterTable(x);
                }
            }
        }
        this.indentCount--;

        return false;
    }

    private void printAlterTable(SQLAlterTableStatement x) {
        printUcase("ALTER TABLE ");
        if (x.isIfExists()) {
            printUcase("IF EXISTS ");
        }
        printTableSourceExpr(x.getName());
    }

    private void accept(SQLAlterTableItem item) {
        if (item instanceof MySqlAlterTableModifyColumn) {
            MySqlAlterTableModifyColumn x = (MySqlAlterTableModifyColumn) item;
            printUcase("ALTER COLUMN ");
            x.getNewColumnDefinition().accept(this);
        } else {
            item.accept(this);
        }
    }

    public boolean visit(SQLAlterTableAddConstraint x) {
        if (x.isWithNoCheck()) {
            this.print0(this.ucase ? "WITH NOCHECK " : "with nocheck ");
        }

        this.print0(this.ucase ? "ADD " : "add ");

        if (x.getConstraint().getParent() instanceof SQLAlterTableAddConstraint && x.getConstraint() instanceof SQLForeignKeyImpl) {
            this.visit((SQLForeignKeyImpl) x.getConstraint());
        } else if (x.getConstraint().getParent() instanceof SQLAlterTableAddConstraint && x.getConstraint() instanceof SQLUnique) {
            this.visit((SQLUnique) x.getConstraint());
        } else {
            x.getConstraint().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLUnique x) {
        SQLName name = x.getName();
        if (name != null) {
            this.print0(this.ucase ? "CONSTRAINT " : "constraint ");
            print(addIndexNameSuffixForH2(name));

            this.print(' ');
        }

        this.print0(this.ucase ? "UNIQUE (" : "unique (");
        this.printAndAccept(x.getColumns(), ", ");
        this.print(')');
        return false;
    }

}
