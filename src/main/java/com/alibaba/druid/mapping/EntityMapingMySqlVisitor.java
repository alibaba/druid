package com.alibaba.druid.mapping;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

public class EntityMapingMySqlVisitor extends MySqlASTVisitorAdapter {

    private final Entity entity;

    public EntityMapingMySqlVisitor(Entity entity){
        super();
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

}
