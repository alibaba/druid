package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Name;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 验证 natural join 语句的解析
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5260">Issue来源</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/join.html">MySQL join语法</a>
 */
public class Issue5260 {

    @Test
    public void test_natural_join() throws Exception {
        for (DbType dbType : new DbType[]{DbType.mysql, DbType.oracle}) {
            for (String sql : new String[]{
                "select * from t_a natural join t_b;",
                "SELECT * FROM t1 NATURAL JOIN t2;",
                "SELECT * FROM t1 NATURAL LEFT JOIN t2;",
                "SELECT * FROM t1 NATURAL RIGHT JOIN t2;",
                "SELECT * FROM t1 NATURAL INNER JOIN t2;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println(dbType + "原始的sql===" + sql);
                System.out.println(dbType + "归一化的sql===" + Issue5421.normalizeSql(sql));
                String newSql = statement.toString() + ";";
                System.out.println(dbType + "生成的sql归一化===" + Issue5421.normalizeSql(newSql));
                parser = SQLParserUtils.createSQLStatementParser(newSql, dbType);
                statement = parser.parseStatement();
                String sqlNew2 = Issue5421.normalizeSql(statement.toString() + ";");
                System.out.println(dbType + "再次解析对象得到sql===" + sqlNew2);
                System.out.println(dbType + "最原始的实际得到sql===" + Issue5421.normalizeSql(sql));
                assertTrue(Issue5421.normalizeSql(sql).equalsIgnoreCase(sqlNew2));
                SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
                statement.accept(visitor);
                System.out.println("getTables==" + visitor.getTables());
                Map<Name, TableStat> tableMap = visitor.getTables();
                assertFalse(tableMap.isEmpty());
            }
        }
    }
}
