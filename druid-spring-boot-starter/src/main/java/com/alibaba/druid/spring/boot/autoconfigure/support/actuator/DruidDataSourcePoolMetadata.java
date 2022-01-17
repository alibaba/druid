/*
 * Copyright 2022 Alibaba Group Holding Ltd.
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
// Created on 2022年1月14日
// $Id$
package com.alibaba.druid.spring.boot.autoconfigure.support.actuator;

import org.springframework.boot.autoconfigure.jdbc.metadata.AbstractDataSourcePoolMetadata;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author zero3h
 */
public class DruidDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<DruidDataSource> {

    /**
     * @param dataSource
     */
    public DruidDataSourcePoolMetadata(DruidDataSource dataSource){
        super(dataSource);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata#getActive()
     */
    @Override
    public Integer getActive() {
        return getDataSource().getActiveCount();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata#getMax()
     */
    @Override
    public Integer getMax() {
        return getDataSource().getMaxActive();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata#getMin()
     */
    @Override
    public Integer getMin() {
        return getDataSource().getMinIdle();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata#getValidationQuery()
     */
    @Override
    public String getValidationQuery() {
        return getDataSource().getValidationQuery();
    }

    /**
     * 兼容spring boot 2.0后新增的方法
     * 
     * @return
     */
    public Boolean getDefaultAutoCommit() {
        return getDataSource().isDefaultAutoCommit();
    }

}
