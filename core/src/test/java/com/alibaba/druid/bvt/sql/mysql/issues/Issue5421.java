package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 验证 create table as with语句的解析
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5421">Issue来源</a>
 */
public class Issue5421 {

    @Test
    public void test_create_table_with() throws Exception {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                "create table if not exists t2 as\n"
                    + "with X as ( select * from t1)\n"
                    + "select * from X;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println("原始的sql===" + sql);
                sql=normalizeSql(sql);
                System.out.println("归一化的sql===" + sql);
                String newSql=normalizeSql(statement.toString())+";";
                System.out.println("生成的sql===" + newSql);
                assertTrue(newSql.equalsIgnoreCase(sql));
                SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
                statement.accept(visitor);
                //@todo 为什么这里的表名只有t2，而没有t1,还需要进一步分析
                System.out.println("getTables==" + visitor.getTables());
                Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
                assertFalse(tableMap.isEmpty());
                assertTrue(tableMap.containsKey(new TableStat.Name("t2")));

            }
        }
    }

    public static String normalizeSql(String sql) {
        sql = StringUtils.replace(sql, " ( ", "(");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, " )", ")");
        sql = StringUtils.replace(sql, "\t", " ");
        sql = StringUtils.replace(sql, "\n", " ");
        sql = StringUtils.replace(sql, "\'", "\"");
        sql = StringUtils.replace(sql, " ( ", "(");
        sql = StringUtils.replace(sql, " (", "(");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, " )", ")");
        sql = StringUtils.replace(sql, " )", ")");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "  ", " ");
        sql = StringUtils.replace(sql, "( ", "(");
        sql = StringUtils.replace(sql, ", ", ",");
        sql = StringUtils.replace(sql, " ,", ",");
        return sql;
    }
}
