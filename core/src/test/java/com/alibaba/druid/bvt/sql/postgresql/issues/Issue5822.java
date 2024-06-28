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
 * @see <a href="https://www.postgresql.org/docs/current/collation.html">Collation Support </a>
 */
public class Issue5822 {

    @Test
    public void test_parse_postgresql_collate() {
        for (String sql : new String[]{
            "SELECT a < 'foo' FROM test1;",
            "SELECT a < b FROM test1;",
            "SELECT a < b COLLATE \"de_DE\" FROM test1;",
            "SELECT a COLLATE \"de_DE\" < b FROM test1;",
            "SELECT a || b FROM test1;",
            "SELECT * FROM test1 ORDER BY a || 'foo';",
            "SELECT * FROM test1 ORDER BY a || b;",
            "SELECT * FROM test1 ORDER BY a || b COLLATE \"fr_FR\";",
            "SELECT a COLLATE \"C\" < b COLLATE \"POSIX\" FROM test1;",

            "SELECT a < ('foo' COLLATE \"zh_CN\") FROM test1;",
            "SELECT a < ('foo' COLLATE 'zh_CN') FROM test1;",
            "SELECT a < ('foo') FROM test1;",
            "SELECT a < (bbb COLLATE \"zh_CN\") FROM test1;",
            "SELECT a < (ccc COLLATE 'zh_CN') FROM test1;",
            "SELECT id FROM a_product WHERE product_name COLLATE \"C\" ILIKE '%70%'"
                + " AND 1=1 ORDER BY product_name,created_at DESC,id DESC LIMIT 20",
            "SELECT id FROM a_product WHERE a_product.tenant_id=123 AND (tenant_id=123 AND del_flg=0 AND (product_name COLLATE \"C\" ILIKE '%70%' AND (INDEPENDENT_PRODUCT=1 AND ((1711942157815>=START_DATE AND 1711942157815< END_DATE) OR (START_DATE IS NULL AND END_DATE IS NULL) OR (START_DATE IS NULL AND 1711942157815< END_DATE) OR (1711942157815>=START_DATE AND END_DATE IS NULL))) AND id IN (SELECT dbc_relation_2 FROM p_custom_data_408 WHERE p_custom_data_408.tenant_id=123 AND (TENANT_ID=123 AND dbc_relation_1=1706133096090917 AND DELETE_FLG=0 AND dbc_select_2=1)) AND (((EXISTS (SELECT id FROM b_entity_checkbox pickvalue WHERE pickvalue.tenant_id=123 AND (a_product.id=pickvalue.object_id AND item_id=1776944933031192 AND option_code IN (1))) AND 1=1) OR (EXISTS (SELECT id FROM b_entity_checkbox pickvalue WHERE pickvalue.tenant_id=123 AND (a_product.id=pickvalue.object_id AND item_id=1776944933031192 AND option_code IN (2))) AND id=-999999999)) AND dbc_varchar_16='FERT') AND 1=1)) ORDER BY product_name,created_at DESC,id DESC LIMIT 20",
        }) {
            System.out.println("原始的sql===" + sql);
            SQLStatementParser parser1 = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
            List<SQLStatement> statementList1 = parser1.parseStatementList();
            String sqleNew = statementList1.get(0).toString();
            System.out.println("生成的sql===" + sqleNew);
            SQLStatementParser parser2 = SQLParserUtils.createSQLStatementParser(sqleNew, DbType.greenplum);
            List<SQLStatement> statementList2 = parser2.parseStatementList();
            String sqleNew2 = statementList2.get(0).toString();
            System.out.println("再次解析生成的sql===" + sqleNew2);
            assertEquals(sqleNew, sqleNew2);
        }

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/charset-collate.html">Using COLLATE in SQL Statements</a>
     */
    @Test
    public void test_parse_mysql_collate() {
        for (String sql : new String[]{
         "SELECT k\n"
             + "FROM t1\n"
             + "ORDER BY k COLLATE latin1_german2_ci;",
            "SELECT k COLLATE latin1_german2_ci AS k1\n"
                + "FROM t1\n"
                + "ORDER BY k1;",
            "SELECT k\n"
                + "FROM t1\n"
                + "GROUP BY k COLLATE latin1_german2_ci;",
            "SELECT MAX(k COLLATE latin1_german2_ci)\n"
                + "FROM t1;",
            "SELECT DISTINCT k COLLATE latin1_german2_ci\n"
                + "FROM t1;",
            "SELECT *\n"
                + "FROM t1\n"
                + "WHERE _latin1 'Müller' COLLATE latin1_german2_ci = k;",
            "SELECT *\n"
                + "FROM t1\n"
                + "WHERE k LIKE _latin1 'Müller' COLLATE latin1_german2_ci;",
            "SELECT k\n"
                + "FROM t1\n"
                + "GROUP BY k\n"
                + "HAVING k = _latin1 'Müller' COLLATE latin1_german2_ci;",
        }) {
            System.out.println("原始的sql===" + sql);
            SQLStatementParser parser1 = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
            List<SQLStatement> statementList1 = parser1.parseStatementList();
            String sqleNew = statementList1.get(0).toString();
            System.out.println("生成的sql===" + sqleNew);
            SQLStatementParser parser2 = SQLParserUtils.createSQLStatementParser(sqleNew, DbType.mariadb);
            List<SQLStatement> statementList2 = parser2.parseStatementList();
            String sqleNew2 = statementList2.get(0).toString();
            System.out.println("再次解析生成的sql===" + sqleNew2);
            assertEquals(sqleNew, sqleNew2);
        }

    }
}
