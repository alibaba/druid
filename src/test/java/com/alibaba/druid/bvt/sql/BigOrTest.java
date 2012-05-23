package com.alibaba.druid.bvt.sql;

import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

public class BigOrTest extends TestCase {

    public void testBigOr() throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append("SELECT * FROM T WHERE FID = ?");
        for (int i = 0; i < 10000; ++i) {
            buf.append(" OR FID = " + i);
        }
        String sql = buf.toString();
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, null);
        String text = SQLUtils.toSQLString(stmtList.get(0));
        System.out.println(text);
    }
}
