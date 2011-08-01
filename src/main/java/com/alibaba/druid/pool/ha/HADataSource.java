package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.balance.Balancer;
import com.alibaba.druid.pool.ha.balance.RoundRobinBlancer;

public class HADataSource extends MultiDataSource implements DataSource {

    protected ArrayList<DruidDataSource>  dataSources             = new ArrayList<DruidDataSource>();
    protected final List<DruidDataSource> notAvailableDatasources = new CopyOnWriteArrayList<DruidDataSource>();

    protected Balancer                    balancer                = new RoundRobinBlancer();

    public HADataSource(){

    }

    public List<DruidDataSource> getDataSources() {
        return Collections.unmodifiableList(dataSources);
    }

    public void setDataSources(List<DruidDataSource> dataSources) {
        this.dataSources = new ArrayList<DruidDataSource>(dataSources);
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

}
