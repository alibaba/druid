package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 验证 ALTER user ACCOUNT LOCK | ACCOUNT UNLOCK语法
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5702">Issue来源</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/alter-user.html">...</a>
 */
public class Issue5702 {

    @Test
    public void test_alter_user_accout() throws Exception {
        for (DbType dbType : new DbType[]{DbType.mysql}) {

            for (String sql : new String[]{
                "ALTER USER 'jeffrey'@'localhost' ACCOUNT LOCK",
                "ALTER USER 'jeffrey'@'localhost' ACCOUNT UNLOCK",
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
                assertEquals(sql, statement.toString());
            }

            String sql = "ALTER USER 'jeffrey'@'localhost' ACCOUNT LOCK;\n"
                + "ALTER USER 'jeffrey'@'localhost' ACCOUNT UNLOCK;\n"
                + "ALTER USER 'jeffrey'@'localhost' ACCOUNT LOCK;\n"
                + "ALTER USER 'jeffrey'@'localhost' ACCOUNT UNLOCK;";
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
            List<SQLStatement> list = parser.parseStatementList();
            assertEquals("ALTER USER 'jeffrey'@'localhost' ACCOUNT LOCK;", list.get(0).toString());
            assertEquals("ALTER USER 'jeffrey'@'localhost' ACCOUNT UNLOCK;", list.get(1).toString());
            assertEquals("ALTER USER 'jeffrey'@'localhost' ACCOUNT LOCK;", list.get(2).toString());
            assertEquals("ALTER USER 'jeffrey'@'localhost' ACCOUNT UNLOCK;", list.get(3).toString());
        }
    }
}
