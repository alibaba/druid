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
package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class DruidDataSourceFactory implements ObjectFactory {

    private final static Log      LOG                                      = LogFactory.getLog(DruidDataSourceFactory.class);

    static final int              UNKNOWN_TRANSACTIONISOLATION             = -1;

    public final static String    PROP_DEFAULTAUTOCOMMIT                   = "defaultAutoCommit";
    public final static String    PROP_DEFAULTREADONLY                     = "defaultReadOnly";
    public final static String    PROP_DEFAULTTRANSACTIONISOLATION         = "defaultTransactionIsolation";
    public final static String    PROP_DEFAULTCATALOG                      = "defaultCatalog";
    public final static String    PROP_DRIVERCLASSNAME                     = "driverClassName";
    public final static String    PROP_MAXACTIVE                           = "maxActive";
    public final static String    PROP_MAXIDLE                             = "maxIdle";
    public final static String    PROP_MINIDLE                             = "minIdle";
    public final static String    PROP_INITIALSIZE                         = "initialSize";
    public final static String    PROP_MAXWAIT                             = "maxWait";
    public final static String    PROP_TESTONBORROW                        = "testOnBorrow";
    public final static String    PROP_TESTONRETURN                        = "testOnReturn";
    public final static String    PROP_TIMEBETWEENEVICTIONRUNSMILLIS       = "timeBetweenEvictionRunsMillis";
    public final static String    PROP_NUMTESTSPEREVICTIONRUN              = "numTestsPerEvictionRun";
    public final static String    PROP_MINEVICTABLEIDLETIMEMILLIS          = "minEvictableIdleTimeMillis";
    public final static String    PROP_PHY_TIMEOUT_MILLIS                  = "phyTimeoutMillis";
    public final static String    PROP_TESTWHILEIDLE                       = "testWhileIdle";
    public final static String    PROP_PASSWORD                            = "password";
    public final static String    PROP_URL                                 = "url";
    public final static String    PROP_USERNAME                            = "username";
    public final static String    PROP_VALIDATIONQUERY                     = "validationQuery";
    public final static String    PROP_VALIDATIONQUERY_TIMEOUT             = "validationQueryTimeout";

    public final static String    PROP_INITCONNECTIONSQLS                  = "initConnectionSqls";
    public final static String    PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED = "accessToUnderlyingConnectionAllowed";
    public final static String    PROP_REMOVEABANDONED                     = "removeAbandoned";
    public final static String    PROP_REMOVEABANDONEDTIMEOUT              = "removeAbandonedTimeout";
    public final static String    PROP_LOGABANDONED                        = "logAbandoned";
    public final static String    PROP_POOLPREPAREDSTATEMENTS              = "poolPreparedStatements";
    public final static String    PROP_MAXOPENPREPAREDSTATEMENTS           = "maxOpenPreparedStatements";
    public final static String    PROP_CONNECTIONPROPERTIES                = "connectionProperties";
    public final static String    PROP_FILTERS                             = "filters";
    public final static String    PROP_EXCEPTION_SORTER                    = "exceptionSorter";
    public final static String    PROP_EXCEPTION_SORTER_CLASS_NAME         = "exception-sorter-class-name";

    public final static String    PROP_INIT                                = "init";

    private final static String[] ALL_PROPERTIES                           = { PROP_DEFAULTAUTOCOMMIT,
            PROP_DEFAULTREADONLY, PROP_DEFAULTTRANSACTIONISOLATION, PROP_DEFAULTCATALOG, PROP_DRIVERCLASSNAME,
            PROP_MAXACTIVE, PROP_MAXIDLE, PROP_MINIDLE, PROP_INITIALSIZE, PROP_MAXWAIT, PROP_TESTONBORROW,
            PROP_TESTONRETURN, PROP_TIMEBETWEENEVICTIONRUNSMILLIS, PROP_NUMTESTSPEREVICTIONRUN,
            PROP_MINEVICTABLEIDLETIMEMILLIS, PROP_TESTWHILEIDLE, PROP_PASSWORD, PROP_URL, PROP_USERNAME,
            PROP_VALIDATIONQUERY, PROP_VALIDATIONQUERY_TIMEOUT, PROP_INITCONNECTIONSQLS,
            PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED, PROP_REMOVEABANDONED, PROP_REMOVEABANDONEDTIMEOUT,
            PROP_LOGABANDONED, PROP_POOLPREPAREDSTATEMENTS, PROP_MAXOPENPREPAREDSTATEMENTS, PROP_CONNECTIONPROPERTIES,
            PROP_FILTERS, PROP_EXCEPTION_SORTER, PROP_EXCEPTION_SORTER_CLASS_NAME, PROP_INIT };

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

    @SuppressWarnings({ "deprecation", "rawtypes" })
    public static void config(DruidDataSource dataSource, Map properties) throws SQLException {
        String value = null;

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

        value = (String) properties.get(PROP_INIT);
        if ("true".equals(value)) {
            dataSource.init();
        }
    }
}
