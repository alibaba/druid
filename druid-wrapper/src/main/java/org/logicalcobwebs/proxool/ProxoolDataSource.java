package org.logicalcobwebs.proxool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

public class ProxoolDataSource implements DataSource, ObjectFactory {

    private DruidDataSource druid              = new DruidDataSource();

    private Properties      delegateProperties = new Properties();

    public ProxoolDataSource(){

    }

    public ProxoolDataSource(String alias){
        druid.setName(alias);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return druid.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        druid.setLogWriter(out);
    }

    public int getLoginTimeout() throws SQLException {
        return druid.getLoginTimeout();
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        druid.setLoginTimeout(seconds);
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return druid.getParentLogger();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return druid.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return druid.isWrapperFor(iface);
    }

    @SuppressWarnings("rawtypes")
    public Object getObjectInstance(Object refObject, Name name, Context context, Hashtable hashtable) throws Exception {
        if (!(refObject instanceof Reference)) {
            return null;
        }
        Reference reference = (Reference) refObject;
        populatePropertiesFromReference(reference);
        return this;
    }

    public Connection getConnection() throws SQLException {
        return druid.getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return druid.getConnection(username, password);
    }

    public String getAlias() {
        return druid.getName();
    }

    public void setAlias(String alias) {
        druid.setName(alias);
    }

    public String getDriverUrl() {
        return druid.getUrl();
    }

    public void setDriverUrl(String url) {
        druid.setUrl(url);
    }

    public String getDriver() {
        return druid.getDriverClassName();
    }

    public void setDriver(String driver) {
        druid.setDriverClassName(driver);
    }

    public long getMaximumConnectionLifetime() {
        return Long.MAX_VALUE;
    }

    @Deprecated
    public void setMaximumConnectionLifetime(int maximumConnectionLifetime) {

    }

    @Deprecated
    public int getPrototypeCount() {
        return 0;
    }

    @Deprecated
    public void setPrototypeCount(int prototypeCount) {

    }

    public int getMinimumConnectionCount() {
        return druid.getMinIdle();
    }

    public void setMinimumConnectionCount(int minimumConnectionCount) {
        druid.setMinIdle(minimumConnectionCount);
    }

    public int getMaximumConnectionCount() {
        return druid.getMaxActive();
    }

    public void setMaximumConnectionCount(int maximumConnectionCount) {
        druid.setMaxActive(maximumConnectionCount);
    }

    public long getHouseKeepingSleepTime() {
        return druid.getTimeBetweenEvictionRunsMillis();
    }

    public void setHouseKeepingSleepTime(int houseKeepingSleepTime) {
        druid.setTimeBetweenEvictionRunsMillis(houseKeepingSleepTime);
    }

    public int getSimultaneousBuildThrottle() {
        return 0;
    }

    public void setSimultaneousBuildThrottle(int simultaneousBuildThrottle) {

    }

    public long getRecentlyStartedThreshold() {
        return 0;
    }

    public void setRecentlyStartedThreshold(int recentlyStartedThreshold) {

    }

    public long getOverloadWithoutRefusalLifetime() {
        return 0;
    }

    public void setOverloadWithoutRefusalLifetime(int overloadWithoutRefusalLifetime) {

    }

    public long getMaximumActiveTime() {
        return druid.getRemoveAbandonedTimeoutMillis();
    }

    public void setMaximumActiveTime(long maximumActiveTime) {
        druid.setRemoveAbandonedTimeoutMillis(maximumActiveTime);
    }

    public boolean isVerbose() {
        return false;
    }

    @Deprecated
    public void setVerbose(boolean verbose) {

    }

    public boolean isTrace() {
        return false;
    }

    @Deprecated
    public void setTrace(boolean trace) {
    }

    public String getStatistics() {
        return "";
    }

    public void setStatistics(String statistics) {
    }

    public String getStatisticsLogLevel() {
        return "";
    }

