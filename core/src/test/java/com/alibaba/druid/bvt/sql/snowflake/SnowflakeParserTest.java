package com.alibaba.druid.bvt.sql.snowflake;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;

import java.util.List;

public class SnowflakeParserTest extends TestCase {
    private final DbType dbType = DbType.snowflake;

    private String parse(String sql) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        return SQLUtils.toSQLString(stmtList, dbType).trim();
    }

    // ==================== Cast Operators ====================

    public void testCastOperator() {
        String sql = "SELECT a::VARCHAR FROM t1";
        String result = parse(sql);
        assertEquals("SELECT CAST(a AS VARCHAR)\nFROM t1", result);
    }

    public void testChainedCast() {
        String sql = "SELECT a::INTEGER::VARCHAR FROM t1";
        String result = parse(sql);
        assertEquals("SELECT CAST(CAST(a AS INTEGER) AS VARCHAR)\nFROM t1", result);
    }

    public void testTryCast() {
        String sql = "SELECT TRY_CAST('abc' AS INTEGER)";
        String result = parse(sql);
        assertEquals("SELECT TRY_CAST('abc' AS INTEGER)", result);
    }

    // ==================== ILIKE ====================

    public void testILike() {
        String sql = "SELECT * FROM t1 WHERE name ILIKE '%test%'";
        String result = parse(sql);
        assertTrue(result.contains("ILIKE '%test%'"));
    }

    // ==================== QUALIFY ====================

    public void testQualify() {
        String sql = "SELECT * FROM t1 QUALIFY ROW_NUMBER() OVER (PARTITION BY id ORDER BY ts DESC) = 1";
        String result = parse(sql);
        assertTrue(result.contains("QUALIFY"));
    }

    // ==================== GROUP BY ALL ====================

    public void testGroupByAll() {
        String sql = "SELECT a, b, SUM(c) FROM t1 GROUP BY ALL";
        String result = parse(sql);
        assertTrue(result.contains("GROUP BY ALL"));
    }

    // ==================== CREATE TABLE Variants ====================

    public void testCreateOrReplaceTable() {
        String sql = "CREATE OR REPLACE TABLE my_table (id INTEGER, name VARCHAR)";
        String result = parse(sql);
        assertTrue(result.contains("CREATE OR REPLACE TABLE"));
    }

    public void testCreateTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS my_table (id INTEGER)";
        String result = parse(sql);
        assertTrue(result.contains("IF NOT EXISTS"));
    }

    public void testCreateTableAsSelect() {
        String sql = "CREATE TABLE my_table AS SELECT * FROM other_table";
        String result = parse(sql);
        assertTrue(result.contains("AS"));
        assertTrue(result.contains("SELECT *"));
    }

    public void testCreateTransientTable() {
        String sql = "CREATE TRANSIENT TABLE temp_data (id INTEGER, val VARCHAR)";
        String result = parse(sql);
        assertTrue(result.contains("TABLE"));
    }

    public void testCreateTableLike() {
        String sql = "CREATE TABLE new_table LIKE existing_table";
        String result = parse(sql);
        assertTrue(result.contains("CREATE TABLE"));
    }

    public void testCreateTableClone() {
        String sql = "CREATE TABLE new_table CLONE existing_table";
        String result = parse(sql);
        assertTrue(result.contains("CREATE TABLE new_table"));
    }

    public void testCreateTemporaryTable() {
        String sql = "CREATE TEMPORARY TABLE temp_t (id INTEGER, val VARCHAR)";
        String result = parse(sql);
        assertTrue(result.contains("TEMPORARY"));
        assertTrue(result.contains("TABLE"));
    }

    // ==================== CREATE/DROP VIEW ====================

    public void testCreateOrReplaceView() {
        String sql = "CREATE OR REPLACE VIEW my_view AS SELECT id FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("CREATE OR REPLACE VIEW"));
    }

    // ==================== MERGE ====================

    public void testMerge() {
        String sql = "MERGE INTO t1 USING t2 ON t1.id = t2.id "
                + "WHEN MATCHED THEN UPDATE SET t1.name = t2.name "
                + "WHEN NOT MATCHED THEN INSERT (id, name) VALUES (t2.id, t2.name)";
        String result = parse(sql);
        assertTrue(result.contains("MERGE INTO"));
        assertTrue(result.contains("WHEN MATCHED"));
        assertTrue(result.contains("WHEN NOT MATCHED"));
    }

    // ==================== PIVOT/UNPIVOT ====================

    public void testPivot() {
        String sql = "SELECT * FROM t1 PIVOT (SUM(amount) FOR month IN ('Jan', 'Feb'))";
        String result = parse(sql);
        assertTrue(result.contains("PIVOT"));
    }

    public void testUnpivot() {
        String sql = "SELECT * FROM t1 UNPIVOT (val FOR col IN (a, b, c))";
        String result = parse(sql);
        assertTrue(result.contains("UNPIVOT"));
    }

    // ==================== CTE ====================

    public void testCTE() {
        String sql = "WITH cte AS (SELECT 1 AS id) SELECT * FROM cte";
        String result = parse(sql);
        assertTrue(result.contains("WITH cte"));
    }

    public void testRecursiveCTE() {
        String sql = "WITH RECURSIVE cte AS (SELECT 1 AS n UNION ALL SELECT n + 1 FROM cte WHERE n < 10) SELECT * FROM cte";
        String result = parse(sql);
        assertTrue(result.contains("RECURSIVE"));
    }

    // ==================== GROUP BY variants ====================

    public void testGroupByCube() {
        String sql = "SELECT a, b, SUM(c) FROM t1 GROUP BY CUBE (a, b)";
        String result = parse(sql);
        assertTrue(result.contains("CUBE"));
    }

    public void testGroupByRollup() {
        String sql = "SELECT a, b, SUM(c) FROM t1 GROUP BY ROLLUP (a, b)";
        String result = parse(sql);
        assertTrue(result.contains("ROLLUP"));
    }

    // ==================== ORDER BY NULLS ====================

    public void testNullsFirstLast() {
        String sql = "SELECT * FROM t1 ORDER BY a ASC NULLS FIRST, b DESC NULLS LAST";
        String result = parse(sql);
        assertTrue(result.contains("NULLS FIRST"));
        assertTrue(result.contains("NULLS LAST"));
    }

    // ==================== DML ====================

    public void testDropTableIfExists() {
        String sql = "DROP TABLE IF EXISTS my_table";
        String result = parse(sql);
        assertEquals("DROP TABLE IF EXISTS my_table", result);
    }

    public void testAlterTableAddColumn() {
        String sql = "ALTER TABLE t1 ADD COLUMN name VARCHAR(256)";
        String result = parse(sql);
        assertTrue(result.contains("ALTER TABLE"));
        assertTrue(result.contains("name VARCHAR(256)"));
    }

    public void testInsertValues() {
        String sql = "INSERT INTO t1 (id, name) VALUES (1, 'Alice')";
        String result = parse(sql);
        assertTrue(result.contains("INSERT INTO"));
        assertTrue(result.contains("VALUES"));
    }

    public void testInsertSelect() {
        String sql = "INSERT INTO t1 SELECT * FROM t2";
        String result = parse(sql);
        assertTrue(result.contains("INSERT INTO"));
        assertTrue(result.contains("SELECT *"));
    }

    public void testUpdateWithWhere() {
        String sql = "UPDATE t1 SET name = 'test' WHERE id = 1";
        String result = parse(sql);
        assertTrue(result.contains("UPDATE t1"));
        assertTrue(result.contains("SET name = 'test'"));
        assertTrue(result.contains("WHERE id = 1"));
    }

    public void testDeleteWithWhere() {
        String sql = "DELETE FROM t1 WHERE status = 'inactive'";
        String result = parse(sql);
        assertTrue(result.contains("DELETE FROM"));
        assertTrue(result.contains("WHERE status = 'inactive'"));
    }

    // ==================== Window Functions ====================

    public void testWindowFunctions() {
        String sql = "SELECT id, SUM(amount) OVER (PARTITION BY dept ORDER BY id ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("OVER"));
        assertTrue(result.contains("PARTITION BY"));
        assertTrue(result.contains("ROWS BETWEEN"));
    }

    // ==================== Set Operations ====================

    public void testUnionAllIntersectExcept() {
        String sql = "SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3";
        String result = parse(sql);
        assertTrue(result.contains("UNION ALL"));
    }

    // ==================== Subquery ====================

    public void testSubquery() {
        String sql = "SELECT * FROM (SELECT id FROM t1) sub WHERE sub.id > 5";
        String result = parse(sql);
        assertTrue(result.contains("FROM ("));
    }

    // ==================== Aggregate Functions ====================

    public void testListagg() {
        String sql = "SELECT LISTAGG(name, ', ') WITHIN GROUP (ORDER BY name) FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("LISTAGG"));
        assertTrue(result.contains("WITHIN GROUP"));
    }

    // ==================== Date Functions ====================

    public void testDateFunctions() {
        String sql = "SELECT DATEADD(day, 1, CURRENT_DATE()), DATEDIFF(day, '2024-01-01', '2024-12-31')";
        String result = parse(sql);
        assertTrue(result.contains("DATEADD"));
        assertTrue(result.contains("DATEDIFF"));
    }

    // ==================== CASE Expression ====================

    public void testCaseExpression() {
        String sql = "SELECT CASE WHEN x = 1 THEN 'a' WHEN x = 2 THEN 'b' ELSE 'c' END FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("CASE"));
        assertTrue(result.contains("WHEN"));
        assertTrue(result.contains("ELSE"));
    }

    // ==================== Multiple JOINs ====================

    public void testMultipleJoins() {
        String sql = "SELECT a.id, b.name, c.value FROM t1 a INNER JOIN t2 b ON a.id = b.aid LEFT JOIN t3 c ON b.id = c.bid";
        String result = parse(sql);
        assertTrue(result.contains("INNER JOIN"));
        assertTrue(result.contains("LEFT JOIN"));
    }

    // ==================== EXISTS Subquery ====================

    public void testExistsSubquery() {
        String sql = "SELECT * FROM t1 WHERE EXISTS (SELECT 1 FROM t2 WHERE t1.id = t2.fk)";
        String result = parse(sql);
        assertTrue(result.contains("EXISTS"));
    }

    // ==================== DISTINCT ====================

    public void testDistinct() {
        String sql = "SELECT DISTINCT a, b FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("DISTINCT"));
    }

    // ==================== LIMIT/OFFSET ====================

    public void testLimit() {
        String sql = "SELECT * FROM t1 LIMIT 10";
        String result = parse(sql);
        assertTrue(result.contains("LIMIT 10"));
    }

    public void testLimitOffset() {
        String sql = "SELECT * FROM t1 LIMIT 10 OFFSET 20";
        String result = parse(sql);
        assertTrue(result.contains("LIMIT 10"));
    }

    // ==================== TRUNCATE TABLE ====================

    public void testTruncateTable() {
        String sql = "TRUNCATE TABLE my_table";
        String result = parse(sql);
        assertEquals("TRUNCATE TABLE my_table", result);
    }

    // ==================== NEW: COPY INTO ====================

    public void testCopyIntoFromStage() {
        String sql = "COPY INTO my_table FROM '@my_stage/data/'";
        String result = parse(sql);
        assertTrue(result.contains("COPY"));
        assertTrue(result.contains("my_table"));
    }

    public void testCopyIntoWithFileFormat() {
        String sql = "COPY INTO my_table FROM '@my_stage/' FILE_FORMAT = (TYPE = CSV FIELD_DELIMITER = ',' SKIP_HEADER = 1)";
        String result = parse(sql);
        assertTrue(result.contains("COPY"));
        assertTrue(result.contains("my_table"));
    }

    // ==================== NEW: USE ====================

    public void testUseDatabase() {
        String sql = "USE DATABASE my_db";
        String result = parse(sql);
        assertTrue(result.contains("USE"));
        assertTrue(result.contains("my_db"));
    }

    public void testUseSchema() {
        String sql = "USE SCHEMA my_schema";
        String result = parse(sql);
        assertTrue(result.contains("USE"));
        assertTrue(result.contains("my_schema"));
    }

    public void testUseWarehouse() {
        String sql = "USE WAREHOUSE my_wh";
        String result = parse(sql);
        assertTrue(result.contains("USE"));
        assertTrue(result.contains("my_wh"));
    }

    public void testUseRole() {
        String sql = "USE ROLE my_role";
        String result = parse(sql);
        assertTrue(result.contains("USE"));
        assertTrue(result.contains("my_role"));
    }

    // ==================== NEW: DESCRIBE ====================

    public void testDescribeTable() {
        String sql = "DESCRIBE TABLE my_table";
        String result = parse(sql);
        assertTrue(result.contains("my_table"));
    }

    public void testDescTable() {
        String sql = "DESC TABLE my_table";
        String result = parse(sql);
        assertTrue(result.contains("my_table"));
    }

    // ==================== NEW: CALL ====================

    public void testCallProcedure() {
        String sql = "CALL my_procedure('arg1', 42)";
        String result = parse(sql);
        assertTrue(result.contains("CALL"));
        assertTrue(result.contains("my_procedure"));
    }

    // ==================== NEW: SHOW variants ====================

    public void testShowTables() {
        String sql = "SHOW TABLES";
        String result = parse(sql);
        assertTrue(result.contains("SHOW"));
    }

    public void testShowTablesLike() {
        String sql = "SHOW TABLES LIKE '%test%'";
        String result = parse(sql);
        assertTrue(result.contains("SHOW"));
    }

    public void testShowTablesInDatabase() {
        String sql = "SHOW TABLES IN DATABASE my_db";
        String result = parse(sql);
        assertTrue(result.contains("SHOW"));
    }

    public void testShowDatabases() {
        String sql = "SHOW DATABASES";
        String result = parse(sql);
        assertTrue(result.contains("SHOW"));
    }

    public void testShowSchemas() {
        String sql = "SHOW SCHEMAS IN DATABASE my_db";
        String result = parse(sql);
        assertTrue(result.contains("SHOW"));
    }

    public void testShowViews() {
        String sql = "SHOW VIEWS IN SCHEMA my_schema";
        String result = parse(sql);
        assertTrue(result.contains("SHOW"));
    }

    public void testShowColumns() {
        String sql = "SHOW COLUMNS IN TABLE my_table";
        String result = parse(sql);
        assertTrue(result.contains("SHOW"));
    }

    public void testShowWarehouses() {
        String sql = "SHOW WAREHOUSES LIKE '%prod%'";
        String result = parse(sql);
        assertTrue(result.contains("SHOW"));
    }

    public void testShowGrants() {
        String sql = "SHOW GRANTS ON TABLE my_table";
        String result = parse(sql);
        assertTrue(result.contains("SHOW"));
    }

    // ==================== NEW: TABLE(FLATTEN(...)) ====================

    public void testTableFlatten() {
        String sql = "SELECT f.value FROM TABLE(FLATTEN(INPUT => PARSE_JSON('[1,2,3]'))) f";
        String result = parse(sql);
        assertTrue(result.contains("FLATTEN"));
        assertTrue(result.contains("PARSE_JSON"));
    }

    // ==================== NEW: LATERAL FLATTEN ====================

    public void testLateralFlatten() {
        String sql = "SELECT t1.id, f.value FROM t1, LATERAL FLATTEN(INPUT => t1.data) f";
        String result = parse(sql);
        assertTrue(result.contains("FLATTEN"));
    }

    // ==================== NEW: Semi-structured data access ====================

    public void testVariantPathAccess() {
        String sql = "SELECT src:name FROM variant_table";
        String result = parse(sql);
        assertTrue(result.contains("src"));
        assertTrue(result.contains("name"));
    }

    public void testVariantPathAccessWithCast() {
        String sql = "SELECT src:name::VARCHAR FROM variant_table";
        String result = parse(sql);
        assertTrue(result.contains("CAST"));
        assertTrue(result.contains("VARCHAR"));
    }

    public void testVariantDeepPathAccess() {
        String sql = "SELECT src:address.city FROM variant_table";
        String result = parse(sql);
        assertTrue(result.contains("address"));
        assertTrue(result.contains("city"));
    }

    // ==================== NEW: EXECUTE IMMEDIATE ====================

    public void testExecuteImmediate() {
        String sql = "EXECUTE IMMEDIATE 'SELECT 1'";
        String result = parse(sql);
        assertTrue(result.contains("CALL"));
        assertTrue(result.contains("'SELECT 1'"));
    }

    // ==================== NEW: BEGIN/END Block ====================

    public void testBeginTransaction() {
        String sql = "BEGIN TRANSACTION";
        String result = parse(sql);
        assertNotNull(result);
    }

    // ==================== NEW: DELETE USING ====================

    public void testDeleteUsing() {
        String sql = "DELETE FROM t1 USING t2 WHERE t1.id = t2.id";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLDeleteStatement deleteStmt = (SQLDeleteStatement) stmtList.get(0);
        assertNotNull(deleteStmt.getUsing());
        assertTrue(deleteStmt.getUsing().toString().contains("t2"));
    }

    // ==================== NEW: INSERT OVERWRITE ====================

    public void testInsertOverwrite() {
        String sql = "INSERT OVERWRITE INTO t1 SELECT * FROM t2";
        String result = parse(sql);
        assertTrue(result.contains("INSERT"));
        assertTrue(result.contains("SELECT *"));
    }

    // ==================== NEW: CREATE TABLE with options ====================

    public void testCreateTableWithClusterBy() {
        String sql = "CREATE TABLE t1 (id INTEGER, ts TIMESTAMP) CLUSTER BY (id)";
        String result = parse(sql);
        assertTrue(result.contains("CREATE TABLE"));
        // Druid outputs CLUSTERED BY
        assertTrue(result.contains("CLUSTERED BY") || result.contains("CLUSTER BY"));
    }

    public void testCreateTableWithComment() {
        String sql = "CREATE TABLE t1 (id INTEGER) COMMENT = 'test table'";
        String result = parse(sql);
        assertTrue(result.contains("CREATE TABLE"));
        assertTrue(result.contains("COMMENT"));
    }

    // ==================== NEW: Snowflake Functions ====================

    public void testObjectConstruct() {
        String sql = "SELECT OBJECT_CONSTRUCT('key1', 'val1', 'key2', 'val2')";
        String result = parse(sql);
        assertTrue(result.contains("OBJECT_CONSTRUCT"));
    }

    public void testArrayConstruct() {
        String sql = "SELECT ARRAY_CONSTRUCT(1, 2, 3)";
        String result = parse(sql);
        assertTrue(result.contains("ARRAY_CONSTRUCT"));
    }

    public void testParseJson() {
        String sql = "SELECT PARSE_JSON('{\"a\": 1}')";
        String result = parse(sql);
        assertTrue(result.contains("PARSE_JSON"));
    }

    public void testIff() {
        String sql = "SELECT IFF(a > 0, 'positive', 'negative') FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("IFF"));
    }

    public void testNvl() {
        String sql = "SELECT NVL(name, 'unknown') FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("NVL"));
    }

    public void testCoalesce() {
        String sql = "SELECT COALESCE(a, b, c) FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("COALESCE"));
    }

    public void testDecode() {
        String sql = "SELECT DECODE(status, 1, 'Active', 2, 'Inactive', 'Unknown') FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("DECODE"));
    }

    // ==================== NEW: Window Functions with QUALIFY ====================

    public void testQualifyWithRank() {
        String sql = "SELECT id, name, RANK() OVER (ORDER BY score DESC) AS rnk FROM students QUALIFY rnk <= 3";
        String result = parse(sql);
        assertTrue(result.contains("QUALIFY"));
        assertTrue(result.contains("RANK()"));
    }

    // ==================== NEW: Multiple statements ====================

    public void testMultipleStatements() {
        String sql = "SELECT 1; SELECT 2;";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(2, stmtList.size());
    }

    // ==================== NEW: Natural Join ====================

    public void testNaturalJoin() {
        String sql = "SELECT * FROM t1 NATURAL JOIN t2";
        String result = parse(sql);
        assertTrue(result.contains("NATURAL JOIN"));
    }

    // ==================== NEW: Cross Join ====================

    public void testCrossJoin() {
        String sql = "SELECT * FROM t1 CROSS JOIN t2";
        String result = parse(sql);
        assertTrue(result.contains("CROSS JOIN"));
    }

    // ==================== NEW: EXCEPT/MINUS ====================

    public void testExcept() {
        String sql = "SELECT id FROM t1 EXCEPT SELECT id FROM t2";
        String result = parse(sql);
        assertTrue(result.contains("EXCEPT"));
    }

    public void testMinus() {
        String sql = "SELECT id FROM t1 MINUS SELECT id FROM t2";
        String result = parse(sql);
        assertNotNull(result);
    }

    // ==================== NEW: HAVING ====================

    public void testHaving() {
        String sql = "SELECT dept, COUNT(*) cnt FROM emp GROUP BY dept HAVING cnt > 5";
        String result = parse(sql);
        assertTrue(result.contains("HAVING"));
    }

    // ==================== NEW: IN subquery ====================

    public void testInSubquery() {
        String sql = "SELECT * FROM t1 WHERE id IN (SELECT id FROM t2)";
        String result = parse(sql);
        assertTrue(result.contains("IN ("));
    }

    // ==================== NEW: BETWEEN ====================

    public void testBetween() {
        String sql = "SELECT * FROM t1 WHERE id BETWEEN 1 AND 100";
        String result = parse(sql);
        assertTrue(result.contains("BETWEEN"));
    }

    // ==================== NEW: LIKE ====================

    public void testLike() {
        String sql = "SELECT * FROM t1 WHERE name LIKE 'John%'";
        String result = parse(sql);
        assertTrue(result.contains("LIKE"));
    }

    // ==================== NEW: ARRAY_AGG ====================

    public void testArrayAgg() {
        String sql = "SELECT ARRAY_AGG(name) WITHIN GROUP (ORDER BY name) FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("ARRAY_AGG"));
    }

    // ==================== ALTER SESSION ====================

    public void testAlterSessionSet() {
        String sql = "ALTER SESSION SET QUERY_TAG = 'my_query'";
        String result = parse(sql);
        assertTrue(result.contains("SET"));
    }

    public void testAlterSessionSetMultiple() {
        String sql = "ALTER SESSION SET QUERY_TAG = 'test', TIMESTAMP_OUTPUT_FORMAT = 'YYYY-MM-DD'";
        String result = parse(sql);
        assertTrue(result.contains("SET"));
    }

    public void testAlterSessionUnset() {
        String sql = "ALTER SESSION UNSET QUERY_TAG";
        String result = parse(sql);
        assertNotNull(result);
    }

    // ==================== Time Travel (AT/BEFORE) ====================

    public void testSelectAtTimestamp() {
        String sql = "SELECT * FROM my_table AT(TIMESTAMP => '2024-01-01 00:00:00'::timestamp)";
        String result = parse(sql);
        assertTrue(result.contains("SELECT"));
    }

    public void testSelectAtOffset() {
        String sql = "SELECT * FROM my_table AT(OFFSET => -3600)";
        String result = parse(sql);
        assertTrue(result.contains("SELECT"));
    }

    public void testSelectBeforeStatement() {
        String sql = "SELECT * FROM my_table BEFORE(STATEMENT => 'query_id')";
        String result = parse(sql);
        assertTrue(result.contains("SELECT"));
    }

    // ==================== TOP N ====================

    public void testSelectTop() {
        String sql = "SELECT TOP 10 * FROM my_table";
        String result = parse(sql);
        assertTrue(result.contains("TOP 10") || result.contains("LIMIT"));
    }

    public void testSelectTopWithOrderBy() {
        String sql = "SELECT TOP 5 id, name FROM users ORDER BY created_at DESC";
        String result = parse(sql);
        assertTrue(result.contains("TOP 5") || result.contains("LIMIT"));
    }

    // ==================== Semi-structured data functions ====================

    public void testArrayConstructCompact() {
        String sql = "SELECT [1, 2, 3] AS arr";
        String result = parse(sql);
        assertNotNull(result);
    }

    public void testObjectConstructCompact() {
        String sql = "SELECT {'key': 'value'} AS obj";
        String result = parse(sql);
        assertNotNull(result);
    }

    // ==================== JSON functions ====================

    public void testJsonExtract() {
        String sql = "SELECT data:key::VARCHAR FROM json_table";
        String result = parse(sql);
        assertNotNull(result);
    }

    public void testJsonExtractPath() {
        String sql = "SELECT data:address.city::VARCHAR FROM json_table";
        String result = parse(sql);
        assertNotNull(result);
    }

    // ==================== Conditional functions ====================

    public void testIffFunction() {
        String sql = "SELECT IFF(condition, 'true_value', 'false_value') FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("IFF"));
    }

    public void testNullIf() {
        String sql = "SELECT NULLIF(a, b) FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("NULLIF"));
    }

    public void testNvl2() {
        String sql = "SELECT NVL2(expr1, expr2, expr3) FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("NVL2"));
    }

    // ==================== String functions ====================

    public void testRegexpSubstring() {
        String sql = "SELECT REGEXP_SUBSTR(str, 'pattern') FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("REGEXP_SUBSTR"));
    }

    public void testRegexpReplace() {
        String sql = "SELECT REGEXP_REPLACE(str, 'pattern', 'replacement') FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("REGEXP_REPLACE"));
    }

    public void testSplitPart() {
        String sql = "SELECT SPLIT_PART('a,b,c', ',', 2)";
        String result = parse(sql);
        assertTrue(result.contains("SPLIT_PART"));
    }

    // ==================== Date/Time functions ====================

    public void testDateTrunc() {
        String sql = "SELECT DATE_TRUNC('MONTH', created_at) FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("DATE_TRUNC"));
    }

    public void testTimeSlice() {
        String sql = "SELECT TIME_SLICE(timestamp_col, 5, 'MINUTE') FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("TIME_SLICE"));
    }

    public void testLastDay() {
        String sql = "SELECT LAST_DAY(created_at, 'MONTH') FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("LAST_DAY"));
    }

    // ==================== Window functions ====================

    public void testRowNumber() {
        String sql = "SELECT ROW_NUMBER() OVER (PARTITION BY dept ORDER BY salary DESC) FROM employees";
        String result = parse(sql);
        assertTrue(result.contains("ROW_NUMBER"));
    }

    public void testRankDenseRank() {
        String sql = "SELECT RANK() OVER (ORDER BY score DESC), DENSE_RANK() OVER (ORDER BY score DESC) FROM results";
        String result = parse(sql);
        assertTrue(result.contains("RANK"));
        assertTrue(result.contains("DENSE_RANK"));
    }

    public void testLagLead() {
        String sql = "SELECT LAG(price, 1) OVER (ORDER BY date), LEAD(price, 1) OVER (ORDER BY date) FROM stocks";
        String result = parse(sql);
        assertTrue(result.contains("LAG"));
        assertTrue(result.contains("LEAD"));
    }

    // ==================== Aggregate functions ====================

    public void testCountIf() {
        String sql = "SELECT COUNT_IF(condition) FROM t1";
        String result = parse(sql);
        assertTrue(result.contains("COUNT_IF"));
    }

    public void testApproxCountDistinct() {
        String sql = "SELECT APPROX_COUNT_DISTINCT(user_id) FROM events";
        String result = parse(sql);
        assertTrue(result.contains("APPROX_COUNT_DISTINCT"));
    }

    public void testPercentileCont() {
        String sql = "SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY salary) FROM employees";
        String result = parse(sql);
        assertTrue(result.contains("PERCENTILE_CONT"));
    }

    // ==================== Tablesample ====================

    public void testTablesample() {
        String sql = "SELECT * FROM my_table TABLESAMPLE (10 PERCENT)";
        String result = parse(sql);
        assertNotNull(result);
    }

    public void testTablesampleRows() {
        String sql = "SELECT * FROM my_table TABLESAMPLE (100 ROWS)";
        String result = parse(sql);
        assertNotNull(result);
    }

    public void testSample() {
        String sql = "SELECT * FROM my_table SAMPLE (10)";
        String result = parse(sql);
        assertNotNull(result);
    }

    // ==================== Pivot/Unpivot advanced ====================

    public void testPivotWithAggregation() {
        String sql = "SELECT * FROM sales PIVOT(SUM(amount) FOR quarter IN ('Q1', 'Q2', 'Q3', 'Q4'))";
        String result = parse(sql);
        assertTrue(result.contains("PIVOT"));
    }

    public void testUnpivotWithNames() {
        String sql = "SELECT * FROM wide_table UNPIVOT(value FOR measure_name IN (col1, col2, col3))";
        String result = parse(sql);
        assertTrue(result.contains("UNPIVOT"));
    }

    // ==================== Qualify clause ====================

    public void testQualifyWithWindowFunction() {
        String sql = "SELECT * FROM t1 QUALIFY ROW_NUMBER() OVER (PARTITION BY id ORDER BY ts DESC) = 1";
        String result = parse(sql);
        assertTrue(result.contains("QUALIFY"));
    }

    public void testQualifyComplexCondition() {
        String sql = "SELECT * FROM t1 QUALIFY RANK() OVER (ORDER BY score DESC) <= 10";
        String result = parse(sql);
        assertTrue(result.contains("QUALIFY"));
    }

    // ==================== Transaction statements ====================

    public void testBeginTransactionName() {
        String sql = "BEGIN TRANSACTION NAME my_txn";
        String result = parse(sql);
        assertNotNull(result);
    }

    public void testCommit() {
        String sql = "COMMIT";
        String result = parse(sql);
        assertNotNull(result);
    }

    public void testRollback() {
        String sql = "ROLLBACK";
        String result = parse(sql);
        assertNotNull(result);
    }

    // ==================== Session functions ====================

    public void testCurrentTimestamp() {
        String sql = "SELECT CURRENT_TIMESTAMP(), CURRENT_DATE(), CURRENT_TIME()";
        String result = parse(sql);
        assertNotNull(result);
    }

    public void testSessionParameter() {
        String sql = "SELECT $QUERY_TAG, $SESSION_ID";
        String result = parse(sql);
        assertNotNull(result);
    }

    // ==================== Stage operations ====================

    public void testCopyIntoTable() {
        String sql = "COPY INTO my_table FROM @my_stage/data/";
        String result = parse(sql);
        assertTrue(result.contains("COPY"));
    }

    // ==================== MERGE advanced ====================

    public void testMergeMultipleWhenMatched() {
        String sql = "MERGE INTO target t USING source s ON t.id = s.id " +
                "WHEN MATCHED AND t.active = TRUE THEN UPDATE SET t.value = s.value " +
                "WHEN MATCHED THEN DELETE " +
                "WHEN NOT MATCHED THEN INSERT (id, value) VALUES (s.id, s.value)";
        String result = parse(sql);
        assertTrue(result.contains("MERGE"));
    }

    // ==================== DROP VIEW IF EXISTS ====================

    public void testDropViewIfExists() {
        String sql = "DROP VIEW IF EXISTS my_view";
        String result = parse(sql);
        assertTrue(result.contains("IF EXISTS"));
    }

    // ==================== CREATE OR REPLACE TABLE AS SELECT ====================

    public void testCreateOrReplaceTableAsSelect() {
        String sql = "CREATE OR REPLACE TABLE my_table AS SELECT id, name FROM source";
        String result = parse(sql);
        assertTrue(result.contains("CREATE OR REPLACE TABLE"));
    }

    // ==================== DESCRIBE variations ====================

    public void testDescView() {
        String sql = "DESC VIEW my_view";
        String result = parse(sql);
        assertNotNull(result);
    }
}
