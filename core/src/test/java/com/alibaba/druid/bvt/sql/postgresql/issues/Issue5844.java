package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * PostgreSQL解析TABLESAMPLE问题
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5844">Issue来源</a>
 * @see <a href="https://www.postgresql.org/docs/current/tsm-system-rows.html"> the SYSTEM_ROWS sampling method for TABLESAMPLE </a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-select.html#SQL-TABLESAMPLE">TABLESAMPLE</a>
 */
public class Issue5844 {

    @Test
    public void test_parse_postgresql_tablesample() {
        for (String sql : new String[]{
            "SELECT * FROM app_qxx_zh TABLESAMPLE SYSTEM ( 5 )\n"
                + "WHERE random( ) < 0.01\n"
                + "ORDER BY show_count LIMIT 20",
            "SELECT * FROM app_qxx_zh TABLESAMPLE BERNOULLI ( 0.01 )\n"
                + "WHERE random( ) < 0.01\n"
                + "ORDER BY show_count LIMIT 20",
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
