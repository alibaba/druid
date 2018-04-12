package com.alibaba.druid.filter.url;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * A thread to run the validation on each host:port.
 * If some host:port is failed in the validation, we can set test flags in DruidDataSource.
 *
 * @author DigitalSonic
 */
public class HostAndPortValidatorThread implements Runnable {
    private final static Log LOG = LogFactory.getLog(HostAndPortValidatorThread.class);
    private DruidDataSource dataSource;
    private int sleepSeconds = 30;
    private boolean modifyTestFlags = false;
    private boolean oldTestOnBorrow = false;
    private boolean oldTestOnReturn = false;

    @Override
    public void run() {
        oldTestOnBorrow = dataSource.isTestOnBorrow();
        oldTestOnReturn = dataSource.isTestOnReturn();
        while (true) {
            try {
                check();
            } catch(Exception e) {
                LOG.error("Validation Failed!", e);
            } finally {
                try {
                    Thread.sleep(sleepSeconds * 1000);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    protected void check() {
        String url = dataSource.getRawJdbcUrl();
        if (url == null || (url != null &&
                (url.indexOf("${") == -1 || url.indexOf("}") == -1))) {
            return;
        }

        String placeHolder = url.substring(url.indexOf("${") + 2, url.indexOf("}"));
        List<String> values = HostAndPortHolder.getInstance().getAll(placeHolder);
        for (String v : values) {
            validateConnection(dataSource, placeHolder, v);
        }
        if (HostAndPortHolder.getInstance().getBlacklistSize() == 0) {
            resetFlagsIfNeeded();
        }
    }

    private void validateConnection(DruidDataSource dataSource, String placeHolder, String target) {
        Driver driver = dataSource.getRawDriver();
        Properties info = dataSource.getConnectProperties();
        String url = dataSource.getRawJdbcUrl();
        Connection conn = null;

        try {
            LOG.debug("Validating " + target + " every " + sleepSeconds + " seconds.");
            String realUrl = url.replace("${" + placeHolder + "}", target);
            conn = driver.connect(realUrl, info);
            dataSource.validateConnection(conn);
            HostAndPortHolder.getInstance().removeBlacklist(target);
        } catch (SQLException e) {
            LOG.warn("Validation FAILED for " + target, e);
            HostAndPortHolder.getInstance().addBlacklist(target);
            modifyFlagsIfNeeded();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.error("Can not close connection for HostAndPort Validation.", e);
                }
            }
        }
    }

    private void resetFlagsIfNeeded() {
        if (modifyTestFlags) {
            dataSource.setTestOnBorrow(oldTestOnBorrow);
            dataSource.setTestOnReturn(oldTestOnReturn);
        }
    }

    private void modifyFlagsIfNeeded() {
        if (!modifyTestFlags) {
            return;
        }
        if (!oldTestOnReturn || !oldTestOnBorrow) {
            dataSource.setTestOnBorrow(true);
            dataSource.setTestOnReturn(true);
        }
    }

    public void setDataSource(DruidDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setSleepSeconds(int sleepSeconds) {
        this.sleepSeconds = sleepSeconds;
    }

    public void setModifyTestFlags(boolean modifyTestFlags) {
        this.modifyTestFlags = modifyTestFlags;
    }
}
