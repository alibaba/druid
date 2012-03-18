package com.alibaba.druid.mapping.spi;

import java.util.Map;

import com.alibaba.druid.mapping.DruidMappingException;
import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.mapping.Property;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;

public class MappingVisitorUtils {

    public static boolean visit(MappingVisitor visitor, SQLExprTableSource x) {
        SQLExpr expr = x.getExpr();

        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr tableExpr = (SQLIdentifierExpr) expr;
            String entityName = tableExpr.getName();

            Entity entity = visitor.getEntity(entityName);

            if (entity == null) {
                throw new DruidMappingException("entity not foudn : " + entityName);
            }

            tableExpr.setName(entity.getTableName());
        }

        if (x.getAlias() != null) {
            visitor.getTableSources().put(x.getAlias(), x);
        }

        return false;
    }

    public static boolean visit(MappingVisitor visitor, SQLTableSource x) {
        if (x.getAlias() != null) {
            visitor.getTableSources().put(x.getAlias(), x);
        }

        return true;
    }

    public static void fillSelectList(MappingVisitor visitor, SQLSelectQueryBlock x) {
        Entity entity = visitor.getFirstEntity();

        for (Property item : entity.getProperties().values()) {
            x.getSelectList().add(new SQLSelectItem(new SQLIdentifierExpr(item.getName()), '"' + item.getName() + '"'));
        }
    }

    public static boolean visit(MappingVisitor visitor, SQLIdentifierExpr x) {
        String propertyName = x.getName();

        Property property = null;
        for (Entity entity : visitor.getEntities().values()) {
            property = entity.getProperty(propertyName);
            if (property != null) {
                break;
            }
        }

        if (property == null) {
            throw new DruidMappingException("property not found : " + propertyName);
        }

        String dbColumName = property.getDbColumnName();
        x.setName(dbColumName);

        if (x.getParent() instanceof SQLSelectItem) {
            SQLSelectItem selectItem = (SQLSelectItem) x.getParent();
            if (selectItem.getAlias() == null) {
                selectItem.setAlias('"' + property.getName() + '"');
            }
        }

        return false;
    }

    public static boolean visit(MappingVisitor visitor, SQLSelectQueryBlock x) {
        if (x.getSelectList().size() == 0) {
            fillSelectList(visitor, x);
        }

        if (x.getSelectList().size() == 1) {
            if (x.getSelectList().get(0).getExpr() instanceof SQLAllColumnExpr) {
                x.getSelectList().clear();
                fillSelectList(visitor, x);
            }
        }

        if (x.getFrom() == null) {
            Entity firstEntity = visitor.getFirstEntity();
            SQLExprTableSource from = new SQLExprTableSource(new SQLIdentifierExpr(firstEntity.getName()));
            x.setFrom(from);
        }

        for (SQLSelectItem item : x.getSelectList()) {
            item.setParent(x);
        }

        return true;
    }

    public static Entity getEntity(MappingVisitor visitor, String name) {
        Entity entity = visitor.getEntities().get(name);

        if (entity == null) {
            for (Map.Entry<String, Entity> entry : visitor.getEntities().entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name)) {
                    entity = entry.getValue();
                    break;
                }
            }
        }

        return entity;
    }

    public static void setDataSource(MappingEngine engine, SQLDeleteStatement stmt) {
        if (stmt.getExprTableSource() == null) {
            Entity entity = engine.getFirstEntity();
            stmt.setTableSource(new SQLIdentifierExpr(entity.getName()));
        }
    }
    
    public static void setDataSource(MappingEngine engine, SQLUpdateStatement stmt) {
        if (stmt.getTableSource() == null) {
            Entity entity = engine.getFirstEntity();
            stmt.setTableSource(new SQLIdentifierExpr(entity.getName()));
        }
    }
    
    public static void setDataSource(MappingEngine engine, SQLInsertStatement stmt) {
        if (stmt.getTableSource() == null) {
            Entity entity = engine.getFirstEntity();
            stmt.setTableSource(new SQLIdentifierExpr(entity.getName()));
        }
    }
}
