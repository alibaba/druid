/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.mysql.visitor;

import java.util.Map;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLCharactorDataType;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBinaryExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlBooleanExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlExtractExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlIntervalExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlMatchAgainstExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCommitStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlReplicateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRollbackStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectGroupBy;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStartTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class MySqlOutputVisitor extends SQLASTOutputVisitor implements
		MySqlASTVisitor {

	public MySqlOutputVisitor(Appendable appender) {
		super(appender);
	}

	public boolean visit(MySqlBooleanExpr x) {
		print(x.getValue() ? "true" : "false");

		return false;
	}

	public void endVisit(MySqlBooleanExpr x) {
	}

	@Override
	public boolean visit(SQLSelectQueryBlock select) {
		if (select instanceof MySqlSelectQueryBlock) {
			return visit((MySqlSelectQueryBlock) select);
		}

		return false;
	}

	public boolean visit(MySqlSelectQueryBlock select) {
		print("SELECT ");

		if (SQLSetQuantifier.ALL == select.getDistionOption())
			print("ALL ");
		else if (SQLSetQuantifier.DISTINCT == select.getDistionOption())
			print("DISTINCT ");
		else if (SQLSetQuantifier.DISTINCTROW == select.getDistionOption()) {
			print("DISTINCTROW ");
		}

		if (select.isHignPriority()) {
			print("HIGH_PRIORITY ");
		}

		if (select.isSmallResult()) {
			print("SQL_SMALL_RESULT ");
		}

		if (select.isBigResult()) {
			print("SQL_BIG_RESULT ");
		}

		if (select.isBufferResult()) {
			print("SQL_BUFFER_RESULT ");
		}

		if (select.getCache() != null) {
			if (select.getCache().booleanValue()) {
				print("SQL_CACHE ");
			} else {
				print("SQL_NO_CACHE ");
			}
		}

		if (select.isCalcFoundRows()) {
			print("SQL_CALC_FOUND_ROWS ");
		}

		printSelectList(select.getSelectList());

		if (select.getOutFile() != null) {
			println();
			print("INTO OUTFILE ");
			select.getOutFile().accept(this);
			if (select.getOutFileCharset() != null) {
				print(" CHARACTER SET ");
				print(select.getOutFileCharset());
			}

			if (select.getOutFileColumnsTerminatedBy() != null
					|| select.getOutFileColumnsEnclosedBy() != null
					|| select.getOutFileColumnsEscaped() != null) {
				print(" COLUMNS");
				if (select.getOutFileColumnsTerminatedBy() != null) {
					print(" TERMINATED BY ");
					select.getOutFileColumnsTerminatedBy().accept(this);
				}

				if (select.getOutFileColumnsEnclosedBy() != null) {
					if (select.isOutFileColumnsEnclosedOptionally()) {
						print(" OPTIONALLY");
					}
					print(" ENCLOSED BY ");
					select.getOutFileColumnsEnclosedBy().accept(this);
				}

				if (select.getOutFileColumnsEscaped() != null) {
					print(" ESCAPED BY ");
					select.getOutFileColumnsEscaped().accept(this);
				}
			}

			if (select.getOutFileLinesStartingBy() != null
					|| select.getOutFileLinesTerminatedBy() != null) {
				print(" LINES");
				if (select.getOutFileLinesStartingBy() != null) {
					print(" STARTING BY ");
					select.getOutFileLinesStartingBy().accept(this);
				}

				if (select.getOutFileLinesTerminatedBy() != null) {
					print(" TERMINATED BY ");
					select.getOutFileLinesTerminatedBy().accept(this);
				}
			}
		}

		if (select.getFrom() != null) {
			println();
			print("FROM ");
			select.getFrom().accept(this);
		}

		if (select.getWhere() != null) {
			println();
			print("WHERE ");
			select.getWhere().accept(this);
		}

		if (select.getGroupBy() != null) {
			println();
			select.getGroupBy().accept(this);
		}

		if (select.getOrderBy() != null) {
			println();
			select.getOrderBy().accept(this);
		}

		if (select.getLimit() != null) {
			println();
			select.getLimit().accept(this);
		}

		if (select.getProcedureName() != null) {
			print(" PROCEDURE ");
			select.getProcedureName().accept(this);
			if (select.getProcedureArgumentList().size() > 0) {
				print("(");
				printAndAccept(select.getProcedureArgumentList(), ", ");
				print(")");
			}
		}

		if (select.isForUpdate()) {
			println();
			print("FOR UPDATE");
		}

		if (select.isLockInShareMode()) {
			println();
			print("LOCK IN SHARE MODE");
		}

		return false;
	}

	public boolean visit(SQLColumnDefinition x) {
		MySqlSQLColumnDefinition mysqlColumn = null;

		if (x instanceof MySqlSQLColumnDefinition) {
			mysqlColumn = (MySqlSQLColumnDefinition) x;
		}

		x.getName().accept(this);
		print(' ');
		x.getDataType().accept(this);

		if (x.getDefaultExpr() != null) {
			print(" DEFAULT ");
			x.getDefaultExpr().accept(this);
		}

		if (mysqlColumn != null && mysqlColumn.isAutoIncrement()) {
			print(" AUTO_INCREMENT");
		}

		for (SQLColumnConstraint item : x.getConstaints()) {
			print(' ');
			item.accept(this);
		}

		return false;
	}

	public boolean visit(MySqlSelectQueryBlock.Limit x) {
		print("LIMIT ");
		if (x.getOffset() != null) {
			x.getOffset().accept(this);
			print(", ");
		}
		x.getRowCount().accept(this);

		return false;
	}

	public boolean visit(SQLDataType x) {
		print(x.getName());
		if (x.getArguments().size() > 0) {
			print("(");
			printAndAccept(x.getArguments(), ", ");
			print(")");
		}

		if (x instanceof SQLCharactorDataType) {
			SQLCharactorDataType charType = (SQLCharactorDataType) x;
			if (charType.getCharSetName() != null) {
				print(" CHARACTER SET ");
				print(charType.getCharSetName());

				if (charType.getCollate() != null) {
					print(" COLLATE ");
					print(charType.getCollate());
				}
			}
		}
		return false;
	}

	@Override
	public void endVisit(Limit x) {

	}

	@Override
	public void endVisit(MySqlTableIndex x) {

	}

	@Override
	public boolean visit(MySqlTableIndex x) {
		print("INDEX");
		if (x.getName() != null) {
			print(" ");
			x.getName().accept(this);
		}

		if (x.getIndexType() != null) {
			print(" USING ");
			print(x.getIndexType());
		}

		print("(");
		for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
			if (i != 0) {
				print(", ");
			}
			x.getColumns().get(i).accept(this);
		}
		print(")");
		return false;
	}

	public boolean visit(SQLCreateTableStatement x) {

		MySqlCreateTableStatement mysqlCreateTableStatement = null;
		if (x instanceof MySqlCreateTableStatement) {
			mysqlCreateTableStatement = (MySqlCreateTableStatement) x;
		}

		if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(x.getType())) {
			print("CREATE TEMPORARY TABLE ");
		} else {
			print("CREATE TABLE ");
		}

		if (mysqlCreateTableStatement != null
				&& mysqlCreateTableStatement.isIfNotExiists()) {
			print("IF NOT EXISTS ");
		}

		x.getName().accept(this);
		print(" (");
		incrementIndent();
		println();
		for (int i = 0, size = x.getTableElementList().size(); i < size; ++i) {
			if (i != 0) {
				print(", ");
				println();
			}
			x.getTableElementList().get(i).accept(this);
		}
		decrementIndent();
		println();
		print(")");

		if (mysqlCreateTableStatement != null) {
			for (Map.Entry<String, String> option : mysqlCreateTableStatement
					.getTableOptions().entrySet()) {
				print(" ");
				print(option.getKey());
				print(" = ");
				print(option.getValue());
			}
		}

		if (mysqlCreateTableStatement != null
				&& mysqlCreateTableStatement.getQuery() != null) {
			print(" ");
			incrementIndent();
			println();
			mysqlCreateTableStatement.getQuery().accept(this);
			decrementIndent();
		}

		return false;
	}

	@Override
	public void endVisit(MySqlKey x) {

	}

	@Override
	public void endVisit(MySqlPrimaryKey x) {

	}

	@Override
	public boolean visit(MySqlKey x) {
		if (x.getName() != null) {
			print("CONSTRAINT ");
			x.accept(this);
			print(' ');
		}

		print("KEY");

		if (x.getIndexType() != null) {
			print(" USING ");
			print(x.getIndexType());
		}

		print(" (");

		for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
			if (i != 0) {
				print(", ");
			}
			x.getColumns().get(i).accept(this);
		}
		print(")");

		return false;
	}

	@Override
	public boolean visit(MySqlPrimaryKey x) {
		if (x.getName() != null) {
			print("CONSTRAINT ");
			x.accept(this);
			print(' ');
		}

		print("PRIAMRY KEY");

		if (x.getIndexType() != null) {
			print(" USING ");
			print(x.getIndexType());
		}

		print(" (");

		for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
			if (i != 0) {
				print(", ");
			}
			x.getColumns().get(i).accept(this);
		}
		print(")");

		return false;
	}

	public boolean visit(SQLCharExpr x) {
		if (x instanceof MySqlCharExpr) {
			print(x.toString());
		} else if ((x.getText() == null) || (x.getText().length() == 0)) {
			print("NULL");
		} else {
			print("'");
			print(x.getText().replaceAll("'", "''"));
			print("'");
		}

		return false;
	}

	public boolean visit(SQLVariantRefExpr x) {
		print(x.getName());

		String collate = (String) x.getAttribute("COLLATE");
		if (collate != null) {
			print(" COLLATE ");
			print(collate);
		}

		return false;
	}

	public boolean visit(SQLMethodInvokeExpr x) {
		if ("SUBSTRING".equalsIgnoreCase(x.getMethodName())) {
			if (x.getOwner() != null) {
				x.getOwner().accept(this);
				print(".");
			}
			print(x.getMethodName());
			print("(");
			printAndAccept(x.getParameters(), ", ");
			SQLExpr from = (SQLExpr) x.getAttribute("FROM");
			if (from != null) {
				print(" FROM ");
				from.accept(this);
			}

			SQLExpr _for = (SQLExpr) x.getAttribute("FOR");
			if (_for != null) {
				print(" FOR ");
				_for.accept(this);
			}
			print(")");

			return false;
		}

		if ("TRIM".equalsIgnoreCase(x.getMethodName())) {
			if (x.getOwner() != null) {
				x.getOwner().accept(this);
				print(".");
			}
			print(x.getMethodName());
			print("(");

			String trimType = (String) x.getAttribute("TRIM_TYPE");
			if (trimType != null) {
				print(trimType);
				print(' ');
			}

			printAndAccept(x.getParameters(), ", ");

			SQLExpr from = (SQLExpr) x.getAttribute("FROM");
			if (from != null) {
				print(" FROM ");
				from.accept(this);
			}

			print(")");

			return false;
		}

		if ("CONVERT".equalsIgnoreCase(x.getMethodName())) {
			if (x.getOwner() != null) {
				x.getOwner().accept(this);
				print(".");
			}
			print(x.getMethodName());
			print("(");
			printAndAccept(x.getParameters(), ", ");

			String charset = (String) x.getAttribute("USING");
			if (charset != null) {
				print(" USING ");
				print(charset);
			}
			print(")");
			return false;
		}

		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlIntervalExpr x) {

	}

	@Override
	public boolean visit(MySqlIntervalExpr x) {
		print("INTERVAL ");
		x.getValue().accept(this);
		print(' ');
		print(x.getUnit().name());
		return false;
	}

	@Override
	public boolean visit(MySqlExtractExpr x) {
		print("EXTRACT(");
		print(x.getUnit().name());
		print(" FROM ");
		x.getValue().accept(this);
		print(')');
		return false;
	}

	@Override
	public void endVisit(MySqlExtractExpr x) {

	}

	@Override
	public void endVisit(MySqlMatchAgainstExpr x) {

	}

	@Override
	public boolean visit(MySqlMatchAgainstExpr x) {
		print("MATCH (");
		printAndAccept(x.getColumns(), ", ");
		print(")");

		print(" AGAINST (");
		x.getAgainst().accept(this);
		if (x.getSearchModifier() != null) {
			print(' ');
			print(x.getSearchModifier().name);
		}
		print(')');

		return false;
	}

	@Override
	public void endVisit(MySqlBinaryExpr x) {

	}

	@Override
	public boolean visit(MySqlBinaryExpr x) {
		print("b'");
		print(x.getValue());
		print('\'');

		return false;
	}

	@Override
	public void endVisit(MySqlPrepareStatement x) {
	}

	@Override
	public boolean visit(MySqlPrepareStatement x) {
		print("PREPARE ");
		x.getName().accept(this);
		print(" FROM ");
		x.getFrom().accept(this);
		return false;
	}

	@Override
	public void endVisit(MySqlExecuteStatement x) {

	}

	@Override
	public boolean visit(MySqlExecuteStatement x) {
		print("EXECUTE ");
		x.getStatementName().accept(this);
		if (x.getParameters().size() > 0) {
			print(" USING ");
			printAndAccept(x.getParameters(), ", ");
		}
		return false;
	}

	@Override
	public void endVisit(MySqlDeleteStatement x) {

	}

	@Override
	public boolean visit(MySqlDeleteStatement x) {
		print("DELETE ");

		if (x.isLowPriority()) {
			print("LOW_PRIORITY ");
		}

		if (x.isQuick()) {
			print("QUICK ");
		}

		if (x.isIgnore()) {
			print("IGNORE ");
		}

		if (x.getFrom() == null) {
			print("FROM ");
			printAndAccept(x.getTableNames(), ", ");
		} else {
			printAndAccept(x.getTableNames(), ", ");
			println();
			print("FROM ");
			x.getFrom().accept(this);
		}

		if (x.getUsing() != null) {
			println();
			print("USING ");
			x.getUsing().accept(this);
		}

		if (x.getWhere() != null) {
			println();
			print("WHERE ");
			x.getWhere().accept(this);
		}

		if (x.getOrderBy() != null) {
			println();
			x.getOrderBy().accept(this);
		}

		if (x.getLimit() != null) {
			println();
			x.getLimit().accept(this);
		}

		return false;
	}

	@Override
	public void endVisit(MySqlInsertStatement x) {

	}

	@Override
	public boolean visit(MySqlInsertStatement x) {
		print("INSERT ");

		if (x.isLowPriority()) {
			print("LOW_PRIORITY ");
		}

		if (x.isDelayed()) {
			print("DELAYED ");
		}

		if (x.isHighPriority()) {
			print("HIGH_PRIORITY ");
		}

		if (x.isIgnore()) {
			print("IGNORE ");
		}

		print("INTO ");

		x.getTableName().accept(this);

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

		if (x.getValuesList().size() != 0) {
			println();
			print("VALUES");
			println();
			for (int i = 0, size = x.getValuesList().size(); i < size; ++i) {
				if (i != 0) {
					print(", ");
				}
				x.getValuesList().get(i).accept(this);
			}

		}

		if (x.getQuery() != null) {
			print(" ");
			x.getQuery().accept(this);
		}

		if (x.getDuplicateKeyUpdate().size() != 0) {
			print(" ON DUPLICATE KEY UPDATE ");
			printAndAccept(x.getDuplicateKeyUpdate(), ", ");
		}

		return false;
	}

	@Override
	public void endVisit(MySqlLoadDataInFileStatement x) {

	}

	@Override
	public boolean visit(MySqlLoadDataInFileStatement x) {
		print("LOAD DATA ");

		if (x.isLowPriority()) {
			print("LOW_PRIORITY ");
		}

		if (x.isConcurrent()) {
			print("CONCURRENT ");
		}

		if (x.isLocal()) {
			print("LOCAL ");
		}

		print("INFILE ");

		x.getFileName().accept(this);

		if (x.isReplicate()) {
			print(" REPLACE ");
		}

		if (x.isIgnore()) {
			print(" IGNORE ");
		}

		print(" INTO TABLE ");
		x.getTableName().accept(this);

		if (x.getColumnsTerminatedBy() != null
				|| x.getColumnsEnclosedBy() != null
				|| x.getColumnsEscaped() != null) {
			print(" COLUMNS");
			if (x.getColumnsTerminatedBy() != null) {
				print(" TERMINATED BY ");
				x.getColumnsTerminatedBy().accept(this);
			}

			if (x.getColumnsEnclosedBy() != null) {
				if (x.isColumnsEnclosedOptionally()) {
					print(" OPTIONALLY");
				}
				print(" ENCLOSED BY ");
				x.getColumnsEnclosedBy().accept(this);
			}

			if (x.getColumnsEscaped() != null) {
				print(" ESCAPED BY ");
				x.getColumnsEscaped().accept(this);
			}
		}

		if (x.getLinesStartingBy() != null || x.getLinesTerminatedBy() != null) {
			print(" LINES");
			if (x.getLinesStartingBy() != null) {
				print(" STARTING BY ");
				x.getLinesStartingBy().accept(this);
			}

			if (x.getLinesTerminatedBy() != null) {
				print(" TERMINATED BY ");
				x.getLinesTerminatedBy().accept(this);
			}
		}

		if (x.getSetList().size() != 0) {
			print(" SET ");
			printAndAccept(x.getSetList(), ", ");
		}

		return false;
	}

	@Override
	public void endVisit(MySqlReplicateStatement x) {

	}

	@Override
	public boolean visit(MySqlReplicateStatement x) {
		print("REPLACE ");

		if (x.isLowPriority()) {
			print("LOW_PRIORITY ");
		}

		if (x.isDelayed()) {
			print("DELAYED ");
		}

		print("INTO ");

		x.getTableName().accept(this);

		if (x.getColumns().size() > 0) {
			print(" (");
			for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
				if (i != 0) {
					print(", ");
				}
				x.getColumns().get(i).accept(this);
			}
			print(")");
		}

		if (x.getValuesList().size() != 0) {
			println();
			print("VALUES ");
			int size = x.getValuesList().size();
			if (size == 0) {
				print("()");
			} else {
				for (int i = 0; i < size; ++i) {
					if (i != 0) {
						print(", ");
					}
					x.getValuesList().get(i).accept(this);
				}
			}
		}

		if (x.getQuery() != null) {
			x.getQuery().accept(this);
		}

		if (x.getSetItems().size() != 0) {
			println();
			print("SET ");
			printAndAccept(x.getSetItems(), ", ");
		}

		return false;
	}

	@Override
	public void endVisit(MySqlSelectGroupBy x) {

	}

	@Override
	public boolean visit(MySqlSelectGroupBy x) {
		super.visit(x);

		if (x.isRollUp()) {
			print(" WITH ROLLUP");
		}

		return false;
	}

	@Override
	public void endVisit(MySqlStartTransactionStatement x) {

	}

	@Override
	public boolean visit(MySqlStartTransactionStatement x) {
		print("START TRANSACTION");
		if (x.isConsistentSnapshot()) {
			print(" WITH CONSISTENT SNAPSHOT");
		}

		if (x.isBegin()) {
			print(" BEGIN");
		}

		if (x.isWork()) {
			print(" WORK");
		}

		return false;
	}

	@Override
	public void endVisit(MySqlCommitStatement x) {

	}

	@Override
	public boolean visit(MySqlCommitStatement x) {
		print("COMMIT");

		if (x.isWork()) {
			print(" WORK");
		}

		if (x.getChain() != null) {
			if (x.getChain().booleanValue()) {
				print(" AND CHAIN");
			} else {
				print(" AND NO CHAIN");
			}
		}

		if (x.getRelease() != null) {
			if (x.getRelease().booleanValue()) {
				print(" AND RELEASE");
			} else {
				print(" AND NO RELEASE");
			}
		}

		return false;
	}

	@Override
	public void endVisit(MySqlRollbackStatement x) {

	}

	@Override
	public boolean visit(MySqlRollbackStatement x) {
		print("ROLLBACK");

		if (x.isWork()) {
			print(" WORK");
		}

		if (x.getChain() != null) {
			if (x.getChain().booleanValue()) {
				print(" AND CHAIN");
			} else {
				print(" AND NO CHAIN");
			}
		}

		if (x.getRelease() != null) {
			if (x.getRelease().booleanValue()) {
				print(" AND RELEASE");
			} else {
				print(" AND NO RELEASE");
			}
		}

		return false;
	}

	@Override
	public void endVisit(MySqlShowColumnsStatement x) {

	}

	@Override
	public boolean visit(MySqlShowColumnsStatement x) {
		if (x.isFull()) {
			print("SHOW FULL COLUMNS");
		} else {
			print("SHOW COLUMNS");
		}

		if (x.getTable() != null) {
			print(" FROM ");
			x.getTable().accept(this);
		}

		if (x.getDatabase() != null) {
			print(" FROM ");
			x.getDatabase().accept(this);
		}

		if (x.getLike() != null) {
			print(" LIKE ");
			x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print(" WHERE ");
			x.getWhere().accept(this);
		}

		return false;
	}

	@Override
	public void endVisit(MySqlShowTablesStatement x) {

	}

	@Override
	public boolean visit(MySqlShowTablesStatement x) {
		if (x.isFull()) {
			print("SHOW FULL TABLES");
		} else {
			print("SHOW TABLES");
		}

		if (x.getDatabase() != null) {
			print(" FROM ");
			x.getDatabase().accept(this);
		}

		if (x.getLike() != null) {
			print(" LIKE ");
			x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print(" WHERE ");
			x.getWhere().accept(this);
		}

		return false;
	}

	@Override
	public void endVisit(MySqlShowDatabasesStatement x) {

	}

	@Override
	public boolean visit(MySqlShowDatabasesStatement x) {
		print("SHOW DATABASES");

		if (x.getLike() != null) {
			print(" LIKE ");
			x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print(" WHERE ");
			x.getWhere().accept(this);
		}

		return false;
	}

	@Override
	public void endVisit(MySqlShowWarningsStatement x) {

	}

	@Override
	public boolean visit(MySqlShowWarningsStatement x) {
		if (x.isCount()) {
			print("SHOW COUNT(*) WARNINGS");
		} else {
			print("SHOW WARNINGS");
			if (x.getLimit() != null) {
				print(' ');
				x.getLimit().accept(this);
			}
		}

		return false;
	}

	@Override
	public void endVisit(MySqlShowStatusStatement x) {

	}

	@Override
	public boolean visit(MySqlShowStatusStatement x) {
		print("SHOW ");

		if (x.isGlobal()) {
			print("GLOBAL ");
		}

		if (x.isSession()) {
			print("SESSION ");
		}

		print("STATUS");

		if (x.getLike() != null) {
			print(" LIKE ");
			x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print(" WHERE ");
			x.getWhere().accept(this);
		}

		return false;
	}

	@Override
	public void endVisit(MySqlLoadXmlStatement x) {

	}

	@Override
	public boolean visit(MySqlLoadXmlStatement x) {
		print("LOAD XML ");

		if (x.isLowPriority()) {
			print("LOW_PRIORITY ");
		}

		if (x.isConcurrent()) {
			print("CONCURRENT ");
		}

		if (x.isLocal()) {
			print("LOCAL ");
		}

		print("INFILE ");

		x.getFileName().accept(this);

		if (x.isReplicate()) {
			print(" REPLACE ");
		}

		if (x.isIgnore()) {
			print(" IGNORE ");
		}

		print(" INTO TABLE ");
		x.getTableName().accept(this);

		if (x.getCharset() != null) {
			print(" CHARSET ");
			print(x.getCharset());
		}

		if (x.getRowsIdentifiedBy() != null) {
			print(" ROWS IDENTIFIED BY ");
			x.getRowsIdentifiedBy().accept(this);
		}

		if (x.getSetList().size() != 0) {
			print(" SET ");
			printAndAccept(x.getSetList(), ", ");
		}

		return false;
	}

	@Override
	public void endVisit(CobarShowStatus x) {

	}

	@Override
	public boolean visit(CobarShowStatus x) {
		print("SHOW COBAR_STATUS");
		return false;
	}

	@Override
	public void endVisit(MySqlKillStatement x) {

	}

	@Override
	public boolean visit(MySqlKillStatement x) {
		if (MySqlKillStatement.Type.CONNECTION.equals(x.getType())) {
			print("KILL CONNECTION ");
		} else if (MySqlKillStatement.Type.QUERY.equals(x.getType())) {
			print("KILL QUERY ");
		}
		x.getThreadId().accept(this);
		return false;
	}

	@Override
	public void endVisit(MySqlBinlogStatement x) {

	}

	@Override
	public boolean visit(MySqlBinlogStatement x) {
		print("BINLOG ");
		x.getExpr().accept(this);
		return false;
	}
	
	@Override
	public void endVisit(MySqlResetStatement x) {
		
	}
	
	@Override
	public boolean visit(MySqlResetStatement x) {
		print("RESET ");
		for (int i = 0; i < x.getOptions().size(); ++i) {
			if (i != 0) {
				print(", ");
			}
			print(x.getOptions().get(i));
		}
		return false;
	}

}
