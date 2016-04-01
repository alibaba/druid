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

import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalytic;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalyticWindowing;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataIntervalExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class TeradataASTVisitorAdapter extends SQLASTVisitorAdapter implements TeradataASTVisitor {

	@Override
	public boolean visit(TeradataAnalyticWindowing x) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void endVisit(TeradataIntervalExpr x) {
		// TODO Auto-generated method stub
		
	}

}
