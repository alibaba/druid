package com.alibaba.druid.mapping;

import java.util.LinkedHashMap;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class MappingEngine {

    private LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();
    private Integer                       maxLimit;
    private MappingProvider               provider;

    public Integer getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(Integer maxLimit) {
        this.maxLimit = maxLimit;
    }

    public LinkedHashMap<String, Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        this.entities.put(entity.getName(), entity);
    }

    public MappingVisitor createMappingVisitor() {
        return provider.createMappingVisitor(this);
    }

    public SQLASTOutputVisitor createOutputVisitor(Appendable out) {
        return provider.createOutputVisitor(this, out);
    }

    public String explainToSQL(String sql) {
        SQLSelectQueryBlock query = provider.explainToSQLObject(this, sql);

        query.accept(this.createMappingVisitor());

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor outputVisitor = createOutputVisitor(out);
        query.accept(outputVisitor);

        return out.toString();
    }
}
