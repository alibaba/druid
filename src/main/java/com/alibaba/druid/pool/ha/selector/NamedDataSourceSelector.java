package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.ha.HighAvailableDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Use the given name in ThreadLocal variable to choose DataSource.
 *
 * @author DigitalSonic
 */
public class NamedDataSourceSelector implements DataSourceSelector {
    public static final String DEFAULT_NAME = "default";
    private HighAvailableDataSource highAvailableDataSource;
    private ThreadLocal<String> targetDataSourceName = new ThreadLocal<String>();
    private String defaultName = DEFAULT_NAME;

    public NamedDataSourceSelector(HighAvailableDataSource highAvailableDataSource) {
        this.highAvailableDataSource = highAvailableDataSource;
    }

    @Override
    public boolean isSame(String name) {
        return "byName".equalsIgnoreCase(name);
    }

    @Override
    public DataSource get() {
        if (highAvailableDataSource == null) {
            return null;
        }

        Map<String, DataSource> dataSourceMap = highAvailableDataSource.getDataSourceMap();
        if (dataSourceMap == null || dataSourceMap.isEmpty()) {
            return null;
        }
        if (dataSourceMap.size() == 1) {
            for (DataSource v : dataSourceMap.values()) {
                return v;
            }
        }
        String name = getTarget();
        if (name == null) {
            if (dataSourceMap.get(getDefaultName()) != null) {
                return dataSourceMap.get(getDefaultName());
            }
        } else {
            return dataSourceMap.get(name);
        }
        return null;
    }

    @Override
    public void setTarget(String name) {
        targetDataSourceName.set(name);
    }

    public String getTarget() {
        return targetDataSourceName.get();
    }

    public void resetDataSourceName() {
        targetDataSourceName.remove();
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
}
