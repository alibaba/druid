package com.alibaba.druid.mapping.spi;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitorAdapter;

public class MSSQLServerMappingVisitor extends SQLServerASTVisitorAdapter implements MappingVisitor {

    private final MappingEngine                     engine;
    private final Map<String, SQLTableSource> tableSources = new LinkedHashMap<String, SQLTableSource>();

    private final List<Object>                parameters;

    public MSSQLServerMappingVisitor(MappingEngine engine){
        this(engine, Collections.emptyList());
    }

    public MSSQLServerMappingVisitor(MappingEngine engine, List<Object> parameters){
        this.engine = engine;
        this.parameters = parameters;
    }

    public MappingEngine getEngine() {
        return engine;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public LinkedHashMap<String, Entity> getEntities() {
        return engine.getEntities();
    }

    public Map<String, SQLTableSource> getTableSources() {
        return tableSources;
    }

    @Override
    public Entity getFirstEntity() {
        return engine.getFirstEntity();
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
    public boolean visit(SQLServerSelectQueryBlock x) {
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
    public boolean visit(SQLPropertyExpr x) {
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

}
