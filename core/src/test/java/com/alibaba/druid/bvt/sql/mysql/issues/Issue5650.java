package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 验证 ALTER TABLE t1 ALTER INDEX i_idx INVISIBLE语法
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5650">Issue来源</a>
 */
public class Issue5650 {

    @Test
    public void test_alter_index() throws Exception {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                "ALTER TABLE t1 ALTER INDEX i_idx INVISIBLE;",
                "ALTER TABLE t1 ALTER INDEX i_idx VISIBLE;",
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
