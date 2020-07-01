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
package com.alibaba.druid.filter.stat;

public class StatFilterContextListenerAdapter implements StatFilterContextListener {

    @Override
    public void addUpdateCount(int updateCount) {
        
    }

    @Override
    public void addFetchRowCount(int fetchRowCount) {
        
    }

    @Override
    public void executeBefore(String sql, boolean inTransaction) {
        
    }

    @Override
    public void executeAfter(String sql, long nanoSpan, Throwable error) {
        
    }

    @Override
    public void commit() {
        
    }

    @Override
    public void rollback() {
        
    }

    @Override
    public void pool_connect() {
        
    }

    @Override
    public void pool_close(long nanos) {
        
    }

    @Override
    public void physical_connection_connect() {
        
    }

    @Override
    public void physical_connection_close(long nanos) {
        
    }

    @Override
    public void resultSet_open() {
        
    }

    @Override
    public void resultSet_close(long nanos) {
        
    }

    @Override
    public void clob_open() {
        
    }
    
    @Override
    public void blob_open() {
        
    }


    
}
