package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * An enum holding the names and classes of DataSourceSelector.
 *
 * @author DigitalSonic
 */
public enum DataSourceSelectorEnum {
    BY_NAME("byName", NamedDataSourceSelector.class),
    RANDOM("random", RandomDataSourceSelector.class),
    STICKY_RANDOM("stickyRandom", StickyRandomDataSourceSelector.class);

    private final static Log LOG = LogFactory.getLog(DataSourceSelectorEnum.class);
    private String name;
    private Class<? extends DataSourceSelector> clazz;

    DataSourceSelectorEnum(String name, Class<? extends DataSourceSelector> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    /**
     * Create a new instance of the DataSourceSelector represented by this enum.
     *
     * @return null if dataSource is not given or exception occurred while creating new instance
     */
    public DataSourceSelector newInstance(HighAvailableDataSource dataSource) {
        if (dataSource == null) {
            LOG.warn("You should provide an instance of HighAvailableDataSource!");
            return null;
        }

        DataSourceSelector selector = null;
        try {
            selector = clazz.getDeclaredConstructor(HighAvailableDataSource.class).newInstance(dataSource);
        } catch (Exception e) {
            LOG.error("Can not create new instance of " + clazz.getName(), e);
        }
        return selector;
    }

    public String getName() {
        return name;
    }

    public Class<? extends DataSourceSelector> getClazz() {
        return clazz;
    }
}
