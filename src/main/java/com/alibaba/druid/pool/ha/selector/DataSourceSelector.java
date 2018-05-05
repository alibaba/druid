package com.alibaba.druid.pool.ha.selector;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Interface for those selector to implement.
 * e.g. Random and Named
 *
 * @author DigitalSonic
 */
public interface DataSourceSelector {
    DataSource get();
    void setTarget(String name);
    boolean isSame(String name);
}
