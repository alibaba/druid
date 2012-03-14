package com.alibaba.druid.mapping.spi;

import java.util.List;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleSelectParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class OracleMappingProvider implements MappingProvider {

    @Override
    public MappingVisitor createMappingVisitor(MappingEngine engine) {
        return new OracleMappingVisitor(engine.getEntities());
    }

    @Override
    public SQLASTOutputVisitor createOutputVisitor(MappingEngine engine, Appendable out) {
        return new OracleOutputVisitor(out, false);
    }

    public SQLSelectQueryBlock explainToSelectSQLObject(MappingEngine engine, String sql) {
        OracleSelectParser selectParser = new OracleSelectParser(sql);
        OracleSelectQueryBlock query = (OracleSelectQueryBlock) selectParser.query();

        return query;
    }

    public OracleDeleteStatement explainToDeleteSQLObject(MappingEngine engine, String sql) {
        OracleStatementParser parser = new OracleStatementParser(sql);
        OracleDeleteStatement stmt = parser.parseDeleteStatement();
        
        if (stmt.getTableSource() == null) {
            Entity entity = engine.getFirstEntity();
            stmt.setTableName(new SQLIdentifierExpr(entity.getName()));
        }

        return stmt;
    }

    public SQLUpdateStatement explainToUpdateSQLObject(MappingEngine engine, String sql) {
        OracleStatementParser parser = new OracleStatementParser(sql);
        return parser.parseUpdateStatement();
    }

    public SQLInsertStatement explainToInsertSQLObject(MappingEngine engine, String sql) {
        OracleStatementParser parser = new OracleStatementParser(sql);
        return (SQLInsertStatement) parser.parseInsert();
    }

    @Override
    public ExportParameterVisitor createExportParameterVisitor(List<Object> parameters) {
        return new OracleExportParameterVisitor(parameters);
    }
}
