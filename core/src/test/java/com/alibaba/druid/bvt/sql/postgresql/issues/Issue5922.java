package com.alibaba.druid.bvt.sql.postgresql.issues;

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
 * @see <a href="https://github.com/alibaba/druid/issues/5922>Issue来源</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-dropfunction.html">DROP FUNCTION</a>
 */
public class Issue5922 {

    @Test
    public void test_parse_drop_function() {
        for (DbType dbType : new DbType[]{DbType.postgresql, DbType.edb}) {
            for (String sql : new String[]{
                "DROP FUNCTION IF EXISTS add_numbers(a integer);",
                "DROP FUNCTION add_numbers;",
                "DROP FUNCTION add_numbers();",
                "DROP FUNCTION sqrt(integer);",
                "DROP FUNCTION IF EXISTS add_numbers(a integer,b integer);",
                "DROP FUNCTION IF EXISTS add_numbers(a ,b);",
                "DROP FUNCTION IF EXISTS add_numbers(a ,b integer,c );",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                assertEquals(1, statementList.size());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
