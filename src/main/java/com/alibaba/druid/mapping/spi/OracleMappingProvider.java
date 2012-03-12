package com.alibaba.druid.mapping.spi;

import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleSelectParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class OracleMappingProvider implements MappingProvider {

    @Override
    public MappingVisitor createMappingVisitor(MappingEngine engine) {
        return new OracleMappingVisitor(engine.getEntities());
    }

    @Override
    public SQLASTOutputVisitor createOutputVisitor(MappingEngine engine, Appendable out) {
        return new OracleOutputVisitor(out);
    }

    public SQLSelectQueryBlock explainToSelectSQLObject(MappingEngine engine, String sql) {
        OracleSelectParser selectParser = new OracleSelectParser(sql);
        OracleSelectQueryBlock query = (OracleSelectQueryBlock) selectParser.query();

        return query;
    }

}
