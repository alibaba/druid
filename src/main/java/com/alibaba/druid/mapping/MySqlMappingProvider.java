package com.alibaba.druid.mapping;

import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlSelectParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class MySqlMappingProvider implements MappingProvider {

    @Override
    public MappingVisitor createMappingVisitor(MappingEngine engine) {
        return new MySqlMappingVisitor(engine.getEntities());
    }

    @Override
    public SQLASTOutputVisitor createOutputVisitor(MappingEngine engine, Appendable out) {
        return new MySqlOutputVisitor(out);
    }

    public SQLSelectQueryBlock explainToSQLObject(MappingEngine engine, String sql) {
        MySqlSelectParser selectParser = new MySqlSelectParser(sql);
        MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) selectParser.query();

        Integer maxLimit = engine.getMaxLimit();

        if (maxLimit != null) {
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
