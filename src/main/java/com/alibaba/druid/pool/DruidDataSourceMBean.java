/**
 * Project: druid File Created at 2011-2-24 $Id$ Copyright 1999-2100 Alibaba.com Corporation Limited. All rights
 * reserved. This software is the confidential and proprietary information of Alibaba Company.
 * ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.druid.pool;

import java.util.List;

/**
 * @author shaojin.wensj
 */
public interface DruidDataSourceMBean {

    int getInitialSize();

    String getUsername();

    String getPassword();

    String getUrl();

    String getDriverClassName();

    long getConnectCount();

    long getCloseCount();

    long getConnectErrorCount();

    int getPoolingSize();

    long getRecycleCount();

    int getActiveCount();

    long getCreateCount();

    long getDestroyCount();

    long getCreateTimespanMillis();

    int getQueryTimeout();

    boolean isActiveConnectionTraceEnable();

    void setActiveConnectionTraceEnable(boolean connectStackTraceEnable);

    List<String> getActiveConnectionStackTrace();
}
