package com.alibaba.druid.mapping;

import java.util.LinkedHashMap;

import com.alibaba.druid.mapping.spi.MappingProvider;
import com.alibaba.druid.mapping.spi.MappingVisitor;
import com.alibaba.druid.mapping.spi.MySqlMappingProvider;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class MappingEngine {

    private LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();
    private Integer                       maxLimit;
    private final MappingProvider         provider;

    public MappingEngine(){
        this(new MySqlMappingProvider());
    }

    public MappingEngine(MappingProvider provider){
        this.provider = provider;
    }

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

    public SQLSelectQueryBlock explainToSelectSQLObject(String sql) {
        return provider.explainToSelectSQLObject(this, sql);
    }

    public String explainToSelectSQL(String sql) {
        SQLSelectQueryBlock query = explainToSelectSQLObject(sql);

        query.accept(this.createMappingVisitor());

        return toSQL(query);
    }

    public SQLDeleteStatement explainToDeleteSQLObject(String sql) {
        return provider.explainToDeleteSQLObject(this, sql);
    }

    public String explainToDeleteSQLObjectSQL(String sql) {
        SQLDeleteStatement query = explainToDeleteSQLObject(sql);

        query.accept(this.createMappingVisitor());

        return toSQL(query);
    }

    public String toSQL(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor outputVisitor = createOutputVisitor(out);
        sqlObject.accept(outputVisitor);

        return out.toString();
    }

    public SQLUpdateStatement explainToUpdateSQLObject(String sql) {
        return provider.explainToUpdateSQLObject(this, sql);
    }

    public String explainToUpdateSQL(String sql) {
        SQLUpdateStatement query = explainToUpdateSQLObject(sql);

        query.accept(this.createMappingVisitor());

        return toSQL(query);
    }
    
    public SQLInsertStatement explainToInsertSQLObject(String sql) {
        return provider.explainToInsertSQLObject(this, sql);
    }
    
    public String explainToInsertSQL(String sql) {
        SQLInsertStatement query = explainToInsertSQLObject(sql);
        
        query.accept(this.createMappingVisitor());
        
        return toSQL(query);
    }
}
