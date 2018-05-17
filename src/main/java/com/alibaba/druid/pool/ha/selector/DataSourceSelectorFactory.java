package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.ha.HighAvailableDataSource;

/**
 * A Factory pattern for DataSourceSelector.
 *
 * @author DigitalSonic
 */
public class DataSourceSelectorFactory {
    public static DataSourceSelector getSelector(String name, HighAvailableDataSource highAvailableDataSource) {
        if ("random".equalsIgnoreCase(name)) {
            return new RandomDataSourceSelector(highAvailableDataSource);
        } else if ("byName".equalsIgnoreCase(name)) {
            return new NamedDataSourceSelector(highAvailableDataSource);
        }
        return null;
    }
}
