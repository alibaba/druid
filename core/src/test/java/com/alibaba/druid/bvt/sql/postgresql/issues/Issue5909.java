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
 * @see <a href="https://github.com/alibaba/druid/issues/5909>Issue来源</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-altertable.html">...</a>
 */
public class Issue5909 {

    @Test
    public void test_parse_alter_column() {
        for (DbType dbType : new DbType[]{DbType.postgresql, DbType.edb}) {
            for (String sql : new String[]{
                "ALTER TABLE mobino.alarm_count_day ALTER COLUMN warn_day TYPE character varying(100);",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                assertEquals(1, statementList.size());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
