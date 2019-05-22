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
package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.dialect.hive.ast.HiveClusterBy;
import com.alibaba.druid.sql.dialect.hive.ast.HiveClusterByItem;
import com.alibaba.druid.sql.dialect.hive.ast.HiveDistributeBy;
import com.alibaba.druid.sql.dialect.hive.ast.HiveDistributeByItem;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveSelectSortByItem;
import com.alibaba.druid.sql.dialect.hive.ast.HiveShowDatabasesStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveShowTablesStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveSortBy;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

public class HiveASTVisitorAdapter extends SQLASTVisitorAdapter implements HiveASTVisitor {
    @Override
    public boolean visit(HiveCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(HiveCreateTableStatement x) {

    }

    @Override
    public boolean visit(HiveMultiInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(HiveMultiInsertStatement x) {

    }

    @Override
    public boolean visit(HiveInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(HiveInsertStatement x) {

    }

    @Override
    public boolean visit(HiveInsert x) {
        return true;
    }

    @Override
    public void endVisit(HiveInsert x) {

    }

	@Override
	public boolean visit(HiveSelectSortByItem x) {		
		return true;
	}

	@Override
	public void endVisit(HiveSelectSortByItem x) {		
		
	}

	@Override
	public boolean visit(HiveSortBy x) {		
		return true;
	}

	@Override
	public void endVisit(HiveSortBy x) {		
		
	}

	@Override
	public boolean visit(HiveDistributeBy x) {
		return true;
	}

	@Override
	public void endVisit(HiveDistributeBy x) {
		
	}

	@Override
	public boolean visit(HiveDistributeByItem x) {
		return true;
	}

	@Override
	public void endVisit(HiveDistributeByItem x) {
		
	}

	@Override
	public boolean visit(HiveClusterBy x) {
		return true;
	}

	@Override
	public void endVisit(HiveClusterBy x) {
		
	}

	@Override
	public boolean visit(HiveClusterByItem x) {
		return true;
	}

	@Override
	public void endVisit(HiveClusterByItem x) {
		
	}

	@Override
	public boolean visit(HiveShowDatabasesStatement x) {
		return false;
	}

	@Override
	public void endVisit(HiveShowDatabasesStatement x) {
		
	}

	@Override
	public boolean visit(HiveShowTablesStatement x) {
		return false;
	}

	@Override
	public void endVisit(HiveShowTablesStatement x) {
		
	}
    
    
}