    public void setStatisticsLogLevel(String statisticsLogLevel) {
    }

    public String getFatalSqlExceptionsAsString() {
        return null;
    }

    @Deprecated
    public void setFatalSqlExceptionsAsString(String fatalSqlExceptionsAsString) {

    }

    public String getFatalSqlExceptionWrapperClass() {
        return null;
    }

    public void setFatalSqlExceptionWrapperClass(String fatalSqlExceptionWrapperClass) {

    }

    public String getHouseKeepingTestSql() {
        return druid.getValidationQuery();
    }

    public void setHouseKeepingTestSql(String houseKeepingTestSql) {
        druid.setValidationQuery(houseKeepingTestSql);
    }

    public String getUser() {
        return druid.getUsername();
    }

    public void setUser(String user) {
        druid.setUsername(user);
    }

    public String getPassword() {
        return druid.getPassword();
    }

    public void setPassword(String password) {
        druid.setPassword(password);
    }

    public boolean isJmx() {
        return true;
    }

    public void setJmx(boolean jmx) {

    }

    @Deprecated
    public String getJmxAgentId() {
        return "";
    }

    @Deprecated
    public void setJmxAgentId(String jmxAgentId) {

    }

    public boolean isTestBeforeUse() {
        return druid.isTestOnBorrow();
    }

    public void setTestBeforeUse(boolean testBeforeUse) {
        druid.setTestOnBorrow(testBeforeUse);
    }

    public boolean isTestAfterUse() {
        return druid.isTestOnReturn();
    }

    public void setTestAfterUse(boolean testAfterUse) {
        druid.setTestOnReturn(testAfterUse);
    }

