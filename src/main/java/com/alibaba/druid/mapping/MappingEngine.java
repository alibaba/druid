package com.alibaba.druid.mapping;

import java.util.LinkedHashMap;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlSelectParser;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MappingEngine {

    private LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();
    private Integer                       maxLimit;

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

    public SQLASTVisitor createMySqlMappingVisitor() {
        return new MySqlMappingVisitor(entities);
    }

    public String explainToMySql(String sql) {
        MySqlSelectQueryBlock query = explainToMySqlObject(sql);

        query.accept(this.createMySqlMappingVisitor());

        return SQLUtils.toMySqlString(query);
    }

    public MySqlSelectQueryBlock explainToMySqlObject(String sql) {
        MySqlSelectParser selectParser = new MySqlSelectParser(sql);
        MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) selectParser.query();
        
        if (this.maxLimit != null) {
            if (query.getLimit() == null) {
                Limit limit = new Limit();
                limit.setRowCount(new SQLNumberExpr(maxLimit));
                query.setLimit(limit);
            } else {
                SQLNumberExpr rowCountExpr = (SQLNumberExpr) query.getLimit().getRowCount();
                int rowCount = rowCountExpr.getNumber().intValue();
                if (rowCount > maxLimit.intValue()) {
                    rowCountExpr.setNumber(maxLimit);
                }
            }
        }
        
        return query;
    }
}
