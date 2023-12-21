package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

/**
 * 验证 select sjon_tablle 语句的解析
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5642">Issue来源</a>
 */
public class Issue5642 {
    @Test
    public void test_select_json_table() throws Exception {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                "SELECT *\n"
                    + "FROM\n"
                    + "JSON_TABLE(\n"
                    + "'[{\"x\": 10, \"y\": 11}, {\"x\": 20, \"y\": 21}]',\n"
                    + "'$[*]'\n"
                    + "COLUMNS (\n"
                    + "id FOR ORDINALITY,\n"
                    + "x INT PATH '$.x',\n"
                    + "y INT PATH '$.y'\n"
                    + ")\n"
                    + ") AS t;",
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
            }
        }
    }
}
