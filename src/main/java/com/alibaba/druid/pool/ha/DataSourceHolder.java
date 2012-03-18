package com.alibaba.druid.pool.ha;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceHolder implements Closeable {

    private final MultiDataSource multiDataSource;
    private final DruidDataSource dataSource;
    private boolean               enable            = true;
    private final AtomicLong      connectCount      = new AtomicLong();
    private final AtomicLong      connectErrorCount = new AtomicLong();

    private boolean               fail              = false;

    private int                   weight            = 1;

    private transient int         weightRegionBegin;

    private transient int         weightRegionEnd;

    private boolean               writable          = true;

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public int getWeightRegionBegin() {
        return weightRegionBegin;
    }

    public void setWeightRegionBegin(int weightRegionBegin) {
        this.weightRegionBegin = weightRegionBegin;
    }

    public int getWeightRegionEnd() {
        return weightRegionEnd;
    }

    public void setWeightRegionEnd(int weightReginEnd) {
        this.weightRegionEnd = weightReginEnd;
    }

    public DataSourceHolder(MultiDataSource multiDataSource, DruidDataSource dataSource){
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource is null");
        }
        this.multiDataSource = multiDataSource;
        this.dataSource = dataSource;
    }

    public void resetState() {
        connectCount.set(0);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isEnable() {
        return enable && (!fail);
    }

    public void setEnable(boolean enable) {
        if (this.enable != enable) {
            this.enable = enable;

            multiDataSource.afterDataSourceChanged(null);
        }
    }

    public boolean isFail() {
        return fail;
    }

    public void setFail(boolean fail) {
        if (this.fail != fail) {
            this.fail = fail;
            multiDataSource.afterDataSourceChanged(null);
        }
    }

    public DruidDataSource getDataSource() {
        return dataSource;
    }

    public long getConnectCount() {
        return connectCount.get();
    }

    public void incrementConnectCount() {
        connectCount.incrementAndGet();
    }

    public String getUrl() {
        return dataSource.getUrl();
    }

    public MultiConnectionHolder getConnection() throws SQLException {
        connectCount.incrementAndGet();
        try {
            Connection conn = dataSource.getConnection();

            return new MultiConnectionHolder(this, conn);
        } catch (SQLException ex) {
            connectErrorCount.incrementAndGet();
            throw ex;
        } catch (RuntimeException ex) {
            connectErrorCount.incrementAndGet();
            throw ex;
        }
    }

    @Override
    public void close() {
        dataSource.close();
    }

    public void restart() {
        dataSource.restart();
    }
}
