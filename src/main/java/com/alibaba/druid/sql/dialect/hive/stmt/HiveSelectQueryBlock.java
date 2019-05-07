package com.alibaba.druid.sql.dialect.hive.stmt;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.hive.ast.HiveClusterBy;
import com.alibaba.druid.sql.dialect.hive.ast.HiveDistributeBy;
import com.alibaba.druid.sql.dialect.hive.ast.HiveSortBy;

public class HiveSelectQueryBlock extends SQLSelectQueryBlock {

	protected HiveSortBy hiveSortBy;

	protected HiveDistributeBy hiveDistributeBy;
	
	protected HiveClusterBy hiveClusterBy;

	public HiveSortBy getHiveSortBy() {
		return hiveSortBy;
	}

	public void setHiveSortBy(HiveSortBy hiveSortBy) {
		if (hiveSortBy != null) {
			hiveSortBy.setParent(this);
		}
		this.hiveSortBy = hiveSortBy;
	}

	public HiveDistributeBy getHiveDistributeBy() {
		return hiveDistributeBy;
	}

	public void setHiveDistributeBy(HiveDistributeBy hiveDistributeBy) {
		if (hiveDistributeBy != null) {
			hiveDistributeBy.setParent(this);
		}
		this.hiveDistributeBy = hiveDistributeBy;
	}

	public HiveClusterBy getHiveClusterBy() {
		return hiveClusterBy;
	}

	public void setHiveClusterBy(HiveClusterBy hiveClusterBy) {
		if (hiveClusterBy != null) {
			hiveClusterBy.setParent(this);
		}
		this.hiveClusterBy = hiveClusterBy;
	}

}
