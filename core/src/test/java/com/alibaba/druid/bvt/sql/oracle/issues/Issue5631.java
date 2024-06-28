package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.mysql.issues.Issue5421;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * 验证 Oracle merge sql的顺序问题
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5631">Issue来源</a>
 */
public class Issue5631 {

    @Test
    public void test_merge_into() throws Exception {
        for (DbType dbType : new DbType[]{DbType.oracle}) {
            for (String sql : new String[]{
                "MERGE INTO  target_table\n"
                    + "USING source_table ON (target_table.id = source_table.id)\n"
                    + "WHEN NOT MATCHED THEN INSERT (id, column1) VALUES (source_table.id, source_table.column1)\n"
                    + "WHEN MATCHED THEN UPDATE SET target_table.column1 = source_table.column1",
                "MERGE INTO  target_table\n"
                    + "USING source_table ON (target_table.id = source_table.id)\n"
                    + "WHEN MATCHED THEN UPDATE SET target_table.column1 = source_table.column1\n"
                    + "WHEN NOT MATCHED THEN INSERT (id, column1) VALUES (source_table.id, source_table.column1)",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println(dbType + "原始的sql===" + sql);
                System.out.println(dbType + "原始sql归一化===" + Issue5421.normalizeSql(sql));
                String newSql = statement.toString();
                System.out.println(dbType + "初次解析生成的sql===" + newSql);
                System.out.println(dbType + "初次解析生成的sql归一化===" + Issue5421.normalizeSql(newSql));
                parser = SQLParserUtils.createSQLStatementParser(newSql, dbType);
                statement = parser.parseStatement();
                System.out.println(dbType + "重新解析sql归一化===" + Issue5421.normalizeSql(statement.toString()));
                assertTrue(Issue5421.normalizeSql(sql).equalsIgnoreCase(Issue5421.normalizeSql(statement.toString())));
            }
        }
    }
}