    private void populatePropertiesFromReference(Reference reference) {
        RefAddr property = reference.get(ProxoolConstants.ALIAS_PROPERTY);
        if (property != null) {
            setAlias(property.getContent().toString());
        }
        property = reference.get(ProxoolConstants.DRIVER_CLASS_PROPERTY);
        if (property != null) {
            setDriver(property.getContent().toString());
        }
        property = reference.get(ProxoolConstants.FATAL_SQL_EXCEPTION_WRAPPER_CLASS_PROPERTY);
        if (property != null) {
            setFatalSqlExceptionWrapperClass(property.getContent().toString());
        }
        property = reference.get(ProxoolConstants.HOUSE_KEEPING_SLEEP_TIME_PROPERTY);
        if (property != null) {
            setHouseKeepingSleepTime(Integer.valueOf(property.getContent().toString()).intValue());
        }
        property = reference.get(ProxoolConstants.HOUSE_KEEPING_TEST_SQL_PROPERTY);
        if (property != null) {
            setHouseKeepingTestSql(property.getContent().toString());
        }
        property = reference.get(ProxoolConstants.MAXIMUM_CONNECTION_COUNT_PROPERTY);
        if (property != null) {
            setMaximumConnectionCount(Integer.valueOf(property.getContent().toString()).intValue());
        }
        property = reference.get(ProxoolConstants.MAXIMUM_CONNECTION_LIFETIME_PROPERTY);
        if (property != null) {
            setMaximumConnectionLifetime(Integer.valueOf(property.getContent().toString()).intValue());
        }
        property = reference.get(ProxoolConstants.MAXIMUM_ACTIVE_TIME_PROPERTY);
        if (property != null) {
            setMaximumActiveTime(Long.valueOf(property.getContent().toString()).intValue());
        }
        property = reference.get(ProxoolConstants.MINIMUM_CONNECTION_COUNT_PROPERTY);
        if (property != null) {
            setMinimumConnectionCount(Integer.valueOf(property.getContent().toString()).intValue());
        }
        property = reference.get(ProxoolConstants.OVERLOAD_WITHOUT_REFUSAL_LIFETIME_PROPERTY);
        if (property != null) {
            setOverloadWithoutRefusalLifetime(Integer.valueOf(property.getContent().toString()).intValue());
        }
        property = reference.get(ProxoolConstants.PASSWORD_PROPERTY);
        if (property != null) {
            setPassword(property.getContent().toString());
        }
        property = reference.get(ProxoolConstants.PROTOTYPE_COUNT_PROPERTY);
        if (property != null) {
            setPrototypeCount(Integer.valueOf(property.getContent().toString()).intValue());
        }
        property = reference.get(ProxoolConstants.RECENTLY_STARTED_THRESHOLD_PROPERTY);
        if (property != null) {
            setRecentlyStartedThreshold(Integer.valueOf(property.getContent().toString()).intValue());
        }
        property = reference.get(ProxoolConstants.SIMULTANEOUS_BUILD_THROTTLE_PROPERTY);
        if (property != null) {
            setSimultaneousBuildThrottle(Integer.valueOf(property.getContent().toString()).intValue());
        }
        property = reference.get(ProxoolConstants.STATISTICS_PROPERTY);
        if (property != null) {
            setStatistics(property.getContent().toString());
        }
        property = reference.get(ProxoolConstants.STATISTICS_LOG_LEVEL_PROPERTY);
        if (property != null) {
            setStatisticsLogLevel(property.getContent().toString());
        }
        property = reference.get(ProxoolConstants.TRACE_PROPERTY);
        if (property != null) {
            setTrace("true".equalsIgnoreCase(property.getContent().toString()));
        }
        property = reference.get(ProxoolConstants.DRIVER_URL_PROPERTY);
        if (property != null) {
            setDriverUrl(property.getContent().toString());
        }
        property = reference.get(ProxoolConstants.USER_PROPERTY);
        if (property != null) {
            setUser(property.getContent().toString());
        }
        property = reference.get(ProxoolConstants.VERBOSE_PROPERTY);
        if (property != null) {
            setVerbose("true".equalsIgnoreCase(property.getContent().toString()));
        }
        property = reference.get(ProxoolConstants.JMX_PROPERTY);
        if (property != null) {
            setJmx("true".equalsIgnoreCase(property.getContent().toString()));
        }
        property = reference.get(ProxoolConstants.JMX_AGENT_PROPERTY);
        if (property != null) {
            setJmxAgentId(property.getContent().toString());
        }
        property = reference.get(ProxoolConstants.TEST_BEFORE_USE_PROPERTY);
        if (property != null) {
            setTestBeforeUse("true".equalsIgnoreCase(property.getContent().toString()));
        }
        property = reference.get(ProxoolConstants.TEST_AFTER_USE_PROPERTY);
        if (property != null) {
            setTestAfterUse("true".equalsIgnoreCase(property.getContent().toString()));
        }
        // Pick up any properties that we don't recognise
        Enumeration<?> e = reference.getAll();
        while (e.hasMoreElements()) {
            StringRefAddr stringRefAddr = (StringRefAddr) e.nextElement();
            String name = stringRefAddr.getType();
            String content = stringRefAddr.getContent().toString();
            if (name.indexOf(ProxoolConstants.PROPERTY_PREFIX) != 0) {
                delegateProperties.put(name, content);
            }
        }
    }

    public void setDelegateProperties(String properties) {
        StringTokenizer stOuter = new StringTokenizer(properties, ",");
        while (stOuter.hasMoreTokens()) {
            StringTokenizer stInner = new StringTokenizer(stOuter.nextToken(), "=");
            if (stInner.countTokens() == 1) {
                // Allow blank string to be a valid value
                delegateProperties.put(stInner.nextToken().trim(), "");
            } else if (stInner.countTokens() == 2) {
                delegateProperties.put(stInner.nextToken().trim(), stInner.nextToken().trim());
            } else {
                throw new IllegalArgumentException("Unexpected delegateProperties value: '" + properties
                                                   + "'. Expected 'name=value'");
            }
        }
    }
}
