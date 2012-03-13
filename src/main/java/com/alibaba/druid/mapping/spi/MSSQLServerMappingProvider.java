package com.alibaba.druid.mapping.spi;

import java.util.List;

import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.Top;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerSelectParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class MSSQLServerMappingProvider implements MappingProvider {

    @Override
    public MappingVisitor createMappingVisitor(MappingEngine engine) {
        return new MSSQLServerMappingVisitor(engine.getEntities());
    }

    @Override
    public SQLASTOutputVisitor createOutputVisitor(MappingEngine engine, Appendable out) {
        return new SQLServerOutputVisitor(out);
    }

    public SQLSelectQueryBlock explainToSelectSQLObject(MappingEngine engine, String sql) {
        SQLServerSelectParser selectParser = new SQLServerSelectParser(sql);
        SQLServerSelectQueryBlock query = (SQLServerSelectQueryBlock) selectParser.query();

        Integer maxLimit = engine.getMaxLimit();

        if (maxLimit != null) {
            if (query.getTop() == null) {
                Top top = new Top();
                top.setExpr(new SQLNumberExpr(maxLimit));
                query.setTop(top);
            } else {
                SQLNumericLiteralExpr rowCountExpr = (SQLNumericLiteralExpr) query.getTop().getExpr();
                int rowCount = rowCountExpr.getNumber().intValue();
                if (rowCount > maxLimit.intValue()) {
                    rowCountExpr.setNumber(maxLimit);
                }
            }
        }

        return query;
    }
    
    public SQLDeleteStatement explainToDeleteSQLObject(MappingEngine engine, String sql) {
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        return parser.parseDeleteStatement();
    }
    
    public SQLUpdateStatement explainToUpdateSQLObject(MappingEngine engine, String sql) {
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        return parser.parseUpdateStatement();
    }
    
    
    public SQLInsertStatement explainToInsertSQLObject(MappingEngine engine, String sql) {
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        return (SQLInsertStatement) parser.parseInsert();
    }

    @Override
    public ExportParameterVisitor createExportParameterVisitor(List<Object> parameters) {
        return new MSSQLServerExportParameterVisitor(parameters);
    }
}
