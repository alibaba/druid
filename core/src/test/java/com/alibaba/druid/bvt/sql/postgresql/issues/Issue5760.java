package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5760">Issue来源</a>
 */
public class Issue5760 {

    @Test
    public void test_parse_error_sql() {
        for (String sql : new String[]{
            "Vacuum verbose    ",
            "Vacuum verbose;",
            "Vacuum verbose full",
            "Vacuum verbose full;",
            "Vacuum verbose; select a from b", "Vacuum verbose full"
            + ";"
            + "Vacuum verbose bbbb;",
        }) {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
            List<SQLStatement> statementList = parser.parseStatementList();
            System.out.println("原始的sql===" + sql);
            System.out.println("生成的sql===" + statementList);

        }
    }
}
