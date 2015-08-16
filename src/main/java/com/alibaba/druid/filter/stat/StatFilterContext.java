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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatFilterContext {

    private List<StatFilterContextListener> listeners = new CopyOnWriteArrayList<StatFilterContextListener>();

    private static final StatFilterContext  instance  = new StatFilterContext();

    public final static StatFilterContext getInstance() {
        return instance;
    }

    public void addContextListener(StatFilterContextListener listener) {
        this.listeners.add(listener);
    }

    public boolean removeContextListener(StatFilterContextListener listener) {
        return listeners.remove(listener);
    }

    public List<StatFilterContextListener> getListeners() {
        return listeners;
    }

    public void addUpdateCount(int updateCount) {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.addUpdateCount(updateCount);
        }
    }

    public void addFetchRowCount(int fetchRowCount) {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.addFetchRowCount(fetchRowCount);
        }
    }

    public void executeBefore(String sql, boolean inTransaction) {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.executeBefore(sql, inTransaction);
        }
    }

    public void executeAfter(String sql, long nanoSpan, Throwable error) {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.executeAfter(sql, nanoSpan, error);
        }
    }

    public void commit() {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.commit();
        }
    }

    public void rollback() {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.rollback();
        }
    }

    public void pool_connection_open() {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.pool_connect();
        }
    }

    public void pool_connection_close(long nanos) {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.pool_close(nanos);
        }
    }

    public void physical_connection_connect() {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.physical_connection_connect();
        }
    }

    public void physical_connection_close(long nanos) {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.physical_connection_close(nanos);
        }
    }

    public void resultSet_open() {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.resultSet_open();
        }
    }

    public void resultSet_close(long nanos) {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.resultSet_close(nanos);
        }
    }

    public void clob_open() {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.clob_open();
        }
    }
    
    public void blob_open() {
        for (int i = 0; i < listeners.size(); ++i) {
            StatFilterContextListener listener = listeners.get(i);
            listener.blob_open();
        }
    }

}
