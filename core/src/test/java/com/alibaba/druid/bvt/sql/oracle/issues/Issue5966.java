package com.alibaba.druid.bvt.sql.oracle.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5966>Issue来源</a>
 *
 */
public class Issue5966 {

    @Test
    public void test_parse_select() {
        for (DbType dbType : new DbType[]{DbType.oracle, DbType.mysql}) {
            for (String sql : new String[]{
                "SELECT 'ryan000'\n"
                    + "FROM (SELECT 'ryan111' FROM dualaaa)\n"
                    + "UNION ALL\n"
                    + "SELECT 'ryan222'\n"
                    + "FROM (SELECT 'ryan333' FROM dualbbb);",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                assertEquals(1, statementList.size());
                assertEquals("SELECT 'ryan000'\n"
                    + "FROM (\n"
                    + "\tSELECT 'ryan111'\n"
                    + "\tFROM dualaaa\n"
                    + ")\n"
                    + "UNION ALL\n"
                    + "SELECT 'ryan222'\n"
                    + "FROM (\n"
                    + "\tSELECT 'ryan333'\n"
                    + "\tFROM dualbbb\n"
                    + ");", statementList.get(0).toString());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
