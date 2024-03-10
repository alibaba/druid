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
 * @see <a href="https://github.com/alibaba/druid/issues/5710">Issue来源</a>
 */
public class Issue5710 {

    @Test
    public void test_parse_show_sql() {
        for (String sql : new String[]{
            "SHOW search_path",
            "SHOW all",
        }) {
            DbType dbType = DbType.postgresql;
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
