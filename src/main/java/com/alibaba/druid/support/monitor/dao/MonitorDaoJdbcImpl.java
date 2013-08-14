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

import javax.sql.DataSource;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.http.stat.WebAppStatValue;
import com.alibaba.druid.support.http.stat.WebURIStatValue;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.support.monitor.MField;
import com.alibaba.druid.support.monitor.MonitorContext;
import com.alibaba.druid.support.spring.stat.SpringMethodStatValue;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.druid.util.Utils;

public class MonitorDaoJdbcImpl implements MonitorDao {

    private final static Log LOG                      = LogFactory.getLog(MonitorDaoJdbcImpl.class);

    private DataSource       dataSource;

    private BeanInfo         sqlStatBeanInfo          = new BeanInfo(JdbcSqlStatValue.class);
    private BeanInfo         springMethodStatBeanInfo = new BeanInfo(SpringMethodStatValue.class);
    private BeanInfo         webURIStatBeanInfo       = new BeanInfo(WebURIStatValue.class);
    private BeanInfo         webAppStatBeanInfo       = new BeanInfo(WebAppStatValue.class);

    //

    public MonitorDaoJdbcImpl(){
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void saveSql(MonitorContext ctx, List<DruidDataSourceStatValue> dataSourceList) {
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
            FieldInfo field = fields.get(i);
            if (i != 0) {
                buf.append(", ");
            }
            buf.append(field.getColumnName());
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
            domain = "defaultDomain";
        }
        String app = (String) filters.get("app");
        if (StringUtils.isEmpty(app)) {
            app = "defaultApp";
        }

        String cluster = (String) filters.get("cluster");
        if (StringUtils.isEmpty(cluster)) {
            cluster = "defaultCluster";
        }

        String host = (String) filters.get("host");
        if (!StringUtils.isEmpty(host)) {
            buf.append("\nAND host = ?");
        }

        Integer pid = getInteger(filters, "pid");
        if (pid != null) {
            buf.append("\nAND pid = ?");
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

    private void save(BeanInfo beanInfo, MonitorContext ctx, List<?> list) {
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

    protected void setParameterForSqlStat(BeanInfo beanInfo, MonitorContext ctx, PreparedStatement stmt, Object sqlStat)
                                                                                                                        throws SQLException {
        int paramIndex = 1;

        setParam(stmt, paramIndex++, ctx.getDomainName());
        setParam(stmt, paramIndex++, ctx.getAppName());
        setParam(stmt, paramIndex++, ctx.getClusterName());
        setParam(stmt, paramIndex++, ctx.getHost());
        setParam(stmt, paramIndex++, ctx.getPID());
        setParam(stmt, paramIndex++, ctx.getCollectTime());

        try {
            List<FieldInfo> fields = beanInfo.getFields();
            for (int i = 0; i < fields.size(); ++i) {
                FieldInfo field = fields.get(i);
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
                } else {
                    throw new UnsupportedOperationException();
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
        for (int i = 0; i < fields.size(); ++i) {
            FieldInfo field = fields.get(i);
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
        private final List<FieldInfo> fields = new ArrayList<FieldInfo>();
        private String                insertSql;
        private String                tableName;

        public BeanInfo(Class<?> clazz){
            this.clazz = clazz;

            {
                MTable annotation = clazz.getAnnotation(MTable.class);
                if (annotation == null) {
                    throw new IllegalArgumentException(clazz.getName() + " not contains @MTable");
                }

                tableName = annotation.name();
            }

            for (Field field : JdbcSqlStatValue.class.getDeclaredFields()) {
                MField annotation = field.getAnnotation(MField.class);
                if (annotation == null) {
                    continue;
                }

                String columnName = annotation.name();
                if (StringUtils.isEmpty(columnName)) {
                    columnName = field.getName();
                }

                fields.add(new FieldInfo(field, columnName));
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

    }

    public static class FieldInfo {

        private final Field  field;
        private final String columnName;

        public FieldInfo(Field field, String columnName){
            this.field = field;
            this.columnName = columnName;

            field.setAccessible(true);
        }

        public Field getField() {
            return field;
        }

        public String getColumnName() {
            return columnName;
        }

        public Class<?> getFieldType() {
            return field.getType();
        }
    }
}
