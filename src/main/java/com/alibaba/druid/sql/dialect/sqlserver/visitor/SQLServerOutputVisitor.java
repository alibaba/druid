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
package com.alibaba.druid.sql.dialect.sqlserver.visitor;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerColumnDefinition;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerColumnDefinition.Identity;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerDeclareItem;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelect;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerBlockStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerCommitStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerDeclareStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerIfStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerIfStatement.Else;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerRollbackStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerSetStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerSetTransactionIsolationLevelStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerWaitForStatement;
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
            print(' ');
        }

        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            println();
            print("INTO ");
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

        return false;
    }

    @Override
    public void endVisit(SQLServerSelectQueryBlock x) {

    }

    @Override
    public boolean visit(SQLServerTop x) {
        print("TOP ");

        boolean paren = false;

        if (x.getParent() instanceof SQLServerUpdateStatement || x.getParent() instanceof SQLServerInsertStatement) {
            paren = true;
            print("(");
        }

        x.getExpr().accept(this);

        if (paren) {
            print(")");
        }

        if (x.isPercent()) {
            print(" PERCENT");
        }

        return false;
    }

    @Override
    public void endVisit(SQLServerTop x) {

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
        print("INSERT ");

        if (x.getTop() != null) {
            x.getTop().setParent(x);
            x.getTop().accept(this);
            print(' ');
        }
        
        print("INTO ");
        
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
        
        if (x.getOutput() != null) {
            println();
            x.getOutput().setParent(x);
            x.getOutput().accept(this);
        }

        if (x.getValuesList().size() != 0) {
            println();
            print("VALUES");
            println();
            for (int i = 0, size = x.getValuesList().size(); i < size; ++i) {
                if (i != 0) {
                    print(",");
                    println();
                }
                x.getValuesList().get(i).accept(this);
            }
        }

        if (x.getQuery() != null) {
            println();
            x.getQuery().accept(this);
        }

        if (x.isDefaultValues()) {
            print(" DEFAULT VALUES");
        }
        return false;
    }

    @Override
    public void endVisit(SQLServerInsertStatement x) {

    }

    @Override
    public boolean visit(SQLServerUpdateStatement x) {
        print("UPDATE ");

        if (x.getTop() != null) {
            x.getTop().setParent(x);
            x.getTop().accept(this);
            print(' ');
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
        
        if (x.getOutput() != null) {
            println();
            x.getOutput().setParent(x);
            x.getOutput().accept(this);
        }

        if (x.getFrom() != null) {
            println();
            print("FROM ");
            x.getFrom().setParent(x);
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print("WHERE ");
            incrementIndent();
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
        }

        return false;
    }

    @Override
    public void endVisit(SQLServerUpdateStatement x) {

    }

    public boolean visit(SQLExprTableSource x) {
        x.getExpr().accept(this);

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(" WITH (");
            printAndAccept(x.getHints(), ", ");
            print(")");
        }

        if (x.getAlias() != null) {
            print(' ');
            print(x.getAlias());
        }

        return false;
    }

    @Override
    public boolean visit(Identity x) {
        print("IDENTITY (");
        print(x.getSeed());
        print(", ");
        print(x.getIncrement());
        print(")");
        return false;
    }

    @Override
    public void endVisit(Identity x) {

    }

    @Override
    public boolean visit(SQLServerColumnDefinition x) {
        x.getName().accept(this);

        if (x.getDataType() != null) {
            print(' ');
            x.getDataType().accept(this);
        }

        if (x.getDefaultExpr() != null) {
            visitColumnDefault(x);
        }

        for (SQLColumnConstraint item : x.getConstraints()) {
            print(' ');
            item.accept(this);
        }

        if (x.getIdentity() != null) {
            print(' ');
            x.getIdentity().accept(this);
        }

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print(" ENABLE");
            }
        }

        return false;
    }

    @Override
    public void endVisit(SQLServerColumnDefinition x) {

    }

    public boolean visit(SQLColumnDefinition x) {
        if (x instanceof SQLServerColumnDefinition) {
            return visit((SQLServerColumnDefinition) x);
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLServerExecStatement x) {
        print("EXEC ");
        
        SQLName returnStatus = x.getReturnStatus();
        if (returnStatus != null) {
            returnStatus.accept(this);
            print(" = ");
        }
        
        SQLName moduleName = x.getModuleName();
        if (moduleName != null) {
            moduleName.accept(this);
            print(' ');
        } else {
            print(" (");
        }
        printAndAccept(x.getParameters(), ", ");

        if (moduleName == null) {
            print(')');
        }
        return false;
    }

    @Override
    public void endVisit(SQLServerExecStatement x) {

    }

    @Override
    public boolean visit(SQLServerSetTransactionIsolationLevelStatement x) {
        print("SET TRANSACTION ISOLATION LEVEL ");
        print(x.getLevel());
        return false;
    }

    @Override
    public void endVisit(SQLServerSetTransactionIsolationLevelStatement x) {

    }

    @Override
    public boolean visit(SQLServerSetStatement x) {
        print("SET ");
        SQLAssignItem item = x.getItem();
        item.getTarget().accept(this);
        print(" ");
        item.getValue().accept(this);
        return false;
    }

    @Override
    public void endVisit(SQLServerSetStatement x) {

    }

    @Override
    public boolean visit(SQLServerOutput x) {
        print("OUTPUT ");
        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            incrementIndent();
            println();
            print("INTO ");
            x.getInto().accept(this);

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
        }
        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(SQLServerOutput x) {

    }

    @Override
    public boolean visit(SQLServerDeclareItem x) {
        x.getName().accept(this);
        
        if(x.getType() == SQLServerDeclareItem.Type.TABLE) {
            print(" TABLE");
            int size = x.getTableElementList().size();

            if (size > 0) {
                print(" (");
                incrementIndent();
                println();
                for (int i = 0; i < size; ++i) {
                    if (i != 0) {
                        print(",");
                        println();
                    }
                    x.getTableElementList().get(i).accept(this);
                }
                decrementIndent();
                println();
                print(")");
            }
        } else if (x.getType() == SQLServerDeclareItem.Type.CURSOR) {
            print(" CURSOR");
        } else {
            print(" ");
            x.getDataType().accept(this);
            if (x.getValue() != null) {
                print(" = ");
                x.getValue().accept(this);
            }
        }
        
        return false;
    }

    @Override
    public void endVisit(SQLServerDeclareItem x) {
        
    }

    @Override
    public boolean visit(SQLServerDeclareStatement x) {
        print("DECLARE ");
        this.printAndAccept(x.getItems(), ", ");
        return false;
    }

    @Override
    public void endVisit(SQLServerDeclareStatement x) {

    }

    @Override
    public boolean visit(Else x) {
        print("ELSE");
        incrementIndent();
        println();

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement item = x.getStatements().get(i);
            item.setParent(x);
            item.accept(this);
        }

        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(Else x) {

    }

    @Override
    public boolean visit(SQLServerIfStatement x) {
        print("IF ");
        x.getCondition().accept(this);
        incrementIndent();
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.setParent(x);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        decrementIndent();

        if (x.getElseItem() != null) {
            println();
            x.getElseItem().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(SQLServerIfStatement x) {

    }

    @Override
    public boolean visit(SQLServerBlockStatement x) {
        print("BEGIN");
        incrementIndent();
        println();
        for (int i = 0, size = x.getStatementList().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement stmt = x.getStatementList().get(i);
            stmt.setParent(x);
            stmt.accept(this);
            print(";");
        }
        decrementIndent();
        println();
        print("END");
        return false;
    }

    @Override
    public void endVisit(SQLServerBlockStatement x) {

    }
    
    @Override
    protected void printGrantOn(SQLGrantStatement x) {
        if (x.getOn() != null) {
            print(" ON ");

            if (x.getObjectType() != null) {
                print(x.getObjectType().name());
                print("::");
            }

            x.getOn().accept(this);
        }
    }
    
    @Override
    public void endVisit(SQLServerSelect x) {
        
    }

    @Override
    public boolean visit(SQLServerSelect x) {
        super.visit(x);
        if (x.isForBrowse()) {
            println();
            print("FOR BROWSE");
        }
        
        if (x.getForXmlOptions().size() > 0) {
            println();
            print("FOR XML ");
            for (int i = 0; i < x.getForXmlOptions().size(); ++i) {
                if (i != 0) {
                    print(", ");
                    print(x.getForXmlOptions().get(i));
                }
            }
        }
        
        if (x.getOffset() != null) {
            println();
            print("OFFSET ");
            x.getOffset().accept(this);
            print(" ROWS");
            
            if (x.getRowCount() != null) {
                print(" FETCH NEXT ");
                x.getRowCount().accept(this);
                print(" ROWS ONLY");
            }
        }
        return false;
    }

    @Override
    public boolean visit(SQLServerCommitStatement x) {
        print("COMMIT");

        if (x.isWork()) {
            print(" WORK");
        } else {
            print(" TRANSACTION");
            if (x.getTransactionName() != null) {
                print(" ");
                x.getTransactionName().accept(this);
            }
            if (x.getDelayedDurability() != null) {
                print(" WITH ( DELAYED_DURABILITY = ");
                x.getDelayedDurability().accept(this);
                print(" )");
            }
        }

        return false;
    }

    @Override
    public void endVisit(SQLServerCommitStatement x) {
        
    }

    @Override
    public boolean visit(SQLServerRollbackStatement x) {
        print("ROLLBACK");

        if (x.isWork()) {
            print(" WORK");
        } else {
            print(" TRANSACTION");
            if (x.getName() != null) {
                print(" ");
                x.getName().accept(this);
            }
        }
        
        return false;
    }

    @Override
    public void endVisit(SQLServerRollbackStatement x) {
        
    }

    @Override
    public boolean visit(SQLServerWaitForStatement x) {
        print("WAITFOR");

        if (x.getDelay() != null) {
            print(" DELAY ");
            x.getDelay().accept(this);
        } else if (x.getTime() != null) {
            print(" TIME ");
            x.getTime().accept(this);
        } if (x.getStatement() != null) {
            print(" DELAY ");
            x.getStatement().accept(this);
        }
        
        if(x.getTimeout() != null) {
            print(" ,TIMEOUT ");
            x.getTimeout().accept(this);
        }
        
        return false;
    }

    @Override
    public void endVisit(SQLServerWaitForStatement x) {
        
    }
}
