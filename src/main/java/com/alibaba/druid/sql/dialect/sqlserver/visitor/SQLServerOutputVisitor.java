/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLSequenceExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement.SQLServerParameter;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerRollbackStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerSetTransactionIsolationLevelStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerWaitForStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class SQLServerOutputVisitor extends SQLASTOutputVisitor implements SQLServerASTVisitor {

    public SQLServerOutputVisitor(Appendable appender){
        super(appender, JdbcConstants.SQL_SERVER);
    }

    public SQLServerOutputVisitor(Appendable appender, boolean parameterized){
        super(appender, parameterized);
        this.dbType = JdbcConstants.SQL_SERVER;
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

        SQLServerTop top = x.getTop();
        if (top != null) {
            visit(top);
            print(' ');
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
    public void endVisit(SQLServerSelectQueryBlock x) {

    }

    @Override
    public boolean visit(SQLServerTop x) {
        boolean parameterized = this.parameterized;
        this.parameterized = false;

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

        this.parameterized = parameterized;
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

        printInsertColumns(x.getColumns());
        
        if (x.getOutput() != null) {
            println();
            x.getOutput().setParent(x);
            x.getOutput().accept(this);
        }

        if (x.getValuesList().size() != 0) {
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

        SQLServerTop top = x.getTop();
        if (top != null) {
            top.accept(this);
            print(' ');
        }

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

        SQLServerOutput output = x.getOutput();
        if (output != null) {
            println();
            visit(output);
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

    @Override
    public void endVisit(SQLServerUpdateStatement x) {

    }

    public boolean visit(SQLExprTableSource x) {
        printTableSourceExpr(x.getExpr());

        String alias = x.getAlias();
        if (alias != null) {
            print(' ');
            print0(alias);
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
        boolean parameterized = this.parameterized;
        this.parameterized = false;

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

        this.parameterized = parameterized;
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
    public boolean visit(SQLSetStatement x) {
        print0(ucase ? "SET " : "set ");

        SQLSetStatement.Option option = x.getOption();
        if (option != null) {
            print(option.name());
            print(' ');
        }

        List<SQLAssignItem> items = x.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (i != 0) {
                print0(", ");
            }

            SQLAssignItem item = x.getItems().get(i);
            item.getTarget().accept(this);

            SQLExpr value = item.getValue();
            if (value instanceof SQLIdentifierExpr
                    && (((SQLIdentifierExpr) value).nameHashCode64() == FnvHash.Constants.ON
                        || ((SQLIdentifierExpr) value).nameHashCode64() == FnvHash.Constants.OFF)) {
                print(' ');
            } else {
                print0(" = ");
            }
            value.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLServerOutput x) {
        print0(ucase ? "OUTPUT " : "output ");
        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            this.indentCount++;
            println();
            print0(ucase ? "INTO " : "into ");
            x.getInto().accept(this);

            if (x.getColumns().size() > 0) {
                this.indentCount++;
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
                this.indentCount--;
            }
        }
        this.indentCount--;
        return false;
    }

    @Override
    public void endVisit(SQLServerOutput x) {

    }

    @Override
    public boolean visit(SQLBlockStatement x) {
        print0(ucase ? "BEGIN" : "begin");
        this.indentCount++;
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
        this.indentCount--;
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
    
    public boolean visit(SQLSelect x) {
        super.visit(x);
        if (x.isForBrowse()) {
            println();
            print0(ucase ? "FOR BROWSE" : "for browse");
        }
        
        if (x.getForXmlOptionsSize() > 0) {
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
    public boolean visit(SQLCommitStatement x) {
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

		
	}

    @Override
    public boolean visit(SQLStartTransactionStatement x) {
        print0(ucase ? "BEGIN TRANSACTION" : "begin transaction");
        if (x.getName() != null) {
            print(' ');
            x.getName().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLScriptCommitStatement x) {
        print0(ucase ? "GO" : "go");
        return false;
    }

    @Override
    public boolean visit(SQLCreateUserStatement x) {
        print0(ucase ? "CREATE USER " : "create user ");
        x.getUser().accept(this);
        print0(ucase ? " WITH PASSWORD = " : " with password = ");

        SQLExpr passoword = x.getPassword();

        if (passoword instanceof SQLIdentifierExpr) {
            print('\'');
            passoword.accept(this);
            print('\'');
        } else {
            passoword.accept(this);
        }

        return false;
    }

    public boolean visit(SQLSequenceExpr x) {
        SQLSequenceExpr.Function function = x.getFunction();
        switch (function) {
            case NextVal:
                print0(ucase ? "NEXT VALUE FOR " : "next value for ");
                break;
            default:
                throw new ParserException("not support function : " + function);
        }
        printExpr(x.getSequence());
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        boolean odps = isOdps();
        print0(ucase ? "ADD " : "add ");
        printAndAccept(x.getColumns(), ", ");
        return false;
    }
}
