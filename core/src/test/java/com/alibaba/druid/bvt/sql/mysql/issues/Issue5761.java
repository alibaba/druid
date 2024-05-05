package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5761>Issue来源</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/with.html">WITH (Common Table Expressions)</a>
 */
public class Issue5761 {

    @Test
    public void test_parse_with() {
        for (DbType dbType : new DbType[]{
            DbType.mysql,
            DbType.mariadb,

        }) {

            for (String sql : new String[]{
                "WITH\n"
                    + "  cte1 AS (SELECT a, b FROM table1),\n"
                    + "  cte2 AS (SELECT c, d FROM table2)\n"
                    + "SELECT b, d FROM cte1 JOIN cte2\n"
                    + "WHERE cte1.a = cte2.c;",
                "WITH cte (col1, col2) AS\n"
                    + "(\n"
                    + "  SELECT 1, 2\n"
                    + "  UNION ALL\n"
                    + "  SELECT 3, 4\n"
                    + ")\n"
                    + "SELECT col1, col2 FROM cte;",
                "WITH cte AS\n"
                    + "(\n"
                    + "  SELECT 1 AS col1, 2 AS col2\n"
                    + "  UNION ALL\n"
                    + "  SELECT 3, 4\n"
                    + ")\n"
                    + "SELECT col1, col2 FROM cte;",
                "WITH cte1 AS (SELECT 1)\n"
                    + "SELECT * FROM (WITH cte2 AS (SELECT 2) SELECT * FROM cte2 JOIN cte1) AS dt;",
                "WITH RECURSIVE cte (n) AS\n"
                    + "(\n"
                    + "  SELECT 1\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1 FROM cte WHERE n < 5\n"
                    + ")\n"
                    + "SELECT * FROM cte;",
                "WITH RECURSIVE cte AS\n"
                    + "(\n"
                    + "  SELECT 1 AS n, 'abc' AS str\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1, CONCAT(str, str) FROM cte WHERE n < 3\n"
                    + ")\n"
                    + "SELECT * FROM cte;",
                "WITH RECURSIVE cte AS\n"
                    + "(\n"
                    + "  SELECT 1 AS n, CAST('abc' AS CHAR(20)) AS str\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1, CONCAT(str, str) FROM cte WHERE n < 3\n"
                    + ")\n"
                    + "SELECT * FROM cte;",
                "WITH RECURSIVE cte AS\n"
                    + "(\n"
                    + "  SELECT 1 AS n, 1 AS p, -1 AS q\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1, q * 2, p * 2 FROM cte WHERE n < 5\n"
                    + ")\n"
                    + "SELECT * FROM cte;",
                "WITH RECURSIVE cte (n) AS\n"
                    + "(\n"
                    + "  SELECT 1\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1 FROM cte\n"
                    + ")\n"
                    + "SELECT * FROM cte;",
                "WITH RECURSIVE cte (n) AS\n"
                    + "(\n"
                    + "  SELECT 1\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1 FROM cte\n"
                    + ")\n"
                    + "SELECT /*+ SET_VAR(cte_max_recursion_depth = 1M) */ * FROM cte;",
                "WITH RECURSIVE cte (n) AS\n"
                    + "(\n"
                    + "  SELECT 1\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1 FROM cte\n"
                    + ")\n"
                    + "SELECT /*+ MAX_EXECUTION_TIME(1000) */ * FROM cte;",
                "WITH RECURSIVE cte (n) AS\n"
                    + "(\n"
                    + "  SELECT 1\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1 FROM cte LIMIT 10000\n"
                    + ")\n"
                    + "SELECT * FROM cte;",
                "WITH RECURSIVE cte (n) AS\n"
                    + "(\n"
                    + "  SELECT 1\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1 FROM cte LIMIT 10000\n"
                    + ")\n"
                    + "SELECT /*+ MAX_EXECUTION_TIME(1000) */ * FROM cte;",
                "WITH RECURSIVE fibonacci (n, fib_n, next_fib_n) AS\n"
                    + "(\n"
                    + "  SELECT 1, 0, 1\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1, next_fib_n, fib_n + next_fib_n\n"
                    + "    FROM fibonacci WHERE n < 10\n"
                    + ")\n"
                    + "SELECT * FROM fibonacci;",
                "WITH RECURSIVE dates (date) AS\n"
                    + "(\n"
                    + "  SELECT MIN(date) FROM sales\n"
                    + "  UNION ALL\n"
                    + "  SELECT date + INTERVAL 1 DAY FROM dates\n"
                    + "  WHERE date + INTERVAL 1 DAY <= (SELECT MAX(date) FROM sales)\n"
                    + ")\n"
                    + "SELECT * FROM dates;",
                "WITH RECURSIVE dates (date) AS\n"
                    + "(\n"
                    + "  SELECT MIN(date) FROM sales\n"
                    + "  UNION ALL\n"
                    + "  SELECT date + INTERVAL 1 DAY FROM dates\n"
                    + "  WHERE date + INTERVAL 1 DAY <= (SELECT MAX(date) FROM sales)\n"
                    + ")\n"
                    + "SELECT dates.date, COALESCE(SUM(price), 0) AS sum_price\n"
                    + "FROM dates LEFT JOIN sales ON dates.date = sales.date\n"
                    + "GROUP BY dates.date\n"
                    + "ORDER BY dates.date;",
                "WITH RECURSIVE employee_paths (id, name, path) AS\n"
                    + "(\n"
                    + "  SELECT id, name, CAST(id AS CHAR(200))\n"
                    + "    FROM employees\n"
                    + "    WHERE manager_id IS NULL\n"
                    + "  UNION ALL\n"
                    + "  SELECT e.id, e.name, CONCAT(ep.path, ',', e.id)\n"
                    + "    FROM employee_paths AS ep JOIN employees AS e\n"
                    + "      ON ep.id = e.manager_id\n"
                    + ")\n"
                    + "SELECT * FROM employee_paths ORDER BY path;",
                "WITH cte AS (SELECT 1) SELECT * FROM cte;",
                "WITH RECURSIVE cte (n) AS\n"
                    + "(\n"
                    + "  SELECT 1\n"
                    + "  UNION ALL\n"
                    + "  SELECT n + 1 FROM cte WHERE n < 5\n"
                    + ")\n"
                    + "SELECT * FROM cte;",
                "select\n"
                    + "        id,\n"
                    + "        (aa),\n"
                    + "        (bb) cc,\n"
                    + "        (\n"
                    + "            WITH RECURSIVE link_hierarchy AS (\n"
                    + "                SELECT id, parent_id\n"
                    + "                FROM tmp_link\n"
                    + "                WHERE id = ?\n"
                    + "\n"
                    + "                UNION ALL\n"
                    + "\n"
                    + "                SELECT tl.id, tl.parent_id\n"
                    + "                FROM tmp_link tl\n"
                    + "                INNER JOIN link_hierarchy lh ON tl.id = lh.parent_id\n"
                    + "            )\n"
                    + "            SELECT CONCAT('/', GROUP_CONCAT(id ORDER BY id ASC SEPARATOR '/')) AS pathaaa\n"
                    + "            FROM link_hierarchy\n"
                    + "        ) as pathbbb , qqqq\n"
                    + "        from tmp_link;",

                "select\n"
                    + "        (\n"
                    + "            WITH RECURSIVE link_hierarchy AS (\n"
                    + "                SELECT id, parent_id\n"
                    + "                FROM tmp_link\n"
                    + "                WHERE id = ?\n"
                    + "\n"
                    + "                UNION ALL\n"
                    + "\n"
                    + "                SELECT tl.id, tl.parent_id\n"
                    + "                FROM tmp_link tl\n"
                    + "                INNER JOIN link_hierarchy lh ON tl.id = lh.parent_id\n"
                    + "            )\n"
                    + "            SELECT CONCAT('/', GROUP_CONCAT(id ORDER BY id ASC SEPARATOR '/')) AS pathaaa\n"
                    + "            FROM link_hierarchy\n"
                    + "        ) as pathbbb \n"
                    + "        from tmp_link;",

                "select\n"
                    + "        (\n"
                    + "            WITH RECURSIVE link_hierarchy AS (\n"
                    + "                SELECT id, parent_id\n"
                    + "                FROM tmp_link\n"
                    + "                WHERE id = ?\n"
                    + "\n"
                    + "                UNION ALL\n"
                    + "\n"
                    + "                SELECT tl.id, tl.parent_id\n"
                    + "                FROM tmp_link tl\n"
                    + "                INNER JOIN link_hierarchy lh ON tl.id = lh.parent_id\n"
                    + "            )\n"
                    + "            SELECT CONCAT('/', GROUP_CONCAT(id ORDER BY id ASC SEPARATOR '/')) AS pathaaa\n"
                    + "            FROM link_hierarchy\n"
                    + "        ) as pathbbb , qqqq\n"
                    + "        from tmp_link;",

                "select\n"
                    + "        (\n"
                    + "            WITH link_hierarchy AS (\n"
                    + "                SELECT id, parent_id\n"
                    + "                FROM tmp_link\n"
                    + "                WHERE id = ?\n"
                    + "\n"
                    + "                UNION ALL\n"
                    + "\n"
                    + "                SELECT tl.id, tl.parent_id\n"
                    + "                FROM tmp_link tl\n"
                    + "                INNER JOIN link_hierarchy lh ON tl.id = lh.parent_id\n"
                    + "            )\n"
                    + "            SELECT CONCAT('/', GROUP_CONCAT(id ORDER BY id ASC SEPARATOR '/')) AS pathaaa\n"
                    + "            FROM link_hierarchy\n"
                    + "        ) as pathbbb , qwerty\n"
                    + "        from tmp_link;",


                "select\n"
                    + "        id,\n"
                    + "        (aa),\n"
                    + "        (bb) cc,\n"
                    + "        (\n"
                    + "            WITH RECURSIVE link_hierarchy AS (\n"
                    + "                SELECT id, parent_id\n"
                    + "                FROM tmp_link\n"
                    + "                WHERE id = ?\n"
                    + "\n"
                    + "                UNION ALL\n"
                    + "\n"
                    + "                SELECT tl.id, tl.parent_id\n"
                    + "                FROM tmp_link tl\n"
                    + "                INNER JOIN link_hierarchy lh ON tl.id = lh.parent_id\n"
                    + "            )\n"
                    + "            SELECT CONCAT('/', GROUP_CONCAT(id ORDER BY id ASC SEPARATOR '/')) AS pathaaa\n"
                    + "            FROM link_hierarchy\n"
                    + "        ) as pathbbb\n"
                    + "        from tmp_link;",
            }) {
                System.out.println(dbType + "原始的sql===" + sql);
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                String sqlGen = statementList.toString();
                System.out.println(dbType + "首次解析生成的sql===" + sqlGen);
                StringBuilder sb = new StringBuilder();
                for (SQLStatement statement : statementList) {
                    sb.append(statement.toString()).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                parser = SQLParserUtils.createSQLStatementParser(sb.toString(), dbType);
                List<SQLStatement> statementListNew = parser.parseStatementList();
                String sqlGenNew = statementList.toString();
                System.out.println(dbType + "再次解析生成的sql===" + sqlGenNew);
                assertEquals(statementList.toString(), statementListNew.toString());
            }
        }
    }
}
