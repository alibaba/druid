package com.alibaba.druid.mapping.spi;

import java.util.Iterator;
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
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
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
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;

public class MappingVisitorUtils {

    private static final String MAPPING_VAR_INDEX = "mapping.varIndex";
    private static final String MAPPING_VALUE     = "mapping.value";
    private static final String MAPPING_PROPERTY  = "mapping.property";
    private static final String MAPPING_ENTITY    = "mapping.entity";

    public static boolean visit(MappingVisitor visitor, SQLExprTableSource x) {
        SQLExpr expr = x.getExpr();

        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr tableExpr = (SQLIdentifierExpr) expr;
            String entityName = tableExpr.getName();

            Entity entity = (Entity) x.getAttribute(MAPPING_ENTITY);

            if (entity == null) {
                entity = visitor.getEntity(entityName);
            }

            if (entity == null) {
                throw new DruidMappingException("entity not found : " + entityName);
            }

            if (x.getParent() instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) x.getParent();
                if (queryBlock.getAttribute(MAPPING_ENTITY) == null) {
                    queryBlock.putAttribute(MAPPING_ENTITY, entity);
                }
            }

            x.putAttribute(MAPPING_ENTITY, entity);
            String tableName = visitor.resolveTableName(entity);
            tableExpr.setName(tableName);

            visitor.getTableSources().put(entityName, x);
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

    public static boolean fillSelectList(MappingVisitor visitor, SQLSelectQueryBlock x) {
        Entity entity = (Entity) x.getAttribute(MAPPING_ENTITY);

        if (entity == null && x.getFrom() != null) {
            entity = (Entity) x.getFrom().getAttribute(MAPPING_ENTITY);
        }

        if (entity == null) {
            return false;
        }

        x.getSelectList().clear();

        for (Property property : entity.getProperties().values()) {
            String columnName = visitor.resovleColumnName(entity, property);
            String alias = null;
            if (visitor.getContext().isGenerateAlias()) {
                alias = '"' + property.getName() + '"';
            }

            SQLExpr expr = new SQLIdentifierExpr(columnName);

            expr.putAttribute(MAPPING_ENTITY, entity);
            expr.putAttribute(MAPPING_PROPERTY, property);

            SQLSelectItem selelctItem = new SQLSelectItem(expr, alias);

            x.getSelectList().add(selelctItem);
        }

        return true;
    }

    public static boolean visit(MappingVisitor visitor, SQLPropertyExpr x) {
        SQLIdentifierExpr ownerExpr = (SQLIdentifierExpr) x.getOwner();
        String ownerName = ownerExpr.getName();

        String propertyName = x.getName();

        Property property = null;
        Entity entity = visitor.getEntity(ownerName);

        if (entity == null) {
            visitor.getUnresolveList().add(x);
            return false;
        }
        property = entity.getProperty(propertyName);

        if (property == null) {
            throw new DruidMappingException("property not found : " + propertyName);
        }

        String dbColumName = visitor.resovleColumnName(entity, property);
        x.setName(dbColumName);
        x.putAttribute(MAPPING_PROPERTY, property);
        x.putAttribute(MAPPING_ENTITY, entity);

        if (x.getParent() instanceof SQLSelectItem) {
            SQLSelectItem selectItem = (SQLSelectItem) x.getParent();
            if (visitor.getContext().isGenerateAlias() && selectItem.getAlias() == null) {
                selectItem.setAlias('"' + property.getName() + '"');
            }
        }

        return false;
    }

    public static boolean visit(MappingVisitor visitor, SQLAllColumnExpr x) {
        if (visitor.getContext().isExplainAllColumnToList()) {
            visitor.getUnresolveList().add(x);
        }

        return true;
    }

    public static boolean visit(MappingVisitor visitor, SQLIdentifierExpr x) {
        String propertyName = x.getName();

        Property property = null;
        Entity propertyEntity = null;

        for (SQLTableSource tableSource : visitor.getTableSources().values()) {
            Entity entity = (Entity) tableSource.getAttribute(MAPPING_ENTITY);
            if (entity != null) {
                property = entity.getProperty(propertyName);
                if (property != null) {
                    propertyEntity = entity;
                    break;
                }
            }
        }

        if (property == null) {
            visitor.getUnresolveList().add(x);
            return false;
        }

        String dbColumName = visitor.resovleColumnName(propertyEntity, property);
        x.setName(dbColumName);

        x.putAttribute(MAPPING_PROPERTY, property);
        x.putAttribute(MAPPING_ENTITY, propertyEntity);

        if (visitor.getContext().isGenerateAlias() && x.getParent() instanceof SQLSelectItem) {
            SQLSelectItem selectItem = (SQLSelectItem) x.getParent();
            if (visitor.getContext().isGenerateAlias() && selectItem.getAlias() == null) {
                selectItem.setAlias('"' + property.getName() + '"');
            }
        }

        return false;
    }

