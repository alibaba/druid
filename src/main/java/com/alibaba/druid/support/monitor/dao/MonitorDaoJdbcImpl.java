/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.monitor.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.http.stat.WebAppStatValue;
import com.alibaba.druid.support.http.stat.WebURIStatValue;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.support.monitor.MonitorContext;
import com.alibaba.druid.support.monitor.annotation.AggregateType;
import com.alibaba.druid.support.monitor.annotation.MField;
import com.alibaba.druid.support.monitor.annotation.MTable;
import com.alibaba.druid.support.monitor.entity.MonitorApp;
import com.alibaba.druid.support.monitor.entity.MonitorCluster;
import com.alibaba.druid.support.monitor.entity.MonitorInstance;
import com.alibaba.druid.support.spring.stat.SpringMethodStatValue;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.wall.WallFunctionStatValue;
import com.alibaba.druid.wall.WallProviderStatValue;
import com.alibaba.druid.wall.WallSqlStatValue;
import com.alibaba.druid.wall.WallTableStatValue;

public class MonitorDaoJdbcImpl implements MonitorDao {

    private final static Log                                   LOG                      = LogFactory.getLog(MonitorDaoJdbcImpl.class);

    private DataSource                                         dataSource;

    private BeanInfo                                           dataSourceStatBeanInfo   = new BeanInfo(
                                                                                                       DruidDataSourceStatValue.class);
    private BeanInfo                                           sqlStatBeanInfo          = new BeanInfo(
                                                                                                       JdbcSqlStatValue.class);
    private BeanInfo                                           springMethodStatBeanInfo = new BeanInfo(
                                                                                                       SpringMethodStatValue.class);
    private BeanInfo                                           webURIStatBeanInfo       = new BeanInfo(
                                                                                                       WebURIStatValue.class);
    private BeanInfo                                           webAppStatBeanInfo       = new BeanInfo(
                                                                                                       WebAppStatValue.class);

    private BeanInfo                                           wallProviderStatBeanInfo = new BeanInfo(
                                                                                                       WallProviderStatValue.class);
    private BeanInfo                                           wallSqlStatBeanInfo      = new BeanInfo(
                                                                                                       WallSqlStatValue.class);
    private BeanInfo                                           wallTableStatBeanInfo    = new BeanInfo(
                                                                                                       WallTableStatValue.class);
    private BeanInfo                                           wallFunctionStatBeanInfo = new BeanInfo(
                                                                                                       WallFunctionStatValue.class);

    private ConcurrentMap<String, ConcurrentMap<Long, String>> cacheMap                 = new ConcurrentHashMap<String, ConcurrentMap<Long, String>>();

    public MonitorDaoJdbcImpl(){
    }

    public void createTables(String dbType) {
        String[] resources = new String[] { "basic.sql", //
                "const.sql", //
                "datasource.sql", //
                "springmethod.sql", //
                "sql.sql", //
                "webapp.sql", //
                "weburi.sql", "wall.sql" };

        for (String item : resources) {
            String path = "/support/monitor/" + dbType + "/" + item;
            try {
                String text = Utils.readFromResource(path);
                String[] sqls = text.split(";");
                for (String sql : sqls) {
                    JdbcUtils.execute(dataSource, sql);
                }
            } catch (Exception ex) {
                LOG.error("create table error", ex);
            }
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveSql(MonitorContext ctx, List<DruidDataSourceStatValue> dataSourceList) {
        save(dataSourceStatBeanInfo, ctx, dataSourceList);

        for (DruidDataSourceStatValue dataSourceStatValue : dataSourceList) {
            List<JdbcSqlStatValue> sqlList = dataSourceStatValue.getSqlList();
            save(sqlStatBeanInfo, ctx, sqlList);
        }
    }

    @Override
    public void saveSpringMethod(MonitorContext ctx, List<SpringMethodStatValue> list) {
        save(springMethodStatBeanInfo, ctx, list);
    }

    @Override
    public void saveWebURI(MonitorContext ctx, List<WebURIStatValue> list) {
        save(webURIStatBeanInfo, ctx, list);
    }

    @Override
    public void saveSqlWall(MonitorContext ctx, List<WallProviderStatValue> statList) {
        save(wallProviderStatBeanInfo, ctx, statList);

        for (WallProviderStatValue providerStat : statList) {
            save(wallSqlStatBeanInfo, ctx, providerStat.getWhiteList());
            save(wallSqlStatBeanInfo, ctx, providerStat.getBlackList());

            save(wallTableStatBeanInfo, ctx, providerStat.getTables());
            save(wallFunctionStatBeanInfo, ctx, providerStat.getFunctions());
        }
    }

    @Override
    public void saveWebApp(MonitorContext ctx, List<WebAppStatValue> list) {
        save(webAppStatBeanInfo, ctx, list);
    }

    @SuppressWarnings("unchecked")
    public List<JdbcSqlStatValue> loadSqlList(Map<String, Object> filters) {
        return ((List<JdbcSqlStatValue>) load(sqlStatBeanInfo, filters));
    }

    static Integer getInteger(Map<String, Object> filters, String key) {
        Object value = filters.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (value instanceof String) {

            String text = (String) value;
            if (StringUtils.isEmpty(text)) {
                return null;
            }

            return Integer.parseInt(text);
        }

        return null;
    }

    static Date getDate(Map<String, Object> filters, String key) {
        Object value = filters.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            long millis = ((Number) value).longValue();
            return new Date(millis);
        }

        if (value instanceof String) {

            String text = (String) value;
            if (StringUtils.isEmpty(text)) {
                return null;
            }

            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(text);
            } catch (ParseException e) {
                LOG.error("parse filter error", e);
                return null;
            }
        }

        return null;
    }

