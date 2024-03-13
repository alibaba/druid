package com.alibaba.druid.bvt.sql.oceanbase.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.mysql.issues.Issue5421;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 验证 update ant.t1 set name=10 where id >100 limit 1语法
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5078">Issue来源</a>
 */
public class Issue5078 {

    @Test
    public void test_update_limit() throws Exception {
        for (DbType dbType : new DbType[]{DbType.oceanbase}) {
            for (String sql : new String[]{
                "UPDATE ant.t1 SET name = 10 WHERE id > 200;",
                "UPDATE ant.t1 SET name = 10 WHERE id > 100 LIMIT 2024;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println(dbType + "原始的sql===" + sql);
                System.out.println(dbType + "归一化的sql===" + Issue5421.normalizeSql(sql));
                String newSql = statement.toString() + ";";
                System.out.println(dbType + "生成的sql===" + newSql);
                System.out.println(dbType + "生成的sql归一化===" + Issue5421.normalizeSql(newSql));
                parser = SQLParserUtils.createSQLStatementParser(newSql, dbType);
                statement = parser.parseStatement();
                System.out.println(dbType + "再次解析对象得到sql===" + Issue5421.normalizeSql(statement.toString()));
                assertEquals(sql, Issue5421.normalizeSql(statement.toString()) + ";");
            }
        }
    }
}
