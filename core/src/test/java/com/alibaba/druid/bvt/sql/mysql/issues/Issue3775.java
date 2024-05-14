package com.alibaba.druid.bvt.sql.mysql.issues;

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
 * @see <a href="https://github.com/alibaba/druid/issues/3775>Issue来源</a>
 */
public class Issue3775 {
    @Test
    public void test_parse_parentheses() {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                "SELECT ((700055789 % (66666*4444))%8888);",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println(statementList);
                assertEquals(1, statementList.size());
                assertEquals("SELECT ((700055789 % (66666 * 4444)) % 8888);", statementList.get(0).toString());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
