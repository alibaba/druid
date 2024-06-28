package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5474">Issue来源</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-createtrigger.html">PostgreSQL CREATE TRIGGER — define a new trigger</a>
 */
public class Issue5474 {

    @Test
    public void test_create_triger_execute() throws Exception {
        for (DbType dbType : new DbType[]{DbType.postgresql}) {
            String sql = "CREATE TRIGGER \"update_time\" BEFORE UPDATE ON \"poit_cloud\".\"ent_i_checking_analyze\" FOR EACH ROW EXECUTE PROCEDURE poit_cloud.modify_timestamp();";

            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
            SQLStatement statement = parser.parseStatement();
            System.out.println("原始的sql===" + sql);
            String newSql = statement.toString();
            System.out.println("生成的sql===" + newSql);

        }
    }
}