    private List<?> load(BeanInfo beanInfo, Map<String, Object> filters) {
        List<Object> list = new ArrayList<Object>();

        StringBuilder buf = new StringBuilder();

        buf.append("SELECT ");

        List<FieldInfo> fields = beanInfo.getFields();
        for (int i = 0; i < fields.size(); ++i) {
            FieldInfo fieldInfo = fields.get(i);
            if (i != 0) {
                buf.append(", ");
            }

            AggregateType aggregateType = fieldInfo.getField().getAnnotation(MField.class).aggregate();

            switch (aggregateType) {
                case Sum:
                    buf.append("SUM(");
                    buf.append(fieldInfo.getColumnName());
                    buf.append(")");
                    break;
                case Max:
                    buf.append("MAX(");
                    buf.append(fieldInfo.getColumnName());
                    buf.append(")");
                    break;
                case None:
                case Last:
                default:
                    buf.append(fieldInfo.getColumnName());
                    break;
            }
        }

        buf.append("\nFROM ");
        buf.append(getTableName(beanInfo));
        buf.append("\nWHERE collectTime >= ? AND collectTime <= ? AND domain = ? AND app = ? AND cluster = ?");

        Date startTime = getDate(filters, "startTime");
        if (startTime == null) {
            long now = System.currentTimeMillis();
            startTime = new Date(now - 1000 * 60 * 30); // 3 hours
        }

        Date endTime = getDate(filters, "endTime");
        if (endTime == null) {
            endTime = new Date(); // now
        }

        String domain = (String) filters.get("domain");
        if (StringUtils.isEmpty(domain)) {
            domain = "default";
        }
        String app = (String) filters.get("app");
        if (StringUtils.isEmpty(app)) {
            app = "default";
        }

        String cluster = (String) filters.get("cluster");
        if (StringUtils.isEmpty(cluster)) {
            cluster = "default";
        }

        String host = (String) filters.get("host");
        if (!StringUtils.isEmpty(host)) {
            buf.append("\nAND host = ?");
        }

        Integer pid = getInteger(filters, "pid");
        if (pid != null) {
            buf.append("\nAND pid = ?");
        }

        List<FieldInfo> groupByFields = beanInfo.getGroupByFields();
        for (int i = 0; i < groupByFields.size(); ++i) {
            if (i == 0) {
                buf.append("\nGROUP BY ");
            } else {
                buf.append(", ");
            }
            FieldInfo fieldInfo = groupByFields.get(i);
            buf.append(fieldInfo.getColumnName());
        }

        {
            Integer offset = (Integer) filters.get("offset");
            Integer limit = (Integer) filters.get("limit");

            if (limit == null) {
                limit = 1000;
            }

            buf.append("\nLIMIT ");
            if (offset != null) {
                buf.append(offset);
                buf.append(", ");
            }
            buf.append(limit);
        }

        String sql = buf.toString();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);

            int paramIndex = 1;

            stmt.setTimestamp(paramIndex++, new Timestamp(startTime.getTime()));
            stmt.setTimestamp(paramIndex++, new Timestamp(endTime.getTime()));

            stmt.setString(paramIndex++, domain);
            stmt.setString(paramIndex++, app);
            stmt.setString(paramIndex++, cluster);

