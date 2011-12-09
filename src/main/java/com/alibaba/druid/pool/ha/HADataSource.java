package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.balance.Balancer;
import com.alibaba.druid.pool.ha.balance.RoundRobinBlancer;

public class HADataSource extends MultiDataSource implements DataSource {

    private final static Log  LOG      = LogFactory.getLog(HADataSource.class);

    protected DruidDataSource master;
    protected DruidDataSource slave;

    protected Balancer        balancer = new RoundRobinBlancer();

    public HADataSource(){

    }

    public DruidDataSource getMaster() {
        return master;
    }

    public void setMaster(DruidDataSource master) {
        this.getDataSources().put("master", master);
        this.master = master;
    }

    public DruidDataSource getSlave() {
        return slave;
    }

    public void setSlave(DruidDataSource slave) {
        this.getDataSources().put("slave", slave);
        this.slave = slave;
    }

    public synchronized void setDataSources(List<DruidDataSource> dataSources) {
        throw new UnsupportedOperationException();
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
        super.close();
        if (LOG.isDebugEnabled()) {
            LOG.debug("HADataSource closed");
        }
    }

}
