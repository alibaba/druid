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
import com.alibaba.druid.sql.ast.statement.SQLBlockStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelect;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerCommitStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerDeclareStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement.SQLServerParameter;
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
        print0(ucase ? "SELECT " : "select ");

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (SQLSetQuantifier.UNIQUE == x.getDistionOption()) {
            print0(ucase ? "UNIQUE " : "unique ");
        }

        if (x.getTop() != null) {
            x.getTop().accept(this);
            print(' ');
        }

        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            println();
            print0(ucase ? "INTO " : "into ");
            x.getInto().accept(this);
        }

        if (x.getFrom() != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
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
        print0(ucase ? "TOP " : "top ");

        boolean paren = false;

        if (x.getParent() instanceof SQLServerUpdateStatement || x.getParent() instanceof SQLServerInsertStatement) {
            paren = true;
            print('(');
        }

        x.getExpr().accept(this);

        if (paren) {
            print(')');
        }

        if (x.isPercent()) {
            print0(ucase ? " PERCENT" : " percent");
        }

        return false;
    }

    @Override
    public void endVisit(SQLServerTop x) {

    }

    @Override
    public boolean visit(SQLServerObjectReferenceExpr x) {
        print0(x.toString());
        return false;
    }

    @Override
    public void endVisit(SQLServerObjectReferenceExpr x) {

    }

    @Override
    public boolean visit(SQLServerInsertStatement x) {
        print0(ucase ? "INSERT " : "insert ");

        if (x.getTop() != null) {
            x.getTop().setParent(x);
            x.getTop().accept(this);
            print(' ');
        }
        
        print0(ucase ? "INTO " : "into ");
        
        x.getTableSource().accept(this);

        if (x.getColumns().size() > 0) {
            incrementIndent();
            println();
            print('(');
            for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print0(", ");
                }

                x.getColumns().get(i).accept(this);
            }
            print(')');
            decrementIndent();
        }
        
        if (x.getOutput() != null) {
            println();
            x.getOutput().setParent(x);
            x.getOutput().accept(this);
        }

        if (x.getValuesList().size() != 0) {
            println();
            print0(ucase ? "VALUES" : "values");
            println();
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

        if (x.isDefaultValues()) {
            print0(ucase ? " DEFAULT VALUES" : " default values");
        }
        return false;
    }

    @Override
    public void endVisit(SQLServerInsertStatement x) {

    }

    @Override
    public boolean visit(SQLServerUpdateStatement x) {
        print0(ucase ? "UPDATE " : "update ");

        if (x.getTop() != null) {
            x.getTop().setParent(x);
            x.getTop().accept(this);
            print(' ');
        }

        x.getTableSource().accept(this);

        println();
        print0(ucase ? "SET " : "set ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
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
            print0(ucase ? "FROM " : "from ");
            x.getFrom().setParent(x);
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
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

        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }

        if (x.getHints() != null && x.getHints().size() > 0) {
            print0(ucase ? " WITH (" : " with (");
            printAndAccept(x.getHints(), ", ");
            print(')');
        }

        return false;
    }

    @Override
    public boolean visit(SQLColumnDefinition x) {
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
                print0(ucase ? " ENABLE" : " enable");
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLServerExecStatement x) {
        print0(ucase ? "EXEC " : "exec ");
        
        SQLName returnStatus = x.getReturnStatus();
        if (returnStatus != null) {
            returnStatus.accept(this);
            print0(" = ");
        }
        
        SQLName moduleName = x.getModuleName();
        if (moduleName != null) {
            moduleName.accept(this);
            print(' ');
        } else {
            print0(" (");
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
        print0(ucase ? "SET TRANSACTION ISOLATION LEVEL " : "set transaction isolation level ");
        print0(x.getLevel());
        return false;
    }

    @Override
    public void endVisit(SQLServerSetTransactionIsolationLevelStatement x) {

    }

    @Override
    public boolean visit(SQLServerSetStatement x) {
        print0(ucase ? "SET " : "set ");
        SQLAssignItem item = x.getItem();
        item.getTarget().accept(this);
        print(' ');
        item.getValue().accept(this);
        return false;
    }

    @Override
    public void endVisit(SQLServerSetStatement x) {

    }

    @Override
    public boolean visit(SQLServerOutput x) {
        print0(ucase ? "OUTPUT " : "output ");
        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            incrementIndent();
            println();
            print0(ucase ? "INTO " : "into ");
            x.getInto().accept(this);

            if (x.getColumns().size() > 0) {
                incrementIndent();
                println();
                print('(');
                for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
                    if (i != 0) {
                        if (i % 5 == 0) {
                            println();
                        }
                        print0(", ");
                    }

                    x.getColumns().get(i).accept(this);
                }
                print(')');
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
    public boolean visit(SQLServerDeclareStatement x) {
        print0(ucase ? "DECLARE " : "declare ");
        this.printAndAccept(x.getItems(), ", ");
        return false;
    }

    @Override
    public void endVisit(SQLServerDeclareStatement x) {

    }

    @Override
    public boolean visit(SQLBlockStatement x) {
        print0(ucase ? "BEGIN" : "begin");
        incrementIndent();
        println();
        for (int i = 0, size = x.getStatementList().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement stmt = x.getStatementList().get(i);
            stmt.setParent(x);
            stmt.accept(this);
            print(';');
        }
        decrementIndent();
        println();
        print0(ucase ? "END" : "end");
        return false;
    }

    @Override
    protected void printGrantOn(SQLGrantStatement x) {
        if (x.getOn() != null) {
            print0(ucase ? " ON " : " on ");

            if (x.getObjectType() != null) {
                print0(x.getObjectType().name());
                print0("::");
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
            print0(ucase ? "FOR BROWSE" : "for browse");
        }
        
        if (x.getForXmlOptions().size() > 0) {
            println();
            print0(ucase ? "FOR XML " : "for xml ");
            for (int i = 0; i < x.getForXmlOptions().size(); ++i) {
                if (i != 0) {
                    print0(", ");
                    print0(x.getForXmlOptions().get(i));
                }
            }
        }
        
        if (x.getXmlPath() != null) {
            println();
            print0(ucase ? "FOR XML " : "for xml ");
            x.getXmlPath().accept(this);
        }
        
        if (x.getOffset() != null) {
            println();
            print0(ucase ? "OFFSET " : "offset ");
            x.getOffset().accept(this);
            print0(ucase ? " ROWS" : " rows");
            
            if (x.getRowCount() != null) {
                print0(ucase ? " FETCH NEXT " : " fetch next ");
                x.getRowCount().accept(this);
                print0(ucase ? " ROWS ONLY" : " rows only");
            }
        }
        return false;
    }

    @Override
    public boolean visit(SQLServerCommitStatement x) {
        print0(ucase ? "COMMIT" : "commit");

        if (x.isWork()) {
            print0(ucase ? " WORK" : " work");
        } else {
            print0(ucase ? " TRANSACTION" : " transaction");
            if (x.getTransactionName() != null) {
                print(' ');
                x.getTransactionName().accept(this);
            }
            if (x.getDelayedDurability() != null) {
                print0(ucase ? " WITH ( DELAYED_DURABILITY = " : " with ( delayed_durability = ");
                x.getDelayedDurability().accept(this);
                print0(" )");
            }
        }

        return false;
    }

    @Override
    public void endVisit(SQLServerCommitStatement x) {
        
    }

    @Override
    public boolean visit(SQLServerRollbackStatement x) {
        print0(ucase ? "ROLLBACK" : "rollback");

        if (x.isWork()) {
            print0(ucase ? " WORK" : " work");
        } else {
            print0(ucase ? " TRANSACTION" : " transaction");
            if (x.getName() != null) {
                print(' ');
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
        print0(ucase ? "WAITFOR" : "waitfor");

        if (x.getDelay() != null) {
            print0(ucase ? " DELAY " : " delay ");
            x.getDelay().accept(this);
        } else if (x.getTime() != null) {
            print0(ucase ? " TIME " : " time ");
            x.getTime().accept(this);
        } if (x.getStatement() != null) {
            print0(ucase ? " DELAY " : " delay ");
            x.getStatement().accept(this);
        }
        
        if(x.getTimeout() != null) {
            print0(ucase ? " ,TIMEOUT " : " ,timeout ");
            x.getTimeout().accept(this);
        }
        
        return false;
    }

    @Override
    public void endVisit(SQLServerWaitForStatement x) {
        
    }

	@Override
	public boolean visit(SQLServerParameter x) {
		// TODO Auto-generated method stub
		x.getExpr().accept(this);
		if(x.getType())
		{
			print0(ucase ? " OUT" : " out");
		}
		return false;
	}

	@Override
	public void endVisit(SQLServerParameter x) {
		// TODO Auto-generated method stub
		
	}
}
