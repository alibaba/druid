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
package com.alibaba.druid.sql.dialect.teradata.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalytic;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalyticWindowing;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataIntervalExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.stmt.TeradataSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class TeradataOutputVisitor extends SQLASTOutputVisitor implements TeradataASTVisitor {

    public TeradataOutputVisitor(Appendable appender){
        super(appender);
    }
    
    public boolean visit(TeradataAnalytic x) {
    	print0(ucase ? "OVER (" : "over (");
        
        boolean space = false;
        if (x.getPartitionBy().size() > 0) {
            print0(ucase ? "PARTITION BY " : "partition by ");
            printAndAccept(x.getPartitionBy(), ", ");

            space = true;
        }

        if (x.getOrderBy() != null) {
            if (space) {
                print(' ');
            }
            x.getOrderBy().accept(this);
            space = true;
        }

        if (x.getWindowing() != null) {
            if (space) {
                print(' ');
            }
            x.getWindowing().accept(this);
        }

        print(')');
        
        return false;
    }

	@Override
	public boolean visit(TeradataAnalyticWindowing x) {
        print0(x.getType().name().toUpperCase());
        print(' ');
        x.getExpr().accept(this);
        return false;
	}

	@Override
	public void endVisit(TeradataAnalyticWindowing x) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean visit(SQLSelectQueryBlock select) {
		if(select instanceof TeradataSelectQueryBlock) {
			return visit((TeradataSelectQueryBlock) select);
		}
		
		return super.visit(select);
	}
	
	public boolean visit(TeradataSelectQueryBlock x) {
		if (x.getOrderBy() != null) {
            x.getOrderBy().setParent(x);
        }
		
		print0(ucase ? "SELECT " : "select ");

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (SQLSetQuantifier.UNIQUE == x.getDistionOption()) {
            print0(ucase ? "UNIQUE " : "unique ");
        }

        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            println();
            print0(ucase ? "INTO " : "into ");
            x.getInto().accept(this);
        }

        println();
        print0(ucase ? "FROM " : "from ");
        if (x.getFrom() == null) {
            print0(ucase ? "DUAL" : "dual");
        } else {
            x.getFrom().setParent(x);
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
        
        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        return false;
		
	}

	public boolean visit(SQLSelect x) {
		return super.visit(x);
	}
	
    protected void visitAggreateRest(SQLAggregateExpr aggregateExpr) {
        {
            SQLOrderBy value = (SQLOrderBy) aggregateExpr.getAttribute("ORDER BY");
            if (value != null) {
                print(' ');
                ((SQLObject) value).accept(this);
            }
        }
        {
            Object value = aggregateExpr.getAttribute("SEPARATOR");
            if (value != null) {
                print0(ucase ? " SEPARATOR " : " separator ");
                ((SQLObject) value).accept(this);
            }
        }
    }

	@Override
	public boolean visit(TeradataIntervalExpr x) {
		print0(ucase ? "INTERVAL " : "interval ");
		x.getValue().accept(this);
        print(' ');
        print0(ucase ? x.getUnit().name() : x.getUnit().name_lcase);
        return false;
	}

	@Override
	public void endVisit(TeradataIntervalExpr x) {

	}
	
	public void endVisit(SQLMethodInvokeExpr x) {
		
	}
	
	public boolean visit(SQLMethodInvokeExpr x) {
		if ("trim".equalsIgnoreCase(x.getMethodName())) {
			SQLExpr trim_character = (SQLExpr) x.getAttribute("trim_character");
			
			print0(x.getMethodName());
			print('(');
            String trim_option = (String) x.getAttribute("trim_option");
            if (trim_option != null && trim_option.length() != 0) {
                print0(trim_option);
                print(' ');
            }
			if (trim_character != null) {
                trim_character.accept(this);
			} 
			if (x.getParameters().size() > 0) {
                print0(ucase ? " FROM " : " from ");
                x.getParameters().get(0).accept(this);
            }
            print(')');
            return false;
		}
		return super.visit(x);
	}
}
