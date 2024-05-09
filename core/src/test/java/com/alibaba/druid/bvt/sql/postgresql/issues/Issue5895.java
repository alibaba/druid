package com.alibaba.druid.bvt.sql.postgresql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5895>Issue来源</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-createuser.html">CREATE USER</a>
 */
public class Issue5895 {

    @Test
    public void test_parse_createuser() {
        for (DbType dbType : new DbType[]{DbType.postgresql}) {
            for (String sql : new String[]{
                "CREATE USER new_user WITH PASSWORD 'password';",
                "CREATE USER new_user WITH PASSWORD NULL;",
                "CREATE USER new_user WITH ENCRYPTED PASSWORD 'password';",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                assertEquals(1, statementList.size());
                System.out.println("解析后" + statementList.get(0).toString());
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }
}