    public static boolean visit(MappingVisitor visitor, SQLSelectItem x) {
        x.getExpr().setParent(x);
        return true;
    }

    public static boolean visit(MappingVisitor visitor, SQLBinaryOpExpr x) {
        x.getLeft().setParent(x);
        x.getRight().setParent(x);

        if (x.getOperator() == SQLBinaryOperator.Equality) {
            if (x.getLeft() instanceof SQLIdentifierExpr && isSimpleValue(visitor, x.getRight())) {
                visit(visitor, (SQLIdentifierExpr) x.getLeft());
                x.getRight().accept(visitor);

                Entity entity = (Entity) x.getLeft().getAttribute(MAPPING_ENTITY);
                Property property = (Property) x.getLeft().getAttribute(MAPPING_PROPERTY);
                Object value = x.getRight().getAttribute(MAPPING_VALUE);

                PropertyValue propertyValue = new PropertyValue(entity, property, value);
                propertyValue.putAttribute("mapping.expr", x.getRight());
                
                visitor.getPropertyValues().add(propertyValue);

                return false;
            }

            if (x.getLeft() instanceof SQLPropertyExpr && isSimpleValue(visitor, x.getRight())) {
                visit(visitor, (SQLPropertyExpr) x.getLeft());
                x.getRight().accept(visitor);

                Entity entity = (Entity) x.getLeft().getAttribute(MAPPING_ENTITY);
                Property property = (Property) x.getLeft().getAttribute(MAPPING_PROPERTY);
                Object value = x.getRight().getAttribute(MAPPING_VALUE);

                PropertyValue propertyValue = new PropertyValue(entity, property, value);
                propertyValue.putAttribute("mapping.expr", x.getRight());
                
                visitor.getPropertyValues().add(propertyValue);

                return false;
            }
        }

        return true;
    }

    private static boolean isSimpleValue(MappingVisitor visitor, SQLExpr expr) {
        if (expr instanceof SQLNumericLiteralExpr) {
            expr.putAttribute(MAPPING_VALUE, ((SQLNumericLiteralExpr) expr).getNumber());
            return true;
        }

        if (expr instanceof SQLCharExpr) {
            expr.putAttribute(MAPPING_VALUE, ((SQLCharExpr) expr).getText());
            return true;
        }

        if (expr instanceof SQLVariantRefExpr) {
            Map<String, Object> attributes = expr.getAttributes();
            Integer varIndex = (Integer) attributes.get(MAPPING_VAR_INDEX);
            if (varIndex == null) {
                varIndex = visitor.getAndIncrementVariantIndex();
                expr.putAttribute(MAPPING_VAR_INDEX, varIndex);
            }

            if (visitor.getParameters().size() > varIndex) {
                Object parameter = visitor.getParameters().get(varIndex);
                expr.putAttribute(MAPPING_VALUE, parameter);
            }

            return true;
        }

        return false;
    }

    public static void afterResolve(MappingVisitor visitor) {
        for (Iterator<SQLExpr> iter = visitor.getUnresolveList().iterator(); iter.hasNext();) {
            SQLExpr expr = iter.next();
            if (expr instanceof SQLIdentifierExpr) {
                if (resolve(visitor, (SQLIdentifierExpr) expr)) {
                    iter.remove();
                }
            } else if (expr instanceof SQLPropertyExpr) {
                if (resolve(visitor, (SQLPropertyExpr) expr)) {
                    iter.remove();
                }
            } else if (expr instanceof SQLAllColumnExpr) {
                if (resolve(visitor, (SQLAllColumnExpr) expr)) {
                    iter.remove();
                }
            }
        }
    }

    public static boolean resolve(MappingVisitor visitor, SQLAllColumnExpr x) {
        if (!(x.getParent() instanceof SQLSelectItem)) {
            return true;
        }
        SQLSelectItem selectItem = (SQLSelectItem) x.getParent();

        if (!(selectItem.getParent() instanceof SQLSelectQueryBlock)) {
            return true;
        }

        SQLSelectQueryBlock select = (SQLSelectQueryBlock) selectItem.getParent();

        if (select.getSelectList().size() == 1) {
            if (select.getSelectList().get(0).getExpr() instanceof SQLAllColumnExpr) {
                boolean result = fillSelectList(visitor, select);
                if (!result && visitor.getContext().isExplainAllColumnToList()) {
                    visitor.getUnresolveList().add(x);
                }
            }
        }

        return false;
    }

