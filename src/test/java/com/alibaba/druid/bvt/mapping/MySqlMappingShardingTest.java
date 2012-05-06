package com.alibaba.druid.bvt.mapping;

import java.util.Collections;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.mapping.Property;
import com.alibaba.druid.mapping.spi.MappingVisitor;
import com.alibaba.druid.mapping.spi.PropertyValue;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;

public class MySqlMappingShardingTest extends TestCase {

    MappingEngine engine = new ShardingMappingEngine();

    protected void setUp() throws Exception {
        Entity entity = new Entity();
        entity.setName("用户");
        entity.setTableName("user");

        entity.addProperty(new Property("名称", "", "uid"));
        entity.addProperty(new Property("昵称", "", "name"));

        engine.addEntity(entity);
    }

    public void test_0() throws Exception {
        String oql = "select * from 用户 u where u.名称 = 'a'";

        String sql = engine.explainToSelectSQL(oql);

        String expected = "SELECT uid AS \"名称\", name AS \"昵称\"\n" + //
                        "FROM user_a u\n" + //
                        "WHERE u.uid = 'a'";
        
        Assert.assertEquals(expected, sql);
    }
    
    public void test_1() throws Exception {
        String oql = "select * from 用户 u where u.名称 = ?";

        String sql = engine.explainToSelectSQL(oql, Collections.<Object>singletonList("a"));

        String expected = "SELECT uid AS \"名称\", name AS \"昵称\"\n" + //
                        "FROM user_a u\n" + //
                        "WHERE u.uid = ?";
        
        Assert.assertEquals(expected, sql);
    }
    
    public void test_2() throws Exception {
        String oql = "select * from 用户 u where u.名称 = ?";

        String sql = engine.explainToSelectSQL(oql, Collections.<Object>singletonList("b"));

        String expected = "SELECT uid AS \"名称\", name AS \"昵称\"\n" + //
                        "FROM user_x u\n" + //
                        "WHERE u.uid = ?";
        
        Assert.assertEquals(expected, sql);
    }

    public static class ShardingMappingEngine extends MappingEngine {

        public void afterResole(MappingVisitor visitor) {
            String shardingTableName = null;
            for (PropertyValue entry : visitor.getPropertyValues()) {
                Entity entity = entry.getEntity();
                Property property = entry.getProperty();
                Object value = entry.getValue();

                if ("用户".equals(entity.getName()) && "名称".equals(property.getName())) {
                    if ("a".equals(value)) {
                        shardingTableName = "user_a";
                    } else {
                        shardingTableName = "user_x";
                    }
                }
            }

            for (SQLTableSource tableSource : visitor.getTableSources().values()) {
                Entity entity = (Entity) tableSource.getAttribute("mapping.entity");
                if (entity != null && "用户".equals(entity.getName())) {
                    SQLExprTableSource exprTableSource = (SQLExprTableSource) tableSource;
                    exprTableSource.setExpr(new SQLIdentifierExpr(shardingTableName));
                }
            }
        }
    }
}
