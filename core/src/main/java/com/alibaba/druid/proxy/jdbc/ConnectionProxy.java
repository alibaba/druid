/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.proxy.jdbc;

import java.sql.Connection;
import java.util.Date;
import java.util.Properties;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public interface ConnectionProxy extends Connection, WrapperProxy {
    Connection getRawObject();

    Properties getProperties();

    DataSourceProxy getDirectDataSource();

    /**
     * 获取实际创建物理连接的时间
     * @return
     */
    Date getConnectedTime();

    TransactionInfo getTransactionInfo();

    /**
     * 获取连接被执行关闭的次数，正常不能大于1
     * @return
     */
    int getCloseCount();

    /**
     * 获取创建物理连接的触发调用的方法,主要是为了排查连接使用的疑难问题
     * @return
     */
    String getCallMethodForConnect();

    /**
     * 获取关闭物理连接的触发调用的方法，主要是为了排查连接使用的疑难问题
     * @return
     */
    String getCallMethodForClose();

    /**
     * 获取物理连接实际从连接池中借出去使用过的次数
     * @return
     */
    int getUsedCount();

    /**
     * 获取物理连接最近一次从连接池中借出去的时间
     * @return
     */
    long getLastBorrowFromPoolTimeMs();

    /**
     * 获取物理连接最近一次被归还到连接池的时间
     * @return
     */
    long getLastReturnToPoolTimeMs();

    /**
     * 获取物理连接最近一次被执行校验的时间
     * @return
     */
    long getLastRunValidateTimeMs();


}
