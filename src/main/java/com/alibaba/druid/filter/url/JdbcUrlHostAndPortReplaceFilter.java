package com.alibaba.druid.filter.url;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.util.Properties;

/**
 * The filter to replace the placeholder in JDBC URL.
 *
 * @author DigitalSonic
 */
public class JdbcUrlHostAndPortReplaceFilter extends FilterAdapter {
    private final static Log LOG = LogFactory.getLog(HostAndPortValidatorThread.class);

    public static final String DEFAULT_FILE = "datasource.properties";

    private String file = DEFAULT_FILE;

    private int healthCheckIntervalSeconds = 0;
    private HostAndPortValidatorThread validatorThread;
    private Thread runningValidatorThread;
    private boolean isValidatorThreadStarted = false;
    private boolean modifyTestFlags = false;

    public void init() {
        if (file == null || file.trim().length() == 0) {
            file = DEFAULT_FILE;
        }
        HostAndPortHolder.loadProperties(file);
    }

    public void close() {
        if (runningValidatorThread != null) {
            runningValidatorThread.interrupt();
        }
    }

    @Override
    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        startValidationThreadIfNeeded(chain);
        return super.connection_connect(new ConnectionConnectFilterChainImpl(chain), info);
    }

    private void startValidationThreadIfNeeded(FilterChain chain) {
        if (isValidatorThreadStarted || healthCheckIntervalSeconds <= 0) {
            return;
        }
        DataSourceProxy dataSource = chain.getDataSource();
        if (dataSource instanceof DruidDataSource) {
            if (validatorThread == null) {
                validatorThread = new HostAndPortValidatorThread();
                validatorThread.setSleepSeconds(healthCheckIntervalSeconds);
                validatorThread.setModifyTestFlags(modifyTestFlags);
            }
            validatorThread.setDataSource((DruidDataSource) dataSource);
            runningValidatorThread = new Thread(validatorThread);
            runningValidatorThread.start();
            isValidatorThreadStarted = true;
        } else {
            LOG.warn("The DataSource is not a DruidDataSource, ignore HostAndPortValidator.");
        }
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setHealthCheckIntervalSeconds(int healthCheckIntervalSeconds) {
        this.healthCheckIntervalSeconds = healthCheckIntervalSeconds;
    }

    public void setValidatorThread(HostAndPortValidatorThread validatorThread) {
        this.validatorThread = validatorThread;
    }

    public void setModifyTestFlags(boolean modifyTestFlags) {
        this.modifyTestFlags = modifyTestFlags;
    }
}
