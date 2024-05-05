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
 * @see <a href="https://github.com/alibaba/druid/issues/5803>Issue来源</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/invisible-indexes.html">Invisible Indexes</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/alter-table.html">ALTER TABLE Statement</a>
 */
public class Issue5803 {

    @Test
    public void test_parse_alter_table() {
        for (DbType dbType : new DbType[]{
            DbType.mysql,
            DbType.mariadb,

        }) {

            for (String sql : new String[]{
                "ALTER TABLE t1 ALTER INDEX i_idx INVISIBLE;",
                "ALTER TABLE t1 ALTER INDEX i_idx VISIBLE;",
                "ALTER TABLE t2 ALTER INDEX j_idx INVISIBLE;",
                "ALTER TABLE t2 ALTER INDEX j_idx INVISIBLE;",
                "alter table tt ALTER COLUMN c1 SET VISIBLE;",
                "alter table tt ALTER COLUMN c1 SET INVISIBLE;",
            }) {
                System.out.println(dbType + "原始的sql===" + sql);
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                String sqlGen = statementList.toString();
                System.out.println(dbType + "生成的sql===" + sqlGen);
                StringBuilder sb = new StringBuilder();
                for (SQLStatement statement : statementList) {
                    sb.append(statement.toString()).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                parser = SQLParserUtils.createSQLStatementParser(sb.toString(), dbType);
                List<SQLStatement> statementListNew = parser.parseStatementList();
                String sqlGenNew = statementList.toString();
                System.out.println(dbType + "重新解析再生成的sql===" + sqlGenNew);
                assertEquals(statementList.toString(), statementListNew.toString());
            }
        }
    }
}
