package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5381">Issue来源</a>
 */
public class Issue5381 {

    @Test
    public void test_select_in_parameterized() throws Exception {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            String[] orgSqls = new String[]{
                "select aaa from table_demo where id in (100, 200, 300)",
                "select aaa from table_demo where id in (ccc, 200, 300)",
                "select aaa from table_demo where id in (+ccc, 200, 300)",
                "select bbb from table_demo where id in (+100, 200, 300, 400, 500)",
                "select bbb from table_demo where id in (+100.02, 200, 300.0, 400, 500)",
                "select aaa from table_demo where id in (100, +200, 300)",
                "select aaa from table_demo where id in (-100, 200, 300)",
                "select aaa from table_demo where id in (100, -200, 300)",
                "select aaa from table_demo where id in (100, 200, -300)",
            };
            String[] parameterizedSqls = new String[]{
                "SELECT aaa FROM table_demo WHERE id IN (?)",
                "SELECT aaa FROM table_demo WHERE id IN (ccc, ?, ?)",
                "SELECT aaa FROM table_demo WHERE id IN (+ccc, ?, ?)",
                "SELECT bbb FROM table_demo WHERE id IN (?)",
                "SELECT bbb FROM table_demo WHERE id IN (?)",
                "SELECT aaa FROM table_demo WHERE id IN (?)",
                "SELECT aaa FROM table_demo WHERE id IN (?)",
                "SELECT aaa FROM table_demo WHERE id IN (?)",
                "SELECT aaa FROM table_demo WHERE id IN (?)",
            };
            for (int i = 0; i < orgSqls.length; i++) {
                String sql = orgSqls[i];
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println("原始的sql===" + sql);
                //String newSql = statement.toString().replace("\n", " ").replace('\'', '"') + ";";
                // System.out.println("生成的sql===" + newSql);
                //String formatSql = SQLUtils.format(sql, dbType).replace("\n", " ").replace('\'', '"') + ";";
                //System.out.println("格式化sql===" + formatSql);
                String parameterizedSql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType).replace("\n", " ").replace('\'', '"');
                System.out.println("归一化sql===" + parameterizedSql);
                assertEquals(parameterizedSqls[i], parameterizedSql);
                SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
                statement.accept(visitor);
                System.out.println("getTables==" + visitor.getTables());
                Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
                assertFalse(tableMap.isEmpty());

            }
        }
    }
}
