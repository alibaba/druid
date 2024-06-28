/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.pool;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class DruidDataSourceFactory implements ObjectFactory {
    private static final Log LOG = LogFactory.getLog(DruidDataSourceFactory.class);

    static final int UNKNOWN_TRANSACTIONISOLATION = -1;

    public static final String PROP_DEFAULTAUTOCOMMIT = "defaultAutoCommit";
    public static final String PROP_DEFAULTREADONLY = "defaultReadOnly";
    public static final String PROP_DEFAULTTRANSACTIONISOLATION = "defaultTransactionIsolation";
    public static final String PROP_DEFAULTCATALOG = "defaultCatalog";
    public static final String PROP_DRIVERCLASSNAME = "driverClassName";
    public static final String PROP_MAXACTIVE = "maxActive";
    public static final String PROP_MAXIDLE = "maxIdle";
    public static final String PROP_MINIDLE = "minIdle";
    public static final String PROP_INITIALSIZE = "initialSize";
    public static final String PROP_MAXWAIT = "maxWait";
    public static final String PROP_TESTONBORROW = "testOnBorrow";
    public static final String PROP_TESTONRETURN = "testOnReturn";
    public static final String PROP_TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";
    public static final String PROP_NUMTESTSPEREVICTIONRUN = "numTestsPerEvictionRun";
    public static final String PROP_MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";
    public static final String PROP_MAXEVICTABLEIDLETIMEMILLIS = "maxEvictableIdleTimeMillis";
    public static final String PROP_PHY_TIMEOUT_MILLIS = "phyTimeoutMillis";
    public static final String PROP_TESTWHILEIDLE = "testWhileIdle";
    public static final String PROP_PASSWORD = "password";
    public static final String PROP_URL = "url";
    public static final String PROP_USERNAME = "username";
    public static final String PROP_VALIDATIONQUERY = "validationQuery";
    public static final String PROP_VALIDATIONQUERY_TIMEOUT = "validationQueryTimeout";
    public static final String PROP_INITCONNECTIONSQLS = "initConnectionSqls";
    public static final String PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED = "accessToUnderlyingConnectionAllowed";
    public static final String PROP_REMOVEABANDONED = "removeAbandoned";
    public static final String PROP_REMOVEABANDONEDTIMEOUT = "removeAbandonedTimeout";
    public static final String PROP_LOGABANDONED = "logAbandoned";
    public static final String PROP_POOLPREPAREDSTATEMENTS = "poolPreparedStatements";
    public static final String PROP_MAXOPENPREPAREDSTATEMENTS = "maxOpenPreparedStatements";
    public static final String PROP_CONNECTIONPROPERTIES = "connectionProperties";
    public static final String PROP_FILTERS = "filters";
    public static final String PROP_EXCEPTION_SORTER = "exceptionSorter";
    public static final String PROP_EXCEPTION_SORTER_CLASS_NAME = "exception-sorter-class-name";
    public static final String PROP_NAME = "name";
    public static final String PROP_INIT = "init";

    public static final String PROP_CONNECT_TIMEOUT = "connectTimeout";
    public static final String PROP_SOCKET_TIMEOUT = "socketTimeout";
    private static final String[] ALL_PROPERTIES = {
            PROP_DEFAULTAUTOCOMMIT,
            PROP_DEFAULTREADONLY,
            PROP_DEFAULTTRANSACTIONISOLATION,
            PROP_DEFAULTCATALOG,
            PROP_DRIVERCLASSNAME,
            PROP_MAXACTIVE,
            PROP_MAXIDLE,
            PROP_MINIDLE,
            PROP_INITIALSIZE,
            PROP_MAXWAIT,
            PROP_TESTONBORROW,
            PROP_TESTONRETURN,
            PROP_TIMEBETWEENEVICTIONRUNSMILLIS,
            PROP_NUMTESTSPEREVICTIONRUN,
            PROP_MINEVICTABLEIDLETIMEMILLIS,
            PROP_MAXEVICTABLEIDLETIMEMILLIS,
            PROP_TESTWHILEIDLE,
            PROP_PASSWORD,
            PROP_FILTERS,
            PROP_URL,
            PROP_USERNAME,
            PROP_VALIDATIONQUERY,
            PROP_VALIDATIONQUERY_TIMEOUT,
            PROP_INITCONNECTIONSQLS,
            PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED,
            PROP_REMOVEABANDONED,
            PROP_REMOVEABANDONEDTIMEOUT,
            PROP_LOGABANDONED,
            PROP_POOLPREPAREDSTATEMENTS,
            PROP_MAXOPENPREPAREDSTATEMENTS,
            PROP_CONNECTIONPROPERTIES,
            PROP_EXCEPTION_SORTER,
            PROP_EXCEPTION_SORTER_CLASS_NAME,
            PROP_INIT,
            PROP_NAME,
            PROP_CONNECT_TIMEOUT,
            PROP_SOCKET_TIMEOUT,

            "druid.timeBetweenLogStatsMillis",
            "druid.stat.sql.MaxSize",
            "druid.clearFiltersEnable",
            "druid.resetStatEnable", //
            "druid.notFullTimeoutRetryCount", //
            "druid.maxWaitThreadCount", //
            "druid.failFast", //
            "druid.phyTimeoutMillis", //
            "druid.wall.tenantColumn", //
            "druid.wall.updateAllow", //
            "druid.wall.deleteAllow", //
            "druid.wall.insertAllow", //
            "druid.wall.selelctAllow", //
            "druid.wall.multiStatementAllow", //
    };

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
            throws Exception {
        // We only know how to deal with <code>javax.naming.Reference</code>s
        // that specify a class name of "javax.sql.DataSource"
        if ((obj == null) || !(obj instanceof Reference)) {
            return null;
        }
        Reference ref = (Reference) obj;

        if ((!"javax.sql.DataSource".equals(ref.getClassName())) //
                && (!"com.alibaba.druid.pool.DruidDataSource".equals(ref.getClassName())) //
        ) {
            return null;
        }

        Properties properties = new Properties();
        for (int i = 0; i < ALL_PROPERTIES.length; i++) {
            String propertyName = ALL_PROPERTIES[i];
            RefAddr ra = ref.get(propertyName);
            if (ra != null) {
                String propertyValue = ra.getContent().toString();
                properties.setProperty(propertyName, propertyValue);
            }
        }

        return createDataSourceInternal(properties);
    }

    protected DataSource createDataSourceInternal(Properties properties) throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        config(dataSource, properties);
        return dataSource;
    }

    @SuppressWarnings("rawtypes")
    public static DataSource createDataSource(Properties properties) throws Exception {
        return createDataSource((Map) properties);
    }

    @SuppressWarnings("rawtypes")
    public static DataSource createDataSource(Map properties) throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        config(dataSource, properties);
        return dataSource;
    }

    @SuppressWarnings({"deprecation", "rawtypes"})
    public static void config(DruidDataSource dataSource, Map<?, ?> properties) throws SQLException {
        String value = null;

        value = (String) properties.get(PROP_NAME);
        if (value != null) {
            dataSource.setName(value);
        }

        value = (String) properties.get(PROP_DEFAULTAUTOCOMMIT);
        if (value != null) {
            dataSource.setDefaultAutoCommit(Boolean.valueOf(value).booleanValue());
        }

        value = (String) properties.get(PROP_DEFAULTREADONLY);
        if (value != null) {
            dataSource.setDefaultReadOnly(Boolean.valueOf(value).booleanValue());
        }

        value = (String) properties.get(PROP_DEFAULTTRANSACTIONISOLATION);
        if (value != null) {
            int level = UNKNOWN_TRANSACTIONISOLATION;
            if ("NONE".equalsIgnoreCase(value)) {
                level = Connection.TRANSACTION_NONE;
            } else if ("READ_COMMITTED".equalsIgnoreCase(value)) {
                level = Connection.TRANSACTION_READ_COMMITTED;
            } else if ("READ_UNCOMMITTED".equalsIgnoreCase(value)) {
                level = Connection.TRANSACTION_READ_UNCOMMITTED;
            } else if ("REPEATABLE_READ".equalsIgnoreCase(value)) {
                level = Connection.TRANSACTION_REPEATABLE_READ;
            } else if ("SERIALIZABLE".equalsIgnoreCase(value)) {
                level = Connection.TRANSACTION_SERIALIZABLE;
            } else {
                try {
                    level = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    LOG.error("Could not parse defaultTransactionIsolation: " + value);
                    LOG.error("WARNING: defaultTransactionIsolation not set");
                    LOG.error("using default value of database driver");
                    level = UNKNOWN_TRANSACTIONISOLATION;
                }
            }
            dataSource.setDefaultTransactionIsolation(level);
        }

        value = (String) properties.get(PROP_DEFAULTCATALOG);
        if (value != null) {
            dataSource.setDefaultCatalog(value);
        }

        value = (String) properties.get(PROP_DRIVERCLASSNAME);
        if (value != null) {
            dataSource.setDriverClassName(value);
        }

        value = (String) properties.get(PROP_MAXACTIVE);
        if (value != null) {
            dataSource.setMaxActive(Integer.parseInt(value));
        }

        value = (String) properties.get(PROP_MAXIDLE);
        if (value != null) {
            dataSource.setMaxIdle(Integer.parseInt(value));
        }

        value = (String) properties.get(PROP_MINIDLE);
        if (value != null) {
            dataSource.setMinIdle(Integer.parseInt(value));
        }

        value = (String) properties.get(PROP_INITIALSIZE);
        if (value != null) {
            dataSource.setInitialSize(Integer.parseInt(value));
        }

        value = (String) properties.get(PROP_MAXWAIT);
        if (value != null) {
            dataSource.setMaxWait(Long.parseLong(value));
        }

        value = (String) properties.get(PROP_TESTONBORROW);
        if (value != null) {
            dataSource.setTestOnBorrow(Boolean.valueOf(value).booleanValue());
        }

        value = (String) properties.get(PROP_TESTONRETURN);
        if (value != null) {
            dataSource.setTestOnReturn(Boolean.valueOf(value).booleanValue());
        }

        value = (String) properties.get(PROP_TIMEBETWEENEVICTIONRUNSMILLIS);
        if (value != null) {
            dataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(value));
        }

        value = (String) properties.get(PROP_NUMTESTSPEREVICTIONRUN);
        if (value != null) {
            dataSource.setNumTestsPerEvictionRun(Integer.parseInt(value));
        }

        value = (String) properties.get(PROP_MINEVICTABLEIDLETIMEMILLIS);
        if (value != null) {
            dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(value));
        }

        value = (String) properties.get(PROP_MAXEVICTABLEIDLETIMEMILLIS);
        if (value != null) {
            dataSource.setMaxEvictableIdleTimeMillis(Long.parseLong(value));
        }

        value = (String) properties.get(PROP_PHY_TIMEOUT_MILLIS);
        if (value != null) {
            dataSource.setPhyTimeoutMillis(Long.parseLong(value));
        }

        value = (String) properties.get(PROP_TESTWHILEIDLE);
        if (value != null) {
            dataSource.setTestWhileIdle(Boolean.valueOf(value).booleanValue());
        }

        value = (String) properties.get(PROP_PASSWORD);
        if (value != null) {
            dataSource.setPassword(value);
        }

        value = (String) properties.get(PROP_URL);
        if (value != null) {
            dataSource.setUrl(value);
        }

        value = (String) properties.get(PROP_USERNAME);
        if (value != null) {
            dataSource.setUsername(value);
        }

        value = (String) properties.get(PROP_VALIDATIONQUERY);
        if (value != null) {
            dataSource.setValidationQuery(value);
        }

        value = (String) properties.get(PROP_VALIDATIONQUERY_TIMEOUT);
        if (value != null) {
            dataSource.setValidationQueryTimeout(Integer.parseInt(value));
        }

        value = (String) properties.get(PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED);
        if (value != null) {
            dataSource.setAccessToUnderlyingConnectionAllowed(Boolean.valueOf(value).booleanValue());
        }

        value = (String) properties.get(PROP_REMOVEABANDONED);
        if (value != null) {
            dataSource.setRemoveAbandoned(Boolean.valueOf(value).booleanValue());
        }

        value = (String) properties.get(PROP_REMOVEABANDONEDTIMEOUT);
        if (value != null) {
            dataSource.setRemoveAbandonedTimeout(Integer.parseInt(value));
        }

        value = (String) properties.get(PROP_LOGABANDONED);
        if (value != null) {
            dataSource.setLogAbandoned(Boolean.valueOf(value).booleanValue());
        }

        value = (String) properties.get(PROP_POOLPREPAREDSTATEMENTS);
        if (value != null) {
            boolean poolPreparedStatements = Boolean.valueOf(value).booleanValue();
            dataSource.setPoolPreparedStatements(poolPreparedStatements);

            if (poolPreparedStatements) {
                value = (String) properties.get(PROP_MAXOPENPREPAREDSTATEMENTS);
                if (value != null) {
                    dataSource.setMaxOpenPreparedStatements(Integer.parseInt(value));
                }
            }
        }

        value = (String) properties.get(PROP_FILTERS);
        if (value != null) {
            dataSource.setFilters(value);
        }

        value = (String) properties.get(PROP_EXCEPTION_SORTER);
        if (value != null) {
            dataSource.setExceptionSorter(value);
        }

        value = (String) properties.get(PROP_EXCEPTION_SORTER_CLASS_NAME);
        if (value != null) {
            dataSource.setExceptionSorter(value);
        }

        value = (String) properties.get(PROP_INITCONNECTIONSQLS);
        if (value != null) {
            StringTokenizer tokenizer = new StringTokenizer(value, ";");
            dataSource.setConnectionInitSqls(Collections.list(tokenizer));
        }

        value = (String) properties.get(PROP_CONNECTIONPROPERTIES);
        if (value != null) {
            dataSource.setConnectionProperties(value);
        }
        value = (String) properties.get(PROP_SOCKET_TIMEOUT);
        if (value != null) {
            dataSource.setSocketTimeout(Integer.parseInt(value));
        }
        value = (String) properties.get(PROP_CONNECT_TIMEOUT);
        if (value != null) {
            dataSource.setConnectTimeout(Integer.parseInt(value));
        }

        {
            Properties dataSourceProperties = null;
            for (Map.Entry entry : properties.entrySet()) {
                String entryKey = (String) entry.getKey();
                if (entryKey.startsWith("druid.")) {
                    if (dataSourceProperties == null) {
                        dataSourceProperties = new Properties();
                    }

                    String entryValue = (String) entry.getValue();
                    dataSourceProperties.put(entryKey, entryValue);
                }
            }
            if (dataSourceProperties != null) {
                dataSource.configFromPropeties(dataSourceProperties);
            }
        }

        value = (String) properties.get(PROP_INIT);
        if ("true".equals(value)) {
            dataSource.init();
        }
    }
}
