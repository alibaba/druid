package com.alibaba.druid.mapping.spi;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVIsitorAdapter;

public class OracleMappingVisitor extends OracleASTVIsitorAdapter implements MappingVisitor {

    private final LinkedHashMap<String, Entity> entities;
    private final Map<String, SQLTableSource>   tableSources = new LinkedHashMap<String, SQLTableSource>();

    public OracleMappingVisitor(LinkedHashMap<String, Entity> entities){
        super();
        this.entities = entities;
    }

    public LinkedHashMap<String, Entity> getEntities() {
        return entities;
    }

    public Map<String, SQLTableSource> getTableSources() {
        return tableSources;
    }

    public Entity getFirstEntity() {
        for (Map.Entry<String, Entity> entry : entities.entrySet()) {
            return entry.getValue();
        }

        return null;
    }

    public Entity getEntity(String name) {
        return MappingVisitorUtils.getEntity(this, name);
    }

    @Override
    public boolean visit(SQLSelectItem x) {
        x.getExpr().setParent(x);
        return true;
    }

    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        return MappingVisitorUtils.visit(this, x);
    }

    @Override
    public boolean visit(OracleSelectTableReference x) {
        return MappingVisitorUtils.visit(this, x);
    }
}
