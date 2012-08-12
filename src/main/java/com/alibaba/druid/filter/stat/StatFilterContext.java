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

    }

    public void rollback() {

    }
}
