package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5861>Issue来源</a>
 * @see <a href="https://mariadb.com/kb/en/set-statement/">SET STATEMENT</a>
 */
public class Issue5861 {

    @Test
    public void test_parse_set_statement() {
        for (DbType dbType : new DbType[]{
            DbType.mariadb,
            DbType.mysql,

        }) {

            for (String sql : new String[]{
                "SET STATEMENT max_statement_time=25 FOR select T.* from (\n"
                    + "SELECT\n"
                    + "head_pm_code\n"
                    + "FROM\n"
                    + "ef_ap_fee_detail\n"
                    + "where audit_status = 0\n"
                    + "and create_time >= '2023-12-02 00:00:00'\n"
                    + "and create_time < '2023-12-03 00:00:00'\n"
                    + "and (source_from='50' or status='30')\n"
                    + "group by head_pm_code\n"
                    + "limit 10000\n"
                    + ") T;",
                "SET STATEMENT  join_cache_level=6, optimizer_switch='mrr=on' "
                    + "FOR select * from t1 join t2 on t1.a=t2.a;",

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
