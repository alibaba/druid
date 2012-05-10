package com.alibaba.druid.mapping.spi;

import java.util.List;

import com.alibaba.druid.mapping.MappingContext;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleSelectParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleExportParameterVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class OracleMappingProvider implements MappingProvider {

    @Override
    public MappingVisitor createMappingVisitor(MappingEngine engine) {
        return new OracleMappingVisitor(engine);
    }
    
    @Override
    public MappingVisitor createMappingVisitor(MappingEngine engine, MappingContext context) {
        return new OracleMappingVisitor(engine, context);
    }

    @Override
    public SQLASTOutputVisitor createOutputVisitor(MappingEngine engine, Appendable out) {
        return new OracleOutputVisitor(out, false);
    }

    public SQLSelectQueryBlock explainToSelectSQLObject(MappingEngine engine, String sql, MappingContext context) {
        OracleSelectParser selectParser = new OracleSelectParser(sql);
        OracleSelectQueryBlock query = (OracleSelectQueryBlock) selectParser.query();

        return query;
    }

    public OracleDeleteStatement explainToDeleteSQLObject(MappingEngine engine, String sql, MappingContext context) {
        OracleStatementParser parser = new OracleStatementParser(sql);
        OracleDeleteStatement stmt = parser.parseDeleteStatement();

        MappingVisitorUtils.setTableSource(engine, stmt);

        return stmt;
    }

    public SQLUpdateStatement explainToUpdateSQLObject(MappingEngine engine, String sql, MappingContext context) {
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLUpdateStatement stmt = parser.parseUpdateStatement();

        MappingVisitorUtils.setTableSource(engine, stmt);

        return stmt;
    }

    public SQLInsertStatement explainToInsertSQLObject(MappingEngine engine, String sql, MappingContext context) {
        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLInsertStatement stmt = (SQLInsertStatement) parser.parseInsert();
        MappingVisitorUtils.setTableSource(engine, stmt);
        return stmt;

    }

    @Override
    public ExportParameterVisitor createExportParameterVisitor(List<Object> parameters) {
        return new OracleExportParameterVisitor(parameters);
    }
    
    @Override
    public List<SQLStatement> explain(MappingEngine engine, String sql) {
        OracleStatementParser parser = new OracleStatementParser(sql);
        return parser.parseStatementList();
    }
}
