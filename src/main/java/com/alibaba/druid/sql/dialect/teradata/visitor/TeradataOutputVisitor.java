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
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.teradata.ast.TeradataDateTimeDataType;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalytic;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalyticWindowing;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataDateExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataExtractExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataFormatExpr;
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
		if (x.getValue() instanceof SQLLiteralExpr) {
            print0(ucase ? "INTERVAL " : "interval ");
            x.getValue().accept(this);
            print(' ');
        } else {
            print('(');
            x.getValue().accept(this);
            print0(") ");
        }

        print0(x.getType().name());

        if (x.getPrecision() != null) {
            print('(');
            print(x.getPrecision().intValue());
            if (x.getFactionalSecondsPrecision() != null) {
                print0(", ");
                print(x.getFactionalSecondsPrecision().intValue());
            }
            print(')');
        }

        if (x.getToType() != null) {
            print0(ucase ? " TO " : " to ");
            print0(x.getToType().name());
            if (x.getToFactionalSecondsPrecision() != null) {
                print('(');
                print(x.getToFactionalSecondsPrecision().intValue());
                print(')');
            }
        }

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
		} else if ("SUBSTRING".equalsIgnoreCase(x.getMethodName())) {
			SQLExpr origin_string = (SQLExpr) x.getAttribute("ORIGIN_STRING");
			
			print0(x.getMethodName());
			print('(');

			if (origin_string != null) {
				origin_string.accept(this);
			}
			print0(ucase ? " FROM " : " from ");
			if(x.getAttribute("FROM_INDEX") instanceof SQLIntegerExpr) {
				int from_index = ((SQLIntegerExpr) x.getAttribute("FROM_INDEX")).getNumber().intValue();
				print(from_index);
			} else {
				print(x.getAttribute("FROM_INDEX").toString());
			}
			
			if (x.getAttribute("FOR_INDEX") != null) {
				print0(ucase ? " FOR " : " for ");
				int for_index = ((SQLIntegerExpr) x.getAttribute("FOR_INDEX")).getNumber().intValue();
				print(for_index);
			}
			
			print(')');
			return false;
		}
		return super.visit(x);
	}

	@Override
	public boolean visit(TeradataDateExpr x) {	
        print0(ucase ? x.getType().toString().toUpperCase() + " '" 
        		: x.getType().toString().toLowerCase() + " '");
        print0(x.getLiteral());
        print('\'');
        return false;
	}

	@Override
	public void endVisit(TeradataDateExpr x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean visit(TeradataFormatExpr x) {
		print0(ucase ? "FORMAT '" : "format '");
        print0(x.getLiteral());
        print('\'');
        return false;
	}

	@Override
	public void endVisit(TeradataFormatExpr x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean visit(TeradataExtractExpr x) {
		print0(ucase ? "EXTRACT(" : "extract(");
        print0(x.getUnit().name());
        print0(ucase ? " FROM " : " from ");
        x.getFrom().accept(this);
        print(')');
        return false;
	}

	@Override
	public void endVisit(TeradataExtractExpr x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean visit(TeradataDateTimeDataType x) {
		print0(x.getName()); 
		
		if (x.getArguments().size() > 0) {
			print('(');
			x.getArguments().get(0).accept(this);
			print(')');
		}
		
		if (x.isWithTimeZone()) {
			print0(ucase ? " WITH TIME ZONE" : " with time zone");
		}
		
		return false;
	}

	@Override
	public void endVisit(TeradataDateTimeDataType x) {
		
	}
}