            if (!StringUtils.isEmpty(host)) {
                stmt.setString(paramIndex++, host);
            }

            if (pid != null) {
                stmt.setInt(paramIndex++, pid);
            }

            rs = stmt.executeQuery();
            while (rs.next()) {
                Object object = createInstance(beanInfo);

                for (int i = 0; i < fields.size(); ++i) {
                    FieldInfo field = fields.get(i);
                    readFieldValue(object, field, rs, i + 1);
                }

                list.add(object);
            }

            stmt.close();
        } catch (SQLException ex) {
            LOG.error("save sql error", ex);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }

        for (FieldInfo hashField : beanInfo.getHashFields()) {
            loadHashValue(hashField, list, filters);
        }

        return list;
    }

    protected void readFieldValue(Object object, FieldInfo field, ResultSet rs, int paramIndex) throws SQLException {
        Class<?> fieldType = field.getFieldType();
        Object fieldValue = null;
        if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
            fieldValue = rs.getInt(paramIndex);
        } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
            fieldValue = rs.getLong(paramIndex);
        } else if (fieldType.equals(String.class)) {
            fieldValue = rs.getString(paramIndex);
        } else if (fieldType.equals(Date.class)) {
            Timestamp timestamp = rs.getTimestamp(paramIndex);
            if (timestamp != null) {
                fieldValue = new Date(timestamp.getTime());
            }
        } else {
            throw new UnsupportedOperationException();
        }
        try {
            field.getField().set(object, fieldValue);
        } catch (IllegalArgumentException e) {
            throw new DruidRuntimeException("set field error" + field.getField(), e);
        } catch (IllegalAccessException e) {
            throw new DruidRuntimeException("set field error" + field.getField(), e);
        }
    }

    private void loadHashValue(FieldInfo hashField, List<?> list, Map<String, Object> filters) {
        String domain = (String) filters.get("domain");
        if (StringUtils.isEmpty(domain)) {
            domain = "default";
        }
        String app = (String) filters.get("app");
        if (StringUtils.isEmpty(app)) {
            app = "default";
        }

        for (Object statValue : list) {
            try {
                Long hash = (Long) hashField.field.get(statValue);
                String value = cacheGet(hashField.getHashForType(), hash);

                if (value == null) {
                    value = getConstValueFromDb(domain, app, hashField.getHashForType(), hash);
                }
                hashField.getHashFor().set(statValue, value);
            } catch (IllegalArgumentException e) {
                throw new DruidRuntimeException("set field error" + hashField.getField(), e);
            } catch (IllegalAccessException e) {
                throw new DruidRuntimeException("set field error" + hashField.getField(), e);
            }
        }
    }

    protected String getConstValueFromDb(String domain, String app, String type, Long hash) {
        String sql = "select value from druid_const where domain = ? AND app = ? and type = ? and hash = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, domain);
            stmt.setString(2, app);
            stmt.setString(3, type);
            stmt.setLong(4, hash);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            LOG.error("save const error error", ex);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }

        return null;
    }

    private void saveHash(FieldInfo hashField, MonitorContext ctx, List<?> list) {
        final String hashType = hashField.getHashForType();

        for (Object statValue : list) {
            try {
                Long hash = (Long) hashField.field.get(statValue);
                if (!cacheContains(hashField.getHashForType(), hash)) {
                    String value = (String) hashField.getHashFor().get(statValue);
                    final String sql = "insert into druid_const (domain, app, type, hash, value) values (?, ?, ?, ?, ?)";
                    Connection conn = null;
                    PreparedStatement stmt = null;
                    try {
                        conn = dataSource.getConnection();
                        stmt = conn.prepareStatement(sql);

                        stmt.setString(1, ctx.getDomain());
                        stmt.setString(2, ctx.getApp());
                        stmt.setString(3, hashType);
                        stmt.setLong(4, hash);
                        stmt.setString(5, value);

                        stmt.execute();
                        stmt.close();

                    } catch (SQLException ex) {
                        // LOG.error("save const error error", ex);
                    } finally {
                        JdbcUtils.close(stmt);
                        JdbcUtils.close(conn);
                    }
                    cachePut(hashField.getHashForType(), hash, value);
                }
            } catch (IllegalArgumentException e) {
                throw new DruidRuntimeException("set field error" + hashField.getField(), e);
            } catch (IllegalAccessException e) {
                throw new DruidRuntimeException("set field error" + hashField.getField(), e);
            }
        }
    }

    private void save(BeanInfo beanInfo, MonitorContext ctx, List<?> list) {
        if (list.size() == 0) {
            return;
        }

        for (FieldInfo hashField : beanInfo.getHashFields()) {
            saveHash(hashField, ctx, list);
        }

        String sql = buildInsertSql(beanInfo);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);

            for (Object statValue : list) {
                setParameterForSqlStat(beanInfo, ctx, stmt, statValue);
                stmt.addBatch();
            }

            stmt.executeBatch();

            stmt.close();
        } catch (SQLException ex) {
            LOG.error("save sql error", ex);
        } finally {
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }

    protected void setParameterForSqlStat(BeanInfo beanInfo, //
                                          MonitorContext ctx, //
                                          PreparedStatement stmt, //
                                          Object sqlStat) throws SQLException {
        int paramIndex = 1;

        setParam(stmt, paramIndex++, ctx.getDomain());
        setParam(stmt, paramIndex++, ctx.getApp());
        setParam(stmt, paramIndex++, ctx.getCluster());
        setParam(stmt, paramIndex++, ctx.getHost());
        setParam(stmt, paramIndex++, ctx.getPID());
        setParam(stmt, paramIndex++, ctx.getCollectTime());

        try {
            List<FieldInfo> fields = beanInfo.getFields();
            for (FieldInfo field : fields) {
                Class<?> fieldType = field.getFieldType();
                Object value = field.getField().get(sqlStat);

                if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                    setParam(stmt, paramIndex, (Integer) value);
                } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                    setParam(stmt, paramIndex, (Long) value);
                } else if (fieldType.equals(String.class)) {
                    setParam(stmt, paramIndex, (String) value);
                } else if (fieldType.equals(Date.class)) {
                    setParam(stmt, paramIndex, (Date) value);
                } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                    setParam(stmt, paramIndex, (Boolean) value);
                } else {
                    throw new UnsupportedOperationException("not support type : " + fieldType);
                }

                paramIndex++;
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DruidRuntimeException("setParam error", ex);
        }
    }

    public Object createInstance(BeanInfo beanInfo) {
        try {
            return beanInfo.getClazz().newInstance();
        } catch (InstantiationException ex) {
            throw new DruidRuntimeException("create instance error", ex);
        } catch (IllegalAccessException ex) {
            throw new DruidRuntimeException("create instance error", ex);
        }
    }

    public String buildInsertSql(BeanInfo beanInfo) {
        String sql = beanInfo.insertSql;

        if (sql != null) {
            return sql;
        }

        StringBuilder buf = new StringBuilder();

        buf.append("INSERT INTO ") //
        .append(getTableName(beanInfo));

        buf.append(" (domain, app, cluster, host, pid, collectTime");
        List<FieldInfo> fields = beanInfo.getFields();
        for (FieldInfo field : fields) {
            buf.append(", ");
            buf.append(field.getColumnName());
        }
        buf.append(")\nVALUES (?, ?, ?, ?, ?, ?");
        for (int i = 0; i < fields.size(); ++i) {
            buf.append(", ?");
        }
        buf.append(")");
        sql = buf.toString();
        beanInfo.setInsertSql(sql);
        return sql;
    }

    public String getTableName(BeanInfo beanInfo) {
        return beanInfo.getTableName();
    }

    protected long getSqlHash(String sql) {
        return Utils.murmurhash2_64(sql);
    }

    static void setParam(PreparedStatement stmt, int paramIndex, String value) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, Types.VARCHAR);
        } else {
            stmt.setString(paramIndex, value);
        }
    }

    static void setParam(PreparedStatement stmt, int paramIndex, Boolean value) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, Types.BOOLEAN);
        } else {
            stmt.setBoolean(paramIndex, value);
        }
    }

    static void setParam(PreparedStatement stmt, int paramIndex, Long value) throws SQLException {
        if (value == null || value == 0) {
            stmt.setNull(paramIndex, Types.BIGINT);
        } else {
            stmt.setLong(paramIndex, value);
        }
    }

    static void setParam(PreparedStatement stmt, int paramIndex, Integer value) throws SQLException {
        if (value == null || value == 0) {
            stmt.setNull(paramIndex, Types.INTEGER);
        } else {
            stmt.setInt(paramIndex, value);
        }
    }

    static void setParam(PreparedStatement stmt, int paramIndex, Date value) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, Types.TIMESTAMP);
        } else {
            stmt.setTimestamp(paramIndex, new Timestamp(value.getTime()));
        }
    }

    public static class BeanInfo {

        private final Class<?>        clazz;
        private final List<FieldInfo> fields        = new ArrayList<FieldInfo>();
        private final List<FieldInfo> groupByFields = new ArrayList<FieldInfo>();
        private final List<FieldInfo> hashFields    = new ArrayList<FieldInfo>();
        private final String          tableName;

        private String                insertSql;

        public BeanInfo(Class<?> clazz){
            this.clazz = clazz;

            {
                MTable annotation = clazz.getAnnotation(MTable.class);
                if (annotation == null) {
                    throw new IllegalArgumentException(clazz.getName() + " not contains @MTable");
                }

                tableName = annotation.name();
            }

            for (Field field : clazz.getDeclaredFields()) {
                MField annotation = field.getAnnotation(MField.class);
                if (annotation == null) {
                    continue;
                }

                String columnName = annotation.name();
                if (StringUtils.isEmpty(columnName)) {
                    columnName = field.getName();
                }

                Field hashFor = null;
                String hashForType = null;
                if (!StringUtils.isEmpty(annotation.hashFor())) {
                    try {
                        hashFor = clazz.getDeclaredField(annotation.hashFor());
                        hashForType = annotation.hashForType();
                    } catch (Exception e) {
                        throw new IllegalStateException("hashFor error", e);
                    }
                }

                FieldInfo fieldInfo = new FieldInfo(field, columnName, hashFor, hashForType);
                fields.add(fieldInfo);

                if (annotation.groupBy()) {
                    groupByFields.add(fieldInfo);
                }

                if (hashFor != null) {
                    hashFields.add(fieldInfo);
                }
            }
        }

        public String getTableName() {
            return tableName;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getInsertSql() {
            return insertSql;
        }

        public void setInsertSql(String insertSql) {
            this.insertSql = insertSql;
        }

        public List<FieldInfo> getFields() {
            return fields;
        }

        public List<FieldInfo> getGroupByFields() {
            return groupByFields;
        }

        public List<FieldInfo> getHashFields() {
            return hashFields;
        }

    }

    public boolean cacheContains(String type, Long hash) {
        Map<Long, String> cache = cacheMap.get(type);
        if (cache == null) {
            return false;
        }
        return cache.containsKey(hash);
    }

    public String cacheGet(String type, Long hash) {
        Map<Long, String> cache = cacheMap.get(type);
        if (cache == null) {
            return null;
        }
        return cache.get(hash);
    }

    public void cachePut(String type, Long hash, String value) {
        ConcurrentMap<Long, String> cache = cacheMap.get(type);
        if (cache == null) {
            cacheMap.putIfAbsent(type, new ConcurrentHashMap<Long, String>(16, 0.75f, 1));
            cache = cacheMap.get(type);
        }
        cache.putIfAbsent(hash, value);
    }

    public static class FieldInfo {

        private final Field  field;
        private final String columnName;
        private final Field  hashFor;
        private final String hashForType;

        public FieldInfo(Field field, String columnName, Field hashFor, String hashForType){
            this.field = field;
            this.columnName = columnName;
            this.hashFor = hashFor;
            this.hashForType = hashForType;

            field.setAccessible(true);
            if (hashFor != null) {
                hashFor.setAccessible(true);
            }
        }

        public String getHashForType() {
            return hashForType;
        }

        public Field getField() {
            return field;
        }

        public Field getHashFor() {
            return hashFor;
        }

        public String getColumnName() {
            return columnName;
        }

        public Class<?> getFieldType() {
            return field.getType();
        }

    }

    public void insertAppIfNotExits(String domain, String app) throws SQLException {
        MonitorApp monitorApp = findApp(domain, app);
        if (monitorApp != null) {
            return;
        }

        String sql = "insert druid_app (domain, app) values (?, ?)";
        JdbcUtils.execute(dataSource, sql, domain, app);
    }

    public List<MonitorApp> listApp(String domain) throws SQLException {
        List<MonitorApp> list = new ArrayList<MonitorApp>();

        String sql = "select id, domain, app from druid_app " //
                     + " where domain = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, domain);

            rs = stmt.executeQuery();
            if (rs.next()) {
                list.add(readApp(rs));
            }

            return list;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }

    public MonitorApp findApp(String domain, String app) throws SQLException {
        String sql = "select id, domain, app from druid_app " //
                     + " where domain = ? and app = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, domain);
            stmt.setString(2, app);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return readApp(rs);
            }

            return null;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }

    private MonitorApp readApp(ResultSet rs) throws SQLException {
        MonitorApp app = new MonitorApp();

        app.setId(rs.getLong(1));
        app.setDomain(rs.getString(2));
        app.setApp(rs.getString(3));

        return app;
    }

    public List<MonitorCluster> listCluster(String domain, String app) throws SQLException {
        List<MonitorCluster> list = new ArrayList<MonitorCluster>();

        String sql = "select id, domain, app, cluster from druid_cluster " //
                     + " where domain = ?";

        if (app != null) {
            sql += " and app = ?";
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, domain);
            if (app != null) {
                stmt.setString(2, app);
            }

            rs = stmt.executeQuery();
            if (rs.next()) {
                list.add(readCluster(rs));
            }

            return list;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }

    public void insertClusterIfNotExits(String domain, String app, String cluster) throws SQLException {
        MonitorCluster monitorApp = findCluster(domain, app, cluster);
        if (monitorApp != null) {
            return;
        }

        String sql = "insert druid_cluster (domain, app, cluster) values (?, ?, ?)";
        JdbcUtils.execute(dataSource, sql, domain, app, cluster);
    }

    public MonitorCluster findCluster(String domain, String app, String cluster) throws SQLException {
        String sql = "select id, domain, app, cluster from druid_cluster " //
                     + " where domain = ? and app = ? and cluster = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, domain);
            stmt.setString(2, app);
            stmt.setString(3, cluster);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return readCluster(rs);
            }

            return null;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }

    private MonitorCluster readCluster(ResultSet rs) throws SQLException {
        MonitorCluster app = new MonitorCluster();

        app.setId(rs.getLong(1));
        app.setDomain(rs.getString(2));
        app.setApp(rs.getString(3));
        app.setCluster(rs.getString(4));

        return app;
    }

    public void insertOrUpdateInstance(String domain, String app, String cluster, String host, String ip,
                                       Date startTime, long pid) throws SQLException {
        MonitorInstance monitorInst = findInst(domain, app, cluster, host);
        if (monitorInst == null) {
            String sql = "insert into druid_inst (domain, app, cluster, host, ip, lastActiveTime, lastPID) " //
                         + " values (?, ?, ?, ?, ?, ?, ?)";
            JdbcUtils.execute(dataSource, sql, domain, app, cluster, host, ip, startTime, pid);
        } else {
            String sql = "update druid_inst set ip = ?, lastActiveTime = ?, lastPID = ? " //
                         + " where domain = ? and app = ? and cluster = ? and host = ? ";
            JdbcUtils.execute(dataSource, sql, ip, startTime, pid, domain, app, cluster, host);
        }
    }

    public MonitorInstance findInst(String domain, String app, String cluster, String host) throws SQLException {
        String sql = "select id, domain, app, cluster, host, ip, lastActiveTime, lastPID from druid_inst " //
                     + " where domain = ? and app = ? and cluster = ? and host = ? " //
                     + " limit 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, domain);
            stmt.setString(2, app);
            stmt.setString(3, cluster);
            stmt.setString(4, host);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return readInst(rs);
            }

            return null;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }

    public List<MonitorInstance> listInst(String domain, String app, String cluster) throws SQLException {
        List<MonitorInstance> list = new ArrayList<MonitorInstance>();

        String sql = "select id, domain, app, cluster, host, ip, lastActiveTime, lastPID from druid_inst " //
                     + "where domain = ?";

        if (app != null) {
            sql += " and app = ?";
        }

        if (cluster != null) {
            sql += " and cluster = ?";
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);

            int paramIndex = 1;
            stmt.setString(paramIndex++, domain);

            if (app != null) {
                stmt.setString(paramIndex++, app);
            }

            if (cluster != null) {
                stmt.setString(paramIndex++, cluster);
            }

            rs = stmt.executeQuery();
            if (rs.next()) {
                list.add(readInst(rs));
            }

            return list;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }

    private MonitorInstance readInst(ResultSet rs) throws SQLException {
        MonitorInstance inst = new MonitorInstance();

        inst.setId(rs.getLong(1));
        inst.setDomain(rs.getString(2));
        inst.setApp(rs.getString(3));
        inst.setCluster(rs.getString(4));
        inst.setHost(rs.getString(5));

        inst.setIp(rs.getString(6));
        inst.setLastActiveTime(rs.getTimestamp(7));
        inst.setLastPID(rs.getLong(8));

        return inst;
    }
}
