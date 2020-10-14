package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

/**
 * Created by wenshao on 03/02/2017.
 */
public class Issue1576 extends TestCase {
    private final DbType dbType = JdbcConstants.ORACLE;
    public void test_for_issue() throws Exception {
        String sql = "select * from t whe id = 1";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);

        Exception error = null;
        try {
            parser.parseStatement(true);
        } catch (Exception ex) {
            error = ex;
        }
        assertNotNull(error);
    }
}
