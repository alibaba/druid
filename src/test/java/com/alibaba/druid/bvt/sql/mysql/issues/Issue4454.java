package com.alibaba.druid.bvt.sql.mysql.issues;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Test;

import java.util.HashSet;
import java.util.*;

import static org.junit.Assert.assertFalse;

public class Issue4454 {
    @Test
    public void test_idle2() throws Exception {
        String sql = "CREATE TABLE `test_trigger` (\n" +
                "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'trigger id',\n" +
                "  `test` int(11) signed NOT NULL DEFAULT '0' COMMENT 'trigger test',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);
        Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
        assertFalse(tableMap.isEmpty());
    }
}
