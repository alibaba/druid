package com.alibaba.druid.mapping;

import java.util.LinkedHashMap;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlSelectParser;

public class MappingEngine {

    private LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();

    public LinkedHashMap<String, Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        this.entities.put(entity.getName(), entity);
    }

    public EntityMapingMySqlVisitor createMappingVisitor() {
        return new EntityMapingMySqlVisitor(entities);
    }

    public String explain(String sql) {
        MySqlSelectParser selectParser = new MySqlSelectParser(sql);
        SQLSelectQuery query = selectParser.query();
        query.accept(this.createMappingVisitor());

        return SQLUtils.toMySqlString(query);
    }
}
