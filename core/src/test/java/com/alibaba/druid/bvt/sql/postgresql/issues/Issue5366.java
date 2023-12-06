package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 验证 Postgresql 无法解析 create table PARTITION OF 语句 #5366
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5366">增强 #5366</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-createtable.html">CREATE TABLE</a>
 */
public class Issue5366 {

    @Test
    public void test_create_table() throws Exception {
        for (DbType dbType : new DbType[]{DbType.postgresql}) {
            for (String sql : new String[]{
                "CREATE TABLE orders_p4 PARTITION OF orders\n"
                    + "    FOR VALUES WITH (MODULUS 4, REMAINDER 3);",
                "CREATE TABLE measurement_y2016m07\n"
                    + "    PARTITION OF measurement (\n"
                    + "    unitsales DEFAULT 0\n"
                    + ") FOR VALUES FROM ('2016-07-01') TO ('2016-08-01');",
                "CREATE TABLE cities_ab\n"
                    + "    PARTITION OF cities (\n"
                    + "    CONSTRAINT city_id_nonzero CHECK (city_id != 0)\n"
                    + ") FOR VALUES IN ('a', 'b');",
                "CREATE TABLE measurement_ym_y2017m01\n"
                    + "    PARTITION OF measurement_year_month\n"
                    + "    FOR VALUES IN (2017, 1) PARTITION BY RANGE (population);",
                "CREATE TABLE IF NOT EXISTS lc_event_1689811200000 PARTITION OF lc_event FOR VALUES FROM (1689811200000) TO (1690416000000);",
                "CREATE TABLE measurement_ym_older\n"
                    + "    PARTITION OF measurement_year_month\n"
                    + "    FOR VALUES FROM (MINVALUE, MINVALUE) TO (2016, 11);",
                "CREATE TABLE measurement_ym_y2016m11\n"
                    + "    PARTITION OF measurement_year_month\n"
                    + "    FOR VALUES FROM (2016, 11) TO (2016, 12);",
                "CREATE TABLE measurement_ym_y2016m12\n"
                    + "    PARTITION OF measurement_year_month\n"
                    + "    FOR VALUES FROM (2016, 12) TO (2017, 1);",
                "CREATE TABLE measurement_ym_y2017m01\n"
                    + "    PARTITION OF measurement_year_month\n"
                    + "    FOR VALUES FROM (2017, 1) TO (2017, 2);",
                "CREATE TABLE measurement_ym_y2017m02\n"
                    + "    PARTITION OF measurement_year_month\n"
                    + "    FOR VALUES IN (2017, 1) PARTITION BY RANGE (population);",
                "CREATE TABLE cities_ab\n"
                    + "    PARTITION OF cities (\n"
                    + "    CONSTRAINT city_id_nonzero CHECK (city_id != 0)\n"
                    + ") FOR VALUES IN ('a', 'b') PARTITION BY RANGE (population);",
                "CREATE TABLE cities_ab_10000_to_100000\n"
                    + "    PARTITION OF cities_ab FOR VALUES FROM (10000) TO (100000);",
                "CREATE TABLE orders_p1 PARTITION OF orders\n"
                    + "    FOR VALUES WITH (MODULUS 4, REMAINDER 0);",
                "CREATE TABLE orders_p2 PARTITION OF orders\n"
                    + "    FOR VALUES WITH (MODULUS 4, REMAINDER 1);",
                "CREATE TABLE orders_p3 PARTITION OF orders\n"
                    + "    FOR VALUES WITH (MODULUS 4, REMAINDER 2);",
                "CREATE TABLE cities_partdef\n"
                    + "    PARTITION OF cities DEFAULT;",
            }) {
                System.out.println(dbType + "原始的sql===" + sql);
                String normalizeSql = normalizeSql(sql);
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println(dbType + "生成的sql===" + statement);
                String newSql = statement.toString()+";";
                String normalizeNewSql = normalizeSql(newSql);
                assertEquals(normalizeSql.toLowerCase(),normalizeNewSql.toLowerCase());
                SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
                statement.accept(visitor);
                System.out.println(dbType + "getTables==" + visitor.getTables());
                Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
                assertTrue(!tableMap.isEmpty());
            }

        }
    }
    static String normalizeSql(String sql) {
        sql = StringUtils.replace(sql, " ( ", "(");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, " )", ")");
        sql = StringUtils.replace(sql, "\t", " ");
        sql = StringUtils.replace(sql, "\n", " ");
        sql = StringUtils.replace(sql, "\'", "\"");
        sql = StringUtils.replace(sql, " ( ", "(");
        sql = StringUtils.replace(sql, " (", "(");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, " )", ")");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, ", ", ",");
        sql = StringUtils.replace(sql, " ,", ",");
        return sql;
    }
}
