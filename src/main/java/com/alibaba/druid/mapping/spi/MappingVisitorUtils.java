package com.alibaba.druid.mapping.spi;

import java.util.Map;

import com.alibaba.druid.mapping.DruidMappingException;
import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.mapping.Property;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
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

            x.putAttribute("mapping.entity", entity);
            String tableName = visitor.resolveTableName(entity);
            tableExpr.setName(tableName);
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

    public static boolean visit(MappingVisitor visitor, SQLPropertyExpr x) {
        SQLIdentifierExpr ownerExpr = (SQLIdentifierExpr) x.getOwner();
        String ownerName = ownerExpr.getName();

        String propertyName = x.getName();

        Property property = null;
        Entity entity = visitor.getEntity(ownerName);
        property = entity.getProperty(propertyName);

        if (property == null) {
            throw new DruidMappingException("property not found : " + propertyName);
        }

        String dbColumName = visitor.resovleColumnName(entity, property);
        x.setName(dbColumName);
        x.putAttribute("mapping.property", property);
        x.putAttribute("mapping.entity", entity);

        if (x.getParent() instanceof SQLSelectItem) {
            SQLSelectItem selectItem = (SQLSelectItem) x.getParent();
            if (selectItem.getAlias() == null) {
                selectItem.setAlias('"' + property.getName() + '"');
            }
        }

        return false;
    }
    
    public static boolean visit(MappingVisitor visitor, SQLIdentifierExpr x) {
        String propertyName = x.getName();

        Property property = null;
        Entity propertyEntity = null;
        for (Entity entity : visitor.getEntities().values()) {
            property = entity.getProperty(propertyName);
            if (property != null) {
                propertyEntity = entity;
                break;
            }
        }

        if (property == null) {
            throw new DruidMappingException("property not found : " + propertyName);
        }

        String dbColumName = visitor.resovleColumnName(propertyEntity, property);
        x.setName(dbColumName);
        
        x.putAttribute("mapping.property", property);
        x.putAttribute("mapping.entity", propertyEntity);

        if (x.getParent() instanceof SQLSelectItem) {
            SQLSelectItem selectItem = (SQLSelectItem) x.getParent();
            if (selectItem.getAlias() == null) {
                selectItem.setAlias('"' + property.getName() + '"');
            }
        }

        return false;
    }
    
    public static boolean visit(MappingVisitor visitor, SQLBinaryOpExpr x) {
        if (x.getOperator() == SQLBinaryOperator.Equality) {
            if (x.getLeft() instanceof SQLIdentifierExpr && isSimpleValue(visitor, x.getRight())) {
                visit(visitor, (SQLIdentifierExpr) x.getLeft());
                x.getRight().accept(visitor);
                
                Entity entity = (Entity) x.getLeft().getAttribute("mapping.entity");
                Property property = (Property) x.getLeft().getAttribute("mapping.property");
                Object value = x.getRight().getAttribute("mapping.value");
                
                visitor.getPropertyValues().add(new PropertyValue(entity, property, value));
                
                return false;
            }
            
            if (x.getLeft() instanceof SQLPropertyExpr && isSimpleValue(visitor, x.getRight())) {
                visit(visitor, (SQLPropertyExpr) x.getLeft());
                x.getRight().accept(visitor);
                
                Entity entity = (Entity) x.getLeft().getAttribute("mapping.entity");
                Property property = (Property) x.getLeft().getAttribute("mapping.property");
                Object value = x.getRight().getAttribute("mapping.value");
                
                visitor.getPropertyValues().add(new PropertyValue(entity, property, value));
                
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean isSimpleValue(MappingVisitor visitor, SQLExpr expr) {
        if (expr instanceof SQLNumericLiteralExpr) {
            expr.putAttribute("mapping.value", ((SQLNumericLiteralExpr) expr).getNumber());
            return true;
        }
        
        if (expr instanceof SQLCharExpr) {
            expr.putAttribute("mapping.value", ((SQLCharExpr) expr).getText());
            return true;
        }
        
        if (expr instanceof SQLVariantRefExpr) {
            Map<String, Object> attributes = expr.getAttributes();
            Integer varIndex = (Integer) attributes.get("mapping.varIndex");
            if (varIndex == null) {
                varIndex = visitor.getAndIncrementVariantIndex();
                expr.putAttribute("mapping.varIndex", varIndex);
            }
            
            Object parameter = visitor.getParameters().get(varIndex);
            
            expr.putAttribute("mapping.value", parameter);
            return true;
        }
        
        return false;
    }
    
    public static boolean visit(MappingVisitor visitor, SQLVariantRefExpr x) {
        Map<String, Object> attributes = x.getAttributes();
        Integer varIndex = (Integer) attributes.get("mapping.varIndex");
        if (varIndex == null) {
            varIndex = visitor.getAndIncrementVariantIndex();
            x.putAttribute("mapping.varIndex", varIndex);
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
            from.putAttribute("mapping.entity", firstEntity);
            x.setFrom(from);
        }

        for (SQLSelectItem item : x.getSelectList()) {
            item.setParent(x);
        }

        return true;
    }

    public static Entity getEntity(MappingVisitor visitor, String name) {
        SQLTableSource tableSource = visitor.getTableSources().get(name);

        if (tableSource != null) {
            Entity entity = (Entity) tableSource.getAttribute("mapping.entity");
            if (entity != null) {
                return entity;
            }

            if (tableSource instanceof SQLExprTableSource) {
                SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();

                if (expr instanceof SQLIdentifierExpr) {
                    name = ((SQLIdentifierExpr) expr).getName();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

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
