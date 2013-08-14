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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private final static Log LOG             = LogFactory.getLog(MonitorDaoJdbcImpl.class);

    private DataSource       dataSource;

    private BeanInfo         sqlStatBeanInfo = new BeanInfo(JdbcSqlStatValue.class);

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

            String sql = buildInsertSql(sqlStatBeanInfo);
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = dataSource.getConnection();
                stmt = conn.prepareStatement(sql);

                for (JdbcSqlStatValue statValue : sqlList) {
                    setParameterForSqlStat(sqlStatBeanInfo, stmt, statValue);
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
    }

    protected void setParameterForSqlStat(BeanInfo beanInfo, PreparedStatement stmt, JdbcSqlStatValue sqlStat)
                                                                                                              throws SQLException {
        int paramIndex = 1;
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

    public String buildInsertSql(BeanInfo beanInfo) {
        String sql = beanInfo.insertSql;

        if (sql != null) {
            return sql;
        }

        StringBuilder buf = new StringBuilder();

        buf.append("INSERT INTO ") //
        .append(getTableName(beanInfo));

        buf.append(" (");
        List<FieldInfo> fields = beanInfo.getFields();
        for (int i = 0; i < fields.size(); ++i) {
            FieldInfo field = fields.get(i);
            if (i != 0) {
                buf.append(", ");
            }
            buf.append(field.getColumnName());
        }
        buf.append(")\nVALUES (");
        for (int i = 0; i < fields.size(); ++i) {
            if (i != 0) {
                buf.append(", ?");
            } else {
                buf.append("?");
            }
        }
        buf.append(")");
        sql = buf.toString();
        beanInfo.setInsertSql(sql);
        return sql;
    }

    protected String getTableName(BeanInfo beanInfo) {
        return "druid_sql";
    }

    protected long getSqlHash(String sql) {
        return Utils.murmurhash2_64(sql);
    }

    @Override
    public void saveSpringMethod(MonitorContext ctx, List<SpringMethodStatValue> methodList) {

    }

    @Override
    public void saveWebURI(MonitorContext ctx, List<WebURIStatValue> uriList) {

    }

    @Override
    public void saveWebApp(MonitorContext ctx, List<WebAppStatValue> uriList) {

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

        public BeanInfo(Class<?> clazz){
            this.clazz = clazz;
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
