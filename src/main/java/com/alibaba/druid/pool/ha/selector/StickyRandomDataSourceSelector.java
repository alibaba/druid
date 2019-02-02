package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import javax.sql.DataSource;

/**
 * An extend selector based on RandomDataSourceSelector which can stick a DataSource to a Thread in a while.
 *
 * @author DigitalSonic
 * @see RandomDataSourceSelector
 * @see StickyDataSourceHolder
 */
public class StickyRandomDataSourceSelector extends RandomDataSourceSelector {
    private final static Log LOG = LogFactory.getLog(StickyRandomDataSourceSelector.class);

    private ThreadLocal<StickyDataSourceHolder> holders = new ThreadLocal<StickyDataSourceHolder>();

    private int expireSeconds = 5;

    public StickyRandomDataSourceSelector(HighAvailableDataSource highAvailableDataSource) {
        super(highAvailableDataSource);
    }

    @Override
    public DataSource get() {
        StickyDataSourceHolder holder = holders.get();
        if (holder != null && isValid(holder) && !isExpired(holder)) {
            LOG.debug("Return the sticky DataSource " + holder.getDataSource().toString() + " directly.");
            return holder.getDataSource();
        }
        LOG.debug("Return a random DataSource.");
        DataSource dataSource = super.get();
        holder = new StickyDataSourceHolder(dataSource);
        holders.remove();
        holders.set(holder);
        return dataSource;
    }

    private boolean isValid(StickyDataSourceHolder holder) {
        boolean flag = holder.isValid() && !getBlacklist().contains(holder.getDataSource());
        if (!(holder.getDataSource() instanceof DruidDataSource) || !flag) {
            return flag;
        }
        DruidDataSource dataSource = (DruidDataSource) holder.getDataSource();
        return flag && dataSource.getActiveCount() < dataSource.getMaxActive();
    }

    private boolean isExpired(StickyDataSourceHolder holder) {
        return System.currentTimeMillis() - holder.getRetrievingTime() > expireSeconds * 1000;
    }

    public int getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
