package com.alibaba.druid.bvt.filter.wall;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.wall.spi.WallVisitorUtils;

public class WallVisitorUtilsLargeOrTest extends TestCase {
	public void test_largeOr() throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append("ID = 1");
		for (int i = 2; i <= 1000 * 10; ++i) {
			buf.append(" OR ID = " + i);
		}
		
		Assert.assertEquals(null, WallVisitorUtils.getValue(SQLUtils.toSQLExpr(buf.toString())));
	}
}
