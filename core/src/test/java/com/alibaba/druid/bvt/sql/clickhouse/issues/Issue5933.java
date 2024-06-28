package com.alibaba.druid.bvt.sql.clickhouse.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5933" >Issue来源</a>
 * @see <a href="https://clickhouse.com/docs/en/sql-reference/statements/select/array-join">ARRAY JOIN Clause</a>
 */
public class Issue5933 {

    @Test
    public void test_parse_arrauy_join_0() {
        for (DbType dbType : new DbType[]{DbType.clickhouse}) {
            for (String sql : new String[]{
                "SELECT * FROM base_customized_cost "
                    + "ARRAY JOIN split.service AS service "
                    + "WHERE capture_time >= DATE('2024-04-01') "
                    + "AND capture_time < DATE('2024-05-01') LIMIT 100;",
            }) {
                System.out.println("最原始SQL===" + sql);
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println(statementList);
                assertEquals(1, statementList.size());
                SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
                SQLJoinTableSource sts = (SQLJoinTableSource) stmt.getSelect().getQueryBlock().getFrom();
                System.out.println("JOIN类型 " + sts.getJoinType());
                assertEquals("ARRAY JOIN", sts.getJoinType().name);
                assertEquals("SELECT *\n"
                    + "FROM base_customized_cost\n"
                    + "\tARRAY JOIN split.service AS service\n"
                    + "WHERE capture_time >= DATE('2024-04-01')\n"
                    + "\tAND capture_time < DATE('2024-05-01')\n"
                    + "LIMIT 100;", stmt.toString());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }

    @Test
    public void test_parse_arrauy_join_all() {
        for (DbType dbType : new DbType[]{DbType.clickhouse}) {
            for (String sql : new String[]{
                "SELECT s, arr\n"
                    + "FROM arrays_test\n"
                    + "LEFT ARRAY JOIN arr;",
                "SELECT s, arr\n"
                    + "FROM arrays_test\n"
                    + "ARRAY JOIN arr;",
                "SELECT s, arr\n"
                    + "FROM arrays_test\n"
                    + "LEFT ARRAY JOIN arr;",
                "SELECT s, arr, a\n"
                    + "FROM arrays_test\n"
                    + "ARRAY JOIN arr AS a;",
                "SELECT s, arr_external\n"
                    + "FROM arrays_test\n"
                    + "ARRAY JOIN [1, 2, 3] AS arr_external;",
                "SELECT s, arr, a, num, mapped\n"
                    + "FROM arrays_test\n"
                    + "ARRAY JOIN arr AS a, arrayEnumerate(arr) AS num, arrayMap(x -> x + 1, arr) AS mapped;",
                "SELECT s, arr, a, num, arrayEnumerate(arr)\n"
                    + "FROM arrays_test\n"
                    + "ARRAY JOIN arr AS a, arrayEnumerate(arr) AS num;",
//                "SELECT s, arr, a, b\n"
//                    + "FROM arrays_test ARRAY JOIN arr as a, [['a','b'],['c']] as b\n"
//                    + "SETTINGS enable_unaligned_array_join = 1;",
                "SELECT s, `nest.x`, `nest.y`\n"
                    + "FROM nested_test\n"
                    + "ARRAY JOIN nest;",
                "SELECT s, `nest.x`, `nest.y`\n"
                    + "FROM nested_test\n"
                    + "ARRAY JOIN `nest.x`, `nest.y`;",
                "SELECT s, `nest.x`, `nest.y`\n"
                    + "FROM nested_test\n"
                    + "ARRAY JOIN `nest.x`;",
                "SELECT s, `n.x`, `n.y`, `nest.x`, `nest.y`\n"
                    + "FROM nested_test\n"
                    + "ARRAY JOIN nest AS n;",
                "SELECT s, `n.x`, `n.y`, `nest.x`, `nest.y`, num\n"
                    + "FROM nested_test\n"
                    + "ARRAY JOIN nest AS n, arrayEnumerate(`nest.x`) AS num;",
                "SELECT arrayJoin([1, 2, 3] AS src) AS dst, 'Hello', src",
            }) {
                System.out.println("最原始SQL===" + sql);
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println(statementList);
                assertEquals(1, statementList.size());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
