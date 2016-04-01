package com.alibaba.druid.sql.dialect.teradata.ast.expr;

public enum TeradataIntervalUnit {
	
	YEAR, MONTH, DAY,
	
	HOUR, MINUTE, SECOND;
	
	public final String name_lcase;
	
	private TeradataIntervalUnit() {
		this.name_lcase = name().toLowerCase();
	}
}
