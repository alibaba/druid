package com.alibaba.druid.pool.ha;

import com.alibaba.druid.pool.ValidConnectionChecker;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class CounterValidConnectionChecker implements ValidConnectionChecker {
    private int countBeforeFailure = 100;
    private Map<String, Integer> counterMap = new ConcurrentHashMap<String, Integer>();

    @Override
    public boolean isValidConnection(Connection c, String query, int validationQueryTimeout) throws Exception {
        String url = c.getMetaData().getURL();
        if (!counterMap.containsKey(url)) {
            counterMap.put(url, 0);
        }
        Integer counter = counterMap.get(url);
        counterMap.put(url, ++counter);
        if (counter >= countBeforeFailure) {
            return false;
        }

        return true;
    }

    @Override
    public void configFromProperties(Properties properties) {

    }

    public int getCountValue(String url) {
        return counterMap.containsKey(url) ? counterMap.get(url) : 0;
    }

    public int getCountBeforeFailure() {
        return countBeforeFailure;
    }

    public void setCountBeforeFailure(int countBeforeFailure) {
        this.countBeforeFailure = countBeforeFailure;
    }
}
