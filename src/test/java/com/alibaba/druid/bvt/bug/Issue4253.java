package com.alibaba.druid.bvt.bug;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;
import junit.framework.TestCase;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Created by wenshao on 03/02/2017.
 */
public class Issue4253 extends TestCase {
    private final DbType dbType = JdbcConstants.ORACLE;

    public void test_for_issue() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("bvt/parser/oracle-63.txt");
        Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        String input = Utils.read(reader);
        JdbcUtils.close(reader);
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(input, dbType);
        Exception error = null;
        try {
            parser.parseStatement(true);
        } catch (Exception ex) {
            error = ex;
        }
        assertNull(error);
    }
}
