/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

public interface StatFilterContextListener {

    void addUpdateCount(int updateCount);

    void addFetchRowCount(int fetchRowCount);

    void executeBefore(String sql, boolean inTransaction);

    void executeAfter(String sql, long nanoSpan, Throwable error);

    void commit();

    void rollback();
    
    void pool_connect();
    
    void pool_close(long nanos);
    
    void physical_connection_connect();

    void physical_connection_close(long nanos);
    
    void resultSet_open();
    
    void resultSet_close(long nanos);
    
    void clob_open();
    
    void blob_open();
}