    public static boolean resolve(MappingVisitor visitor, SQLIdentifierExpr x) {
        String propertyName = x.getName();

        for (SQLTableSource tableSource : visitor.getTableSources().values()) {
            Entity entity = (Entity) tableSource.getAttribute(MAPPING_ENTITY);
            if (entity != null) {
                Property property = entity.getProperty(propertyName);
                if (property != null) {
                    String columnName = visitor.resovleColumnName(entity, property);
                    x.setName(columnName);

                    x.putAttribute(MAPPING_ENTITY, entity);
                    x.putAttribute(MAPPING_PROPERTY, property);

                    if (visitor.getContext().isGenerateAlias() && x.getParent() instanceof SQLSelectItem) {
                        SQLSelectItem selectItem = (SQLSelectItem) x.getParent();
                        if (selectItem.getAlias() == null) {
                            selectItem.setAlias('"' + property.getName() + '"');
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public static boolean resolve(MappingVisitor visitor, SQLPropertyExpr x) {
        if (x.getOwner() instanceof SQLIdentifierExpr) {
            String ownerName = ((SQLIdentifierExpr) x.getOwner()).getName();
            SQLTableSource tableSource = visitor.getTableSources().get(ownerName);
            Entity entity = (Entity) tableSource.getAttribute(MAPPING_ENTITY);

            if (entity != null) {
                Property property = entity.getProperty(x.getName());
                if (property != null) {
                    String columnName = visitor.resovleColumnName(entity, property);
                    x.setName(columnName);
                    x.putAttribute(MAPPING_ENTITY, entity);
                    x.putAttribute(MAPPING_PROPERTY, property);

                    if (visitor.getContext().isGenerateAlias() && x.getParent() instanceof SQLSelectItem) {
                        SQLSelectItem selectItem = (SQLSelectItem) x.getParent();
                        if (selectItem.getAlias() == null) {
                            selectItem.setAlias('"' + property.getName() + '"');
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public static boolean visit(MappingVisitor visitor, SQLVariantRefExpr x) {
        Map<String, Object> attributes = x.getAttributes();
        Integer varIndex = (Integer) attributes.get(MAPPING_VAR_INDEX);
        if (varIndex == null) {
            varIndex = visitor.getAndIncrementVariantIndex();
            x.putAttribute(MAPPING_VAR_INDEX, varIndex);
        }
        return false;
    }
    
    public static boolean visit(MappingVisitor visitor, MySqlSelectQueryBlock x) {
        Integer maxLimit = visitor.getEngine().getMaxLimit();

        if (maxLimit != null) {
            if (x.getLimit() == null) {
                Limit limit = new Limit();
                limit.setRowCount(new SQLIntegerExpr(maxLimit));
                x.setLimit(limit);
            } else {
                SQLNumericLiteralExpr rowCountExpr = (SQLNumericLiteralExpr) x.getLimit().getRowCount();
                int rowCount = rowCountExpr.getNumber().intValue();
                if (rowCount > maxLimit.intValue()) {
                    rowCountExpr.setNumber(maxLimit);
                }
            }
        }
        
        return visit(visitor, (SQLSelectQueryBlock) x);
    }

    public static boolean visit(MappingVisitor visitor, SQLSelectQueryBlock x) {
        if (x.getSelectList().size() == 0) {
            SQLAllColumnExpr expr = new SQLAllColumnExpr();
            SQLSelectItem selectItem = new SQLSelectItem(expr);
            x.getSelectList().add(selectItem);

            expr.setParent(selectItem);
        }

        if (x.getFrom() == null) {
            Entity firstEntity = visitor.getEngine().getFirstEntity();
            SQLExprTableSource from = new SQLExprTableSource(new SQLIdentifierExpr(firstEntity.getName()));
            from.putAttribute(MAPPING_ENTITY, firstEntity);
            x.setFrom(from);
            x.putAttribute(MAPPING_ENTITY, firstEntity);
        }

        for (SQLSelectItem item : x.getSelectList()) {
            item.setParent(x);
            item.getExpr().setParent(item);
        }

        x.getFrom().setParent(x);
        if (x.getWhere() != null) {
            x.getWhere().setParent(x);
        }

        if (x.getInto() != null) {
            x.getInto().setParent(x);
        }

        return true;
    }

    public static Entity getEntity(MappingVisitor visitor, String name) {
        SQLTableSource tableSource = visitor.getTableSources().get(name);

        if (tableSource != null) {
            Entity entity = (Entity) tableSource.getAttribute(MAPPING_ENTITY);
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

    public static void setTableSource(MappingEngine engine, SQLDeleteStatement stmt) {
        if (stmt.getExprTableSource() == null) {
            Entity entity = engine.getFirstEntity();
            stmt.setTableSource(new SQLIdentifierExpr(entity.getName()));
        }
    }

    public static void setTableSource(MappingEngine engine, SQLUpdateStatement stmt) {
        if (stmt.getTableSource() == null) {
            Entity entity = engine.getFirstEntity();
            stmt.setTableSource(new SQLIdentifierExpr(entity.getName()));
        }
    }

    public static void setTableSource(MappingEngine engine, SQLInsertStatement stmt) {
        if (stmt.getTableSource() == null) {
            Entity entity = engine.getFirstEntity();
            stmt.setTableSource(new SQLIdentifierExpr(entity.getName()));
        }
    }
}
