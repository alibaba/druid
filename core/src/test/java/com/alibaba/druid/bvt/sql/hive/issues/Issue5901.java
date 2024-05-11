package com.alibaba.druid.bvt.sql.hive.issues;

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
 * @see <a href="https://github.com/alibaba/druid/issues/5901>Issue来源</a>
 */
public class Issue5901 {

    @Test
    public void test_parse_set() {
        for (DbType dbType : new DbType[]{DbType.hive}) {
            for (String sql : new String[]{
                "SET hivevar:account_period = IF(${hivevar:now_month} >= 3 and ${hivevar:now_month} < 6, CONCAT(${hivevar:now_year}, '-03XYZ'), ${hivevar:now_year});\n"
                ,
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                assertEquals(1, statementList.size());
                assertEquals(
                    "SET hivevar:account_period = IF(${hivevar:now_month} >= 3 AND ${hivevar:now_month} < 6, CONCAT(${hivevar:now_year}, '-03XYZ'), ${hivevar:now_year});"
                    , statementList.get(0).toString());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }

}
