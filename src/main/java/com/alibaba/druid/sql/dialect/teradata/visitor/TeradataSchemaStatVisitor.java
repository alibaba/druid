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

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalytic;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalyticWindowing;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataDateExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataExtractExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataFormatExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataIntervalExpr;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.util.JdbcUtils;

public class TeradataSchemaStatVisitor extends SchemaStatVisitor implements TeradataASTVisitor {

	protected final Map<String, SQLObject> aliasQueryMap = new LinkedHashMap<String, SQLObject>();
	
    @Override
    public String getDbType() {
        return JdbcUtils.TERADATA;
    }

	@Override
	public boolean visit(TeradataAnalyticWindowing x) {
		return true;
	}

	@Override
	public void endVisit(TeradataAnalyticWindowing x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean visit(TeradataAnalytic x) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(TeradataIntervalExpr x) {
		return true;
	}

	@Override
	public void endVisit(TeradataIntervalExpr x) {

	}
	
	public boolean visit(SQLMethodInvokeExpr x) {
		if ("trim".equalsIgnoreCase(x.getMethodName())) {
			SQLExpr trim_character = (SQLExpr) x.getAttribute("trim_character");
			accept(trim_character);
		}
		return super.visit(x);
	}
	
	@Override
	public boolean visit(SQLSubqueryTableSource x) {
		accept(x.getSelect());
		
		String table = (String) x.getSelect().getAttribute(ATTR_TABLE);
		if (aliasMap != null && x.getAlias() != null) {
			if (table != null) {
				this.aliasMap.put(x.getAlias(), table);
			}
			addSubQuery(x.getAlias(), x.getSelect());
			this.setCurrentTable(x.getAlias());
		}
		
		if (table != null) {
			x.putAttribute(ATTR_TABLE, table);
		}
		
		return false;
	}
	
	@Override
	public void endVisit(SQLSubqueryTableSource x) {
		
	}
	
	public boolean visit(SQLSelectItem x) {
		x.getExpr().accept(this);
        
        String alias = x.getAlias();
        
        Map<String, String> aliasMap = this.getAliasMap();
        if (alias != null && (!alias.isEmpty()) && aliasMap != null) {
            if (x.getExpr() instanceof SQLName) {
                putAliasMap(aliasMap, alias, x.getExpr().toString());
            } else {
            	TeradataSchemaStatVisitor visitor = new TeradataSchemaStatVisitor(); 
            	x.getExpr().accept(visitor);
            	putAliasMap(aliasMap, alias, null);
            	// Add expression alias into aliasQuery map
            	// in order to retrieve column info from aliasQueryMap
            	// if aliasMap value is null 
            	String uniqueAliasName = this.getCurrentTable() == null ?
            			alias : this.getCurrentTable() + "." + alias;
            	addAliasQuery(uniqueAliasName, x.getExpr());
            }
        }
        
        return false;
	}
	
	protected void addAliasQuery(String alias, SQLObject query) {
		String alias_lcase = alias.toLowerCase();
		aliasQueryMap.put(alias_lcase, query);
	}
	
	public SQLObject getAliasQuery(String alias) {
		String alias_lcase = alias.toLowerCase();
		return aliasQueryMap.get(alias_lcase);
	}
	
	public Map<String, SQLObject> getAliasQueryMap() {
		return aliasQueryMap;
	}

	@Override
	public boolean visit(TeradataDateExpr x) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void endVisit(TeradataDateExpr x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean visit(TeradataFormatExpr x) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void endVisit(TeradataFormatExpr x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean visit(TeradataExtractExpr x) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void endVisit(TeradataExtractExpr x) {
		// TODO Auto-generated method stub
		
	}

}
