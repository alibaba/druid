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

import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.teradata.ast.TeradataDateTimeDataType;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalytic;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalyticWindowing;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataDateExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataExtractExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataFormatExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataIntervalExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface TeradataASTVisitor extends SQLASTVisitor {

	boolean visit(TeradataAnalyticWindowing x);

	void endVisit(TeradataAnalyticWindowing x);

	boolean visit(TeradataAnalytic x);
	
	boolean visit(SQLSelectQueryBlock x);
	
	boolean visit(SQLSelect x);
	
	boolean visit(TeradataIntervalExpr x);
	
	void endVisit(TeradataIntervalExpr x);
	
	boolean visit(SQLSubqueryTableSource x);
	
	void endVisit(SQLSubqueryTableSource x);

	boolean visit(TeradataDateExpr x);

	void endVisit(TeradataDateExpr x);

	boolean visit(TeradataFormatExpr x);

	void endVisit(TeradataFormatExpr x);

	boolean visit(TeradataExtractExpr x);

	void endVisit(TeradataExtractExpr x);
	
	boolean visit(TeradataDateTimeDataType x);
	
	void endVisit(TeradataDateTimeDataType x);
}
