package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author lizongbo
 * @see <a href="https://docs.pingcap.com/zh/tidb/stable/sql-statement-split-region">Split Region 使用文档</a>
 * @see <a href="https://github.com/alibaba/druid/issues/5219">Issue来源</a>
 */
public class Issue5219 {

    @Test
    public void test_split_table() throws Exception {
     //   for (DbType dbType : new DbType[]{DbType.mysql, DbType.tidb}) {
        for (DbType dbType : new DbType[]{DbType.tidb}) {
            for (String sql : new String[]{
                "split TABLE t BETWEEN (-9223372036854775808) AND (9223372036854775807) REGIONS 16;",
                "split partition TABLE t BETWEEN (-9223372036854775808) AND (9223372036854775807) REGIONS 16;",
                "split region for TABLE t BETWEEN (-9223372036854775808) AND (9223372036854775807) REGIONS 16;",
                "SPLIT TABLE t INDEX idx BETWEEN (-9223372036854775808) AND (9223372036854775807) REGIONS 16;",
                "SPLIT TABLE t1 INDEX idx4 BY (\"a\", \"2000-01-01 00:00:01\"), (\"b\", \"2019-04-17 14:26:19\"), (\"c\", \"\");",
                "split partition table t partition (p1,p3) between (0) and (10000) regions 2;",
                "split TABLE t BETWEEN (0) AND (1000000000) REGIONS 16;",
                "split TABLE t BY (10000), (90000);",
                "SPLIT TABLE t INDEX idx1 BETWEEN (\"a\") AND (\"z\") REGIONS 25;",
                "SPLIT TABLE t INDEX idx1 BETWEEN (\"a\") AND (\"{\") REGIONS 26;",
                "SPLIT TABLE t INDEX idx2 BETWEEN (\"2010-01-01 00:00:00\") AND (\"2020-01-01 00:00:00\") REGIONS 10;",
                "SPLIT TABLE t INDEX idx2 BETWEEN (\"2020-06-01 00:00:00\") AND (\"2020-07-01 00:00:00\") REGIONS 30;",
                "SPLIT TABLE t INDEX idx3 BETWEEN (\"2010-01-01 00:00:00\") AND (\"2020-01-01 00:00:00\") REGIONS 10;",
                "SPLIT TABLE t INDEX idx3 BETWEEN (\"2010-01-01 00:00:00\", \"a\") AND (\"2010-01-01 00:00:00\", \"z\") REGIONS 10;",
                "SPLIT TABLE t INDEX `PRIMARY` BETWEEN (-9223372036854775808) AND (9223372036854775807) REGIONS 16;",
                "SPLIT TABLE t1 INDEX idx4 BY (\"a\", \"2000-01-01 00:00:01\"), (\"b\", \"2019-04-17 14:26:19\"), (\"c\", \"\");",
                "split partition table t between (0) and (10000) regions 4;",
                "split region for table t index idx between (1000) and (10000) regions 2;",
                "split partition table t index idx between (1000) and (10000) regions 2;",
                "split partition table t partition (p1) between (0) and (10000) regions 2;",
                "split partition table t partition (p2) between (10000) and (20000) regions 2;",
                "split partition table t partition (p1,p2) index idx between (0) and (20000) regions 2;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println("原始的sql===" + sql);
                String newSql=statement.toString().replace("\n","").replace('\'','"')+";";
                System.out.println("生成的sql===" + newSql);
                assertTrue(newSql.equalsIgnoreCase(sql));
                SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
                statement.accept(visitor);
                System.out.println("getTables==" + visitor.getTables());
                Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
                assertFalse(tableMap.isEmpty());

            }
        }
    }
}
