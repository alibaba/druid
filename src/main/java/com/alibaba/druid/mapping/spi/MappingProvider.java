/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.mapping.spi;

import java.util.List;

import com.alibaba.druid.mapping.MappingContext;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public interface MappingProvider {

    MappingVisitor createMappingVisitor(MappingEngine engine);
    
    MappingVisitor createMappingVisitor(MappingEngine engine, MappingContext context);
    
    ExportParameterVisitor createExportParameterVisitor(List<Object> parameters);

    SQLASTOutputVisitor createOutputVisitor(MappingEngine engine, Appendable out);
    
    List<SQLStatement> explain(MappingEngine engine, String sql);

    SQLSelectQueryBlock explainToSelectSQLObject(MappingEngine engine, String sql, MappingContext context);

    SQLDeleteStatement explainToDeleteSQLObject(MappingEngine engine, String sql, MappingContext context);

    SQLUpdateStatement explainToUpdateSQLObject(MappingEngine engine, String sql, MappingContext context);
    
    SQLInsertStatement explainToInsertSQLObject(MappingEngine engine, String sql, MappingContext context);
}
