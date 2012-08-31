/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.stat;

import java.lang.management.ManagementFactory;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.druid.VERSION;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.spring.stat.SpringStatManager;
import com.alibaba.druid.util.DruidDataSourceUtils;
import com.alibaba.druid.util.JdbcSqlStatUtils;
import com.alibaba.druid.util.StringUtils;

/**
 * 监控相关的对外数据暴露
 * 
 * <pre>
 * 1. 为了支持jndi数据源本类内部调用druid相关对象均需要反射调用,返回值也应该是Object,List<Object>,Map<String,Object>等无关于druid的类型
 * 2. 对外暴露的public方法都应该先调用init()，应该有更好的方式，暂时没想到
 * </pre>
 * 
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public final class DruidStatManagerFacade {

    private final static DruidStatManagerFacade instance    = new DruidStatManagerFacade();
    private boolean                             resetEnable = true;
    private final AtomicLong                    resetCount  = new AtomicLong();

    private DruidStatManagerFacade(){
    }

    public static DruidStatManagerFacade getInstance() {
        return instance;
    }

    private Set<Object> getDruidDataSourceInstances() {
        return DruidDataSourceStatManager.getInstances().keySet();
    }
    
    public Object getDruidDataSourceByName(String name) {
        for (Object o : this.getDruidDataSourceInstances()) {
            String itemName = DruidDataSourceUtils.getName(o);
            if (StringUtils.equals(name, itemName)) {
                return o;
            }
        }
        
        return null;
    }

    public void resetDataSourceStat() {
        DruidDataSourceStatManager.getInstance().reset();
    }

    public void resetSqlStat() {
        JdbcStatManager.getInstance().reset();
    }

    public void resetAll() {
        if (!isResetEnable()) {
            return;
        }
        
        SpringStatManager.getInstance().resetStat();
        WebAppStatManager.getInstance().resetStat();
        resetSqlStat();
        resetDataSourceStat();
        resetCount.incrementAndGet();
    }

    public boolean isResetEnable() {
        return resetEnable;
    }

    public void setResetEnable(boolean resetEnable) {
        this.resetEnable = resetEnable;
    }

    public Object getSqlStatById(Integer id) {
        for (Object ds : getDruidDataSourceInstances()) {
            Object sqlStat = DruidDataSourceUtils.getSqlStat(ds, id);
            if (sqlStat != null) {
                return sqlStat;
            }
        }
        return null;
    }

    public Map<String, Object> getDataSourceStatData(Integer id) {
        if (id == null) {
            return null;
        }

        Object datasource = getDruidDataSourceById(id);
        return datasource == null ? null : dataSourceToMapData(datasource);
    }

    public Object getDruidDataSourceById(Integer identity) {
        if (identity == null) {
            return null;
        }

        for (Object datasource : getDruidDataSourceInstances()) {
            if (System.identityHashCode(datasource) == identity) {
                return datasource;
            }
        }
        return null;
    }

    public List<Map<String, Object>> getSqlStatDataList(Integer dataSourceId) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Object datasource : getDruidDataSourceInstances()) {
            if (dataSourceId != null && dataSourceId.intValue() != System.identityHashCode(datasource)) {
                continue;
            }
            
            for (Object sqlStat : DruidDataSourceUtils.getSqlStatMap(datasource).values()) {

                Map<String, Object> data = JdbcSqlStatUtils.getData(sqlStat);

                long executeCount = (Long) data.get("ExecuteCount");
                long runningCount = (Long) data.get("RunningCount");

                if (executeCount == 0 && runningCount == 0) {
                    continue;
                }

                result.add(data);
            }
        }
        return result;
    }

    public Map<String, Object> getSqlStatData(Integer id) {
        if (id == null) {
            return null;
        }

        Object sqlStat = getSqlStatById(id);

        if (sqlStat == null) {
            return null;
        }

        return JdbcSqlStatUtils.getData(sqlStat);
    }

    public List<Map<String, Object>> getDataSourceStatDataList() {
        List<Map<String, Object>> datasourceList = new ArrayList<Map<String, Object>>();
        for (Object dataSource : getDruidDataSourceInstances()) {
            datasourceList.add(dataSourceToMapData(dataSource));
        }
        return datasourceList;
    }

    public List<List<String>> getActiveConnStackTraceList() {
        List<List<String>> traceList = new ArrayList<List<String>>();
        for (Object dataSource : getDruidDataSourceInstances()) {
            List<String> stacks = ((DruidDataSource) dataSource).getActiveConnectionStackTrace();
			if (stacks.size() > 0 ) {
				traceList.add(stacks);
			}
        }
        return traceList;
    }

    public Map<String, Object> returnJSONBasicStat() {
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("Version", VERSION.getVersionNumber());
        dataMap.put("Drivers", getDriversData());
        dataMap.put("ResetEnable", isResetEnable());
        dataMap.put("ResetCount", getResetCount());
        dataMap.put("JavaVMName", System.getProperty("java.vm.name"));
        dataMap.put("JavaVersion", System.getProperty("java.version"));
        dataMap.put("JavaClassPath", System.getProperty("java.class.path"));
        dataMap.put("StartTime", new Date(ManagementFactory.getRuntimeMXBean().getStartTime()));
        return dataMap;
    }

    public long getResetCount() {
        return resetCount.get();
    }

    private List<String> getDriversData() {
        List<String> drivers = new ArrayList<String>();
        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
            Driver driver = e.nextElement();
            drivers.add(driver.getClass().getName());
        }
        return drivers;
    }

    public List<Map<String, Object>> getPoolingConnectionInfoByDataSourceId(Integer id) {
        Object datasource = getDruidDataSourceById(id);

        if (datasource == null) {
            return null;
        }

        return DruidDataSourceUtils.getPoolingConnectionInfo(datasource);
    }

    public List<String> getActiveConnectionStackTraceByDataSourceId(Integer id) {
        Object datasource = getDruidDataSourceById(id);

        if (datasource == null || !DruidDataSourceUtils.isRemoveAbandoned(datasource)) {
            return null;
        }

        return DruidDataSourceUtils.getActiveConnectionStackTrace(datasource);
    }

    private Map<String, Object> dataSourceToMapData(Object dataSource) {

        return DruidDataSourceUtils.getStatData(dataSource);
    }
}
