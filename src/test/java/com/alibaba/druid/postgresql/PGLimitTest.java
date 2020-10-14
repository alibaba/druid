package com.alibaba.druid.postgresql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcConstants;

/**
 * 
 * @author lizongbo
 * 
 */
public class PGLimitTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DbType dbType = JdbcConstants.POSTGRESQL;// "postgresql";
		// dbType = "mysql";
		String sql = " select * from brandinfo where 1=1 and brandid > 100 order by brandid asc";
		String sqlLimit = com.alibaba.druid.sql.PagerUtils.limit(sql, dbType,
				2499, 100);
		System.out.println("sqlLimit == " + sqlLimit);
		String sqlCount = com.alibaba.druid.sql.PagerUtils.count(sql, dbType);
		System.out.println("sqlCount == " + sqlCount);

	}

}
