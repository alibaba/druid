package com.alibaba.druid.mapping;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

public class MySqlMappingVisitor extends MySqlASTVisitorAdapter implements MappingVisitor {

    private final LinkedHashMap<String, Entity> entities;
    private final Map<String, SQLTableSource>   tableSources = new LinkedHashMap<String, SQLTableSource>();

    public MySqlMappingVisitor(LinkedHashMap<String, Entity> entities){
        this.entities = entities;
    }

    public Map<String, SQLTableSource> getTableSources() {
        return tableSources;
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public Entity getFirstEntity() {
        for (Map.Entry<String, Entity> entry : entities.entrySet()) {
            return entry.getValue();
        }

        return null;
    }

    public Entity getEntity(String name) {
        Entity entity = this.entities.get(name);

        if (entity == null) {
            for (Map.Entry<String, Entity> entry : entities.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name)) {
                    entity = entry.getValue();
                    break;
                }
            }
        }

        return entity;
    }

    public boolean visit(SQLSelectItem x) {
        x.getExpr().setParent(x);
        return true;
    }

    public boolean visit(MySqlSelectQueryBlock x) {
        return MappingVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLSelectQueryBlock x) {
        return MappingVisitorUtils.visit(this, x);
    }

    protected void fillSelectList(SQLSelectQueryBlock x) {
        MappingVisitorUtils.fillSelectList(this, x);
    }

    public boolean visit(SQLIdentifierExpr x) {
        return MappingVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLSubqueryTableSource x) {
        return MappingVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLJoinTableSource x) {
        return MappingVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLExprTableSource x) {
        return MappingVisitorUtils.visit(this, x);
    }
}
