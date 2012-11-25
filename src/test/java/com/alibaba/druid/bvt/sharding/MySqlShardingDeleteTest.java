package com.alibaba.druid.bvt.sharding;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sharding.config.LogicTable;
import com.alibaba.druid.sharding.config.MappingRuleAdapter;
import com.alibaba.druid.sharding.config.MappingRuleListEntry;
import com.alibaba.druid.sharding.config.RouteConfig;
import com.alibaba.druid.sharding.sql.MySqlShardingVisitor;
import com.alibaba.druid.sharding.sql.ShardingVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class MySqlShardingDeleteTest extends TestCase {

    private RouteConfig routeConfig = new RouteConfig();

    protected void setUp() throws Exception {
        MappingRuleAdapter rule = new MappingRuleAdapter();

        rule.setColumn("gender");
        rule.setTable("person");
        rule.getEntries().add(new MappingRuleListEntry("person_m", "M"));
        rule.getEntries().add(new MappingRuleListEntry("person_f", "F"));

        routeConfig.getMappingRules().put("person", rule);

        LogicTable logicTable = new LogicTable();
        logicTable.setLogicTable("person");

        logicTable.addPartition("person_m", "db0", "person_001");
        logicTable.addPartition("person_f", "db1", "person_002");

        routeConfig.getLogicTables().put("person", logicTable);
    }

    public void test_delete_m() throws Exception {
        String sql = "delete from person where name = 'xx' AND gender = 'M'";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        Assert.assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);
        MySqlShardingVisitor visitor = new MySqlShardingVisitor(routeConfig);
        stmt.accept(visitor);

        String result = SQLUtils.toMySqlString(stmt);

        Assert.assertEquals("DELETE FROM person_001" + //
                            "\nWHERE name = 'xx'" + //
                            "\n\tAND gender = 'M'", result);
        Assert.assertEquals("db0", stmt.getAttribute(ShardingVisitor.ATTR_DB));
    }

    public void test_delete_f_params() throws Exception {
        String sql = "delete from person where name = 'xx' AND gender = ?";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        Assert.assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);
        MySqlShardingVisitor visitor = new MySqlShardingVisitor(routeConfig, "F"); // 参数化

        stmt.accept(visitor);

        String result = SQLUtils.toMySqlString(stmt);

        Assert.assertEquals("DELETE FROM person_002" + //
                            "\nWHERE name = 'xx'" + //
                            "\n\tAND gender = ?", result);
        Assert.assertEquals("db1", stmt.getAttribute(ShardingVisitor.ATTR_DB));
    }
}
