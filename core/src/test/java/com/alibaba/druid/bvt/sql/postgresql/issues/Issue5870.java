package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * PostgreSQL解析SET SCHEMA的问题
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5870">Issue来源</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-set.html">SET SCHEMA</a>
 * @see <a href="https://www.postgresql.org/docs/current/multibyte.html#MULTIBYTE-SETTING">SET NAMES 'value';</a>
 */
public class Issue5870 {

    @Test
    public void test_parse_set_schema() {
        for (String sql : new String[]{
            "SET SCHEMA 'platform_base';",
            "SET search_path TO my_schema, public;",
            "show client_encoding;",
            "set client_encoding = GBK;",
            "set client_encoding to 'UTF-8'",
            "SET CLIENT_ENCODING TO 'value';",
            "SET NAMES 'UTF-8'",
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
        }

    }
}
