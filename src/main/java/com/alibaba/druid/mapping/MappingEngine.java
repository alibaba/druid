package com.alibaba.druid.mapping;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.mapping.spi.MappingProvider;
import com.alibaba.druid.mapping.spi.MappingVisitor;
import com.alibaba.druid.mapping.spi.MySqlMappingProvider;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcUtils;

public class MappingEngine {

    private LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();
    private Integer                       maxLimit;
    private final MappingProvider         provider;
    private DataSource                    dataSource;

    public MappingEngine(){
        this(new MySqlMappingProvider());
    }

    public MappingEngine(MappingProvider provider){
        this.provider = provider;
    }

    public Entity getFirstEntity(MappingContext context) {
        Entity entity = context.getDefaultEntity();

        if (entity != null) {
            return entity;
        }

        for (Map.Entry<String, Entity> entry : entities.entrySet()) {
            return entry.getValue();
        }

        return null;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public MappingProvider getMappingProvider() {
        return provider;
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

    public MappingVisitor createMappingVisitor(List<Object> parameters) {
        return createMappingVisitor(new MappingContext(parameters));
    }

    public MappingVisitor createMappingVisitor(MappingContext context) {
        return provider.createMappingVisitor(this, context);
    }

    public SQLASTOutputVisitor createOutputVisitor(Appendable out) {
        return provider.createOutputVisitor(this, out);
    }

    public String explain(String sql) {
        return explain(sql);
    }

    public String explain(String sql, MappingContext context) {
        List<SQLStatement> stmtList = provider.explain(this, sql);

        if (stmtList.size() > 0) {
            throw new IllegalArgumentException(sql);
        }

        SQLStatement stmt = stmtList.get(0);

        MappingVisitor visitor = this.createMappingVisitor(context);
        stmt.accept(visitor);
        visitor.afterResolve();
        afterResole(visitor);

        return toSQL(stmt);
    }

    public SQLSelectQueryBlock explainToSelectSQLObject(String sql) {
        return explainToSelectSQLObject(sql, new MappingContext());
    }

    public SQLSelectQueryBlock explainToSelectSQLObject(String sql, MappingContext context) {
        return provider.explainToSelectSQLObject(this, sql, context);
    }

    public String explainToSelectSQL(String sql) {
        return explainToSelectSQL(sql, Collections.emptyList());
    }

    public String explainToSelectSQL(String sql, List<Object> parameters) {
        return explainToSelectSQL(sql, new MappingContext(parameters));
    }

    public String explainToSelectSQL(String sql, MappingContext context) {
        SQLSelectQueryBlock query = explainToSelectSQLObject(sql, context);

        MappingVisitor visitor = this.createMappingVisitor(context);
        query.accept(visitor);
        visitor.afterResolve();
        afterResole(visitor);

        return toSQL(query);
    }

    public void afterResole(MappingVisitor visitor) {

    }

    public SQLDeleteStatement explainToDeleteSQLObject(String sql, MappingContext context) {
        return provider.explainToDeleteSQLObject(this, sql, context);
    }

    public String explainToDeleteSQL(String sql) {
        return explainToDeleteSQL(sql, new MappingContext());
    }

    public String explainToDeleteSQL(String sql, MappingContext context) {
        SQLDeleteStatement stmt = explainToDeleteSQLObject(sql, context);

        MappingVisitor visitor = this.createMappingVisitor(context);
        stmt.accept(visitor);
        visitor.afterResolve();
        afterResole(visitor);

        return toSQL(stmt);
    }

    public String resolveTableName(Entity entity, MappingContext context) {
        return entity.getTableName();
    }

    public String resovleColumnName(Entity entity, Property property, MappingContext context) {
        return property.getDbColumnName();
    }

    public SQLUpdateStatement explainToUpdateSQLObject(String sql, MappingContext context) {
        return provider.explainToUpdateSQLObject(this, sql, context);
    }

    public String explainToUpdateSQL(String sql) {
        return explainToUpdateSQL(sql, new MappingContext());
    }

    public String explainToUpdateSQL(String sql, MappingContext context) {
        SQLUpdateStatement stmt = explainToUpdateSQLObject(sql, context);

        MappingVisitor visitor = this.createMappingVisitor(context);
        stmt.accept(visitor);
        visitor.afterResolve();
        afterResole(visitor);

        return toSQL(stmt);
    }

    public SQLInsertStatement explainToInsertSQLObject(String sql, MappingContext context) {
        return provider.explainToInsertSQLObject(this, sql, context);
    }

    public String explainToInsertSQL(String sql) {
        return explainToInsertSQL(sql, new MappingContext());
    }

    public String explainToInsertSQL(String sql, MappingContext context) {
        SQLInsertStatement stmt = explainToInsertSQLObject(sql, context);

        MappingVisitor visitor = this.createMappingVisitor(context);
        stmt.accept(visitor);
        visitor.afterResolve();
        afterResole(visitor);

        return toSQL(stmt);
    }

    public List<Object> exportParameters(SQLObject sqlObject) {
        List<Object> parameters = new ArrayList<Object>();
        exportParameters(sqlObject, parameters);
        return parameters;
    }

    public void exportParameters(SQLObject sqlObject, List<Object> parameters) {
        ExportParameterVisitor exporter = this.provider.createExportParameterVisitor(parameters);
        sqlObject.accept(exporter);
    }

    public String toSQL(SQLObject sqlObject) {
        return toSQL(sqlObject, false);
    }

    public String toSQL(SQLObject sqlObject, boolean exportParameter) {
        if (exportParameter) {
            exportParameters(sqlObject);
        }

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor outputVisitor = createOutputVisitor(out);
        sqlObject.accept(outputVisitor);

        return out.toString();
    }

    public List<Map<String, Object>> select(Connection conn, String sql, List<Object> parameters) throws SQLException {
        return select(conn, sql, new MappingContext(parameters));
    }

    public List<Map<String, Object>> select(Connection conn, String sql, MappingContext context) throws SQLException {
        SQLSelectQueryBlock sqlObject = this.explainToSelectSQLObject(sql, context);
        exportParameters(sqlObject, context.getParameters());
        String rawSql = this.toSQL(sqlObject);
        return JdbcUtils.executeQuery(conn, rawSql, context.getParameters());
    }

    public int delete(Connection conn, String sql, List<Object> parameters) throws SQLException {
        return delete(conn, sql, new MappingContext(parameters));
    }

    public int delete(Connection conn, String sql, MappingContext context) throws SQLException {
        SQLDeleteStatement sqlObject = this.explainToDeleteSQLObject(sql, context);
        exportParameters(sqlObject, context.getParameters());
        String rawSql = this.toSQL(sqlObject);
        int updateCount = JdbcUtils.executeUpdate(conn, rawSql, context.getParameters());
        return updateCount;
    }

    public int update(Connection conn, String sql, List<Object> parameters) throws SQLException {
        return update(conn, sql, new MappingContext(parameters));
    }

    public int update(Connection conn, String sql, MappingContext context) throws SQLException {
        SQLUpdateStatement sqlObject = this.explainToUpdateSQLObject(sql, context);
        exportParameters(sqlObject, context.getParameters());
        String rawSql = this.toSQL(sqlObject);
        int updateCount = JdbcUtils.executeUpdate(conn, rawSql, context.getParameters());
        return updateCount;
    }
    
    public void insert(Connection conn, String sql, Object... parameters) throws SQLException {
        insert(conn, sql, Arrays.asList(parameters));
    }

    public void insert(Connection conn, String sql, List<Object> parameters) throws SQLException {
        insert(conn, sql, new MappingContext(parameters));
    }

    public void insert(Connection conn, String sql, MappingContext context) throws SQLException {
        SQLInsertStatement sqlObject = this.explainToInsertSQLObject(sql, context);
        exportParameters(sqlObject, context.getParameters());
        String rawSql = this.toSQL(sqlObject);
        JdbcUtils.execute(conn, rawSql, context.getParameters());
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("datasource not init.");
        }
        return this.dataSource.getConnection();
    }

    public List<Map<String, Object>> select(String sql, Object... parameters) throws SQLException {
        return select(sql, Arrays.asList(parameters));
    }

    public List<Map<String, Object>> select(String sql, List<Object> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return select(conn, sql, parameters);
        } finally {
            JdbcUtils.close(conn);
        }
    }
    
    public int delete(String sql, Object... parameters) throws SQLException {
        return delete(sql, Arrays.asList(parameters));
    }

    public int delete(String sql, List<Object> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return delete(conn, sql, parameters);
        } finally {
            JdbcUtils.close(conn);
        }
    }

    public int update(String sql, List<Object> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return update(conn, sql, parameters);
        } finally {
            JdbcUtils.close(conn);
        }
    }
    
    public void insert(String sql, Object... parameters) throws SQLException {
        insert(sql, Arrays.asList(parameters));
    }

    public void insert(String sql, List<Object> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            insert(conn, sql, parameters);
        } finally {
            JdbcUtils.close(conn);
        }
    }
}
