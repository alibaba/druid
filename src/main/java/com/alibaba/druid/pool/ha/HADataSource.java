package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sql.DataSource;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.balance.Balancer;
import com.alibaba.druid.pool.ha.balance.RoundRobinBlancer;
import com.alibaba.druid.util.JdbcUtils;

public class HADataSource extends MultiDataSource implements DataSource {

    private final static Log              LOG                     = LogFactory.getLog(HADataSource.class);

    protected DruidDataSource             master;
    protected DruidDataSource             slave;
    protected final List<DruidDataSource> notAvailableDatasources = new CopyOnWriteArrayList<DruidDataSource>();

    protected Balancer                    balancer                = new RoundRobinBlancer();

    private DataSourceList                dataSourceList          = new DataSourceList();

    public HADataSource(){

    }

    public DruidDataSource getMaster() {
        return master;
    }

    public void setMaster(DruidDataSource master) {
        this.master = master;
    }

    public DruidDataSource getSlave() {
        return slave;
    }

    public void setSlave(DruidDataSource slave) {
        this.slave = slave;
    }

    public List<DruidDataSource> getDataSources() {
        return dataSourceList;
    }

    public synchronized void setDataSources(List<DruidDataSource> dataSources) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new MultiDataSourceConnection(this, createConnectionId());
    }

    public Balancer getBalancer() {
        return balancer;
    }

    public void setBalancer(Balancer balancer) {
        this.balancer = balancer;
    }

    public Connection getConnectionInternal(MultiDataSourceConnection connection, String sql) throws SQLException {
        return this.balancer.getConnection(connection, sql);
    }

    public void handleNotAwailableDatasource(DruidDataSource dataSource) {
        throw new UnsupportedOperationException();
    }

    public void close() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("HADataSource closed");
        }
        
        JdbcUtils.close(master);
    }

    private class DataSourceList extends AbstractList<DruidDataSource> implements RandomAccess, java.io.Serializable {

        private static final long serialVersionUID = -2764017481108945198L;

        public int size() {
            return 2;
        }

        public Object[] toArray() {
            return new DruidDataSource[] { master, slave };
        }

        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        public DruidDataSource get(int index) {
            if (index == 0) {
                return master;
            }

            if (index == 1) {
                return slave;
            }

            throw new IllegalArgumentException("index : " + index);
        }

        public DruidDataSource set(int index, DruidDataSource element) {
            throw new UnsupportedOperationException();
        }

        public int indexOf(Object o) {
            if (o == master) {
                return 0;
            }

            if (o == slave) {
                return 1;
            }

            return -1;
        }

        public boolean contains(Object o) {
            return o == master || o == slave;
        }
    }
}
