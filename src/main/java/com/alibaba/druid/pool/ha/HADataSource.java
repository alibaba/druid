package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sql.DataSource;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.balance.Balancer;
import com.alibaba.druid.pool.ha.balance.RoundRobinBlancer;

public class HADataSource extends MultiDataSource implements DataSource {

    private final static Log              LOG                     = LogFactory.getLog(HADataSource.class);

    protected DruidDataSource             master;
    protected ArrayList<DruidDataSource>  dataSources             = new ArrayList<DruidDataSource>();
    protected final List<DruidDataSource> notAvailableDatasources = new CopyOnWriteArrayList<DruidDataSource>();

    protected Balancer                    balancer                = new RoundRobinBlancer();

    public HADataSource(){

    }

    public DruidDataSource getMaster() {
        return master;
    }

    public void setMaster(DruidDataSource master) {
        this.master = master;
    }

    public List<DruidDataSource> getDataSources() {
        return Collections.unmodifiableList(dataSources);
    }

    public synchronized void setDataSources(List<DruidDataSource> dataSources) {
        this.dataSources = new ArrayList<DruidDataSource>(dataSources);
    }

    public synchronized void addDataSource(DruidDataSource dataSource) {
        ArrayList<DruidDataSource> newList = new ArrayList<DruidDataSource>(dataSources);
        newList.add(dataSource);
        this.dataSources = newList;
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
        boolean removed = dataSources.remove(dataSource);
        if (removed) {
            notAvailableDatasources.add(dataSource);
        }
    }

    public void close() {
        for (DruidDataSource item : dataSources) {
            try {
                item.close();
            } catch (Exception ex) {
                LOG.error("close dataSource error", ex);
            }
        }
        for (DruidDataSource item : notAvailableDatasources) {
            try {
                item.close();
            } catch (Exception ex) {
                LOG.error("close dataSource error", ex);
            }
        }
    }

}
