package com.alibaba.druid.stat;

import javax.management.JMException;
import javax.management.openmbean.TabularData;

public interface DruidDataSourceStatManagerMBean {

    void reset();

    TabularData getDataSourceList() throws JMException;
}
