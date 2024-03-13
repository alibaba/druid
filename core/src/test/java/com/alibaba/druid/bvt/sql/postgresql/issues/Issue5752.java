package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 来自postgresql的alter table语法测试，大部分场景已经能识别并支持解析
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5752">Issue来源</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-altertable.html">ALTER TABLE</a>
 */
public class Issue5752 {

    @Test
    public void test_parse_alter_table_sql() {
        for (DbType dbType : new DbType[]{DbType.postgresql, DbType.greenplum, DbType.edb}) {

            for (String sql : new String[]{
                "ALTER TABLE distributors ADD COLUMN address varchar(30);",
                "ALTER TABLE distributors RENAME COLUMN address TO city;",
                "ALTER TABLE distributors RENAME TO suppliers;",
                "ALTER TABLE distributors RENAME CONSTRAINT zipchk TO zip_check;",
                "ALTER TABLE distributors ALTER COLUMN street SET NOT NULL;",
                "ALTER TABLE distributors ALTER COLUMN street DROP NOT NULL;",
                "ALTER TABLE distributors ADD CONSTRAINT zipchk CHECK (char_length(zipcode) = 5);",
                "ALTER TABLE distributors DROP CONSTRAINT zipchk;",
                "ALTER TABLE distributors DROP CONSTRAINT IF EXISTS zipchk;",
                "ALTER TABLE distributors DROP CONSTRAINT IF EXISTS zipchk RESTRICT;",
                "ALTER TABLE distributors DROP CONSTRAINT IF EXISTS zipchk CASCADE;",
                "ALTER TABLE distributors ADD CONSTRAINT distfk FOREIGN KEY (address) REFERENCES addresses (address);",
                "ALTER TABLE distributors ADD CONSTRAINT dist_id_zipcode_key UNIQUE (dist_id, zipcode);",
                "ALTER TABLE distributors ADD PRIMARY KEY (dist_id);",
                "ALTER TABLE ONLY distributors DROP CONSTRAINT zipchk;",
                "ALTER TABLE measurements\n"
                    + "  ADD COLUMN mtime timestamp with time zone DEFAULT now();",
                "ALTER TABLE distributors DROP COLUMN address RESTRICT;",
                "ALTER TABLE distributors DROP COLUMN IF EXISTS address RESTRICT;",
                "ALTER TABLE distributors DROP COLUMN IF EXISTS address RESTRICT",
                "ALTER TABLE distributors DROP COLUMN address CASCADE ;",
                "ALTER TABLE distributors DROP COLUMN address CASCADE ;",
                "ALTER TABLE distributors\n"
                    + "    ALTER COLUMN address TYPE varchar(80),\n"
                    + "    ALTER COLUMN name TYPE varchar(100);",
                "ALTER TABLE if exists ref_standard_indicator add column if not exists parent_code varchar(64);",
                "ALTER TABLE distributors ADD CONSTRAINT zipchk CHECK (char_length(zipcode) = 5) NO INHERIT;",

                "ALTER TABLE distributors SET COMMENT fasttablespace;",
                "ALTER TABLE distributors SET TABLESPACE fasttablespace;",
                "ALTER TABLE myschema.distributors SET SCHEMA yourschema;",

                "ALTER TABLE cities\n"
                    + "    ATTACH PARTITION cities_partdef;",
                "ALTER TABLE cities\n"
                    + "    ATTACH PARTITION cities_partdef DEFAULT;",
                "ALTER TABLE IF EXISTS cities\n"
                    + "    ATTACH PARTITION cities_partdef DEFAULT;",
                "ALTER TABLE measurement\n"
                    + "    DETACH PARTITION measurement_y2015m12;",
                "ALTER TABLE measurement\n"
                    + "    DETACH PARTITION measurement_y2015m12 CONCURRENTLY ;",
                "ALTER TABLE measurement\n"
                    + "    DETACH PARTITION measurement_y2015m12 FINALIZE ;",

                "ALTER TABLE transactions\n"
                    + "  ADD COLUMN status varchar(30) DEFAULT 'old',\n"
                    + "  ALTER COLUMN status SET default 'current';",

                "ALTER TABLE distributors ADD CONSTRAINT distfk FOREIGN KEY (address) REFERENCES addresses (address) NOT VALID;\n"
                    + "ALTER TABLE distributors VALIDATE CONSTRAINT distfk;",

//
//                "ALTER TABLE foo\n"
//                    + "    ALTER COLUMN foo_timestamp SET DATA TYPE timestamp with time zone\n"
//                    + "    USING\n"
//                    + "        timestamp with time zone 'epoch' + foo_timestamp * interval '1 second';",
//                "ALTER TABLE foo\n"
//                    + "    ALTER COLUMN foo_timestamp DROP DEFAULT,\n"
//                    + "    ALTER COLUMN foo_timestamp TYPE timestamp with time zone\n"
//                    + "    USING\n"
//                    + "        timestamp with time zone 'epoch' + foo_timestamp * interval '1 second',\n"
//                    + "    ALTER COLUMN foo_timestamp SET DEFAULT now();",
//                "",
//                "CREATE UNIQUE INDEX CONCURRENTLY dist_id_temp_idx ON distributors (dist_id);\n"
//                    + "ALTER TABLE distributors DROP CONSTRAINT distributors_pkey,\n"
//                    + "    ADD CONSTRAINT distributors_pkey PRIMARY KEY USING INDEX dist_id_temp_idx;",
//                "ALTER TABLE measurement\n"
//                    + "    ATTACH PARTITION measurement_y2016m07 FOR VALUES FROM ('2016-07-01') TO ('2016-08-01');",
//                "ALTER TABLE cities\n"
//                    + "    ATTACH PARTITION cities_ab FOR VALUES IN ('a', 'b');",
//                "ALTER TABLE orders\n"
//                    + "    ATTACH PARTITION orders_p4 FOR VALUES WITH (MODULUS 4, REMAINDER 3);",

            }) {
                System.out.println("原始的sql===" + sql);
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println("生成的sql===" + statementList);
                StringBuilder sb = new StringBuilder();
                for (SQLStatement statement : statementList) {
                    sb.append(statement.toString()).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                parser = SQLParserUtils.createSQLStatementParser(sb.toString(), dbType);
                List<SQLStatement> statementListNew = parser.parseStatementList();
                System.out.println("重新解析再生成的sql===" + statementListNew);
                assertEquals(statementList.toString(), statementListNew.toString());
            }
        }
    }
}
