package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 优化括号解析之后的sql验证
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5847">Issue来源</a>
 */
public class Issue5847 {

    @Test
    public void test_parse() {
        for (String sql : new String[]{
            "select * from tb_test limit 10 offset ( (2 - 1) * 1 );",
        }) {
            SQLStatementParser parser1 = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
            List<SQLStatement> statementList1 = parser1.parseStatementList();
            System.out.println("原始的sql===" + sql);
            String sqleNew = statementList1.get(0).toString();
            System.out.println("生成的sql===" + sqleNew);
            SQLStatementParser parser2 = SQLParserUtils.createSQLStatementParser(sqleNew, DbType.postgresql);
            List<SQLStatement> statementList2 = parser2.parseStatementList();
            String sqleNew2 = statementList2.get(0).toString();
            System.out.println("再次解析生成的sql===" + sqleNew);
            assertEquals(sqleNew, sqleNew2);

            assertEquals("SELECT *\n"
                + "FROM tb_test\n"
                + "LIMIT 10 OFFSET ((2 - 1) * 1);", sqleNew2);

            String formattedSql = SQLUtils.format(sql, DbType.dm);
            System.out.println(formattedSql);
            assertEquals("SELECT *\n"
                + "FROM tb_test\n"
                + "LIMIT 10 OFFSET ((2 - 1) * 1);", formattedSql);

        }
    }
}
