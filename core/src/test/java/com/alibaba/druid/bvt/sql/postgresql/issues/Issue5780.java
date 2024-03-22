package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5780">Issue来源</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-altertable.html">ALTER TABLE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-createindex.html">CREATE INDEX</a>
 */
public class Issue5780 {

    @Test
    public void test_parse_alter_table_sql() {
        for (DbType dbType : new DbType[]{DbType.postgresql, DbType.greenplum, DbType.edb}) {

            for (String sql : new String[]{
                "CREATE INDEX \"index_log\" ON \"public\".\"check_log\" USING btree (\n" +
                    "  \"t_no\" COLLATE \"pg_catalog\".\"default\" \"pg_catalog\".\"text_ops\" ASC NULLS LAST\n" +
                    ");",
                //"CREATE UNIQUE INDEX title_idx ON films (title);",
                //"CREATE UNIQUE INDEX title_idx ON films (title) INCLUDE (director, rating);",
//                "CREATE INDEX title_idx ON films (title) WITH (deduplicate_items = off);",
//                "CREATE INDEX ON films ((lower(title)));",
//                "CREATE INDEX title_idx_german ON films (title COLLATE \"de_DE\");",
//                "CREATE INDEX title_idx_nulls_low ON films (title NULLS FIRST);",
//                "CREATE UNIQUE INDEX title_idx ON films (title) WITH (fillfactor = 70);",
//                "CREATE INDEX gin_idx ON documents_table USING GIN (locations) WITH (fastupdate = off);",
//                "CREATE INDEX code_idx ON films (code) TABLESPACE indexspace;",
//                "CREATE INDEX pointloc\n"
//                    + "    ON points USING gist (box(location,location));",
//                "CREATE INDEX CONCURRENTLY sales_quantity_index ON sales_table (quantity);",
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
