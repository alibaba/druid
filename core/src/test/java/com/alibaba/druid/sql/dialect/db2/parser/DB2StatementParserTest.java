package com.alibaba.druid.sql.dialect.db2.parser;

import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import junit.framework.TestCase;

/**
 * 验证 db2 SQL 解析器
 *
 * @author abomb4 2022-08-29
 */
public class DB2StatementParserTest extends TestCase {

    /**
     * 测试修改了 parseDeleteStatement 后 delete 语句能否正常解析
     */
    public void testDelete() {
        final String[] caseList = new String[]{
                // 普通语句
                "delete from aaa where 1 = 1 and id = 2",
                // 别名语句
                "delete from test aaa where 1 = 1 and aaa.id = 2",
                // 子查询语句
                "delete from test aaa where exists ( select 1 from b where b.status = 'a' )",
        };

        for (int i = 0; i < caseList.length; i++) {
            final String sql = caseList[i];
            final DB2StatementParser parser = new DB2StatementParser(sql);
            final SQLDeleteStatement parsed = parser.parseDeleteStatement();
            final String result = parsed.toUnformattedString().replaceAll("\\s+", " ").toLowerCase();
            assertEquals("第 " + (i + 1) + "个用例验证失败", sql, result);
        }
    }
}
