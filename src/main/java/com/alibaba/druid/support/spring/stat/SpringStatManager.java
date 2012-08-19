package com.alibaba.druid.support.spring.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class SpringStatManager {

    public final static String             SYS_PROP_INSTANCES = "druid.spring.springStat";

    private final static SpringStatManager instance           = new SpringStatManager();

    private Set<Object>                    springStatSet      = null;

    public static SpringStatManager getInstance() {
        return instance;
    }

    public Set<Object> getSpringStatSet() {
        if (springStatSet == null) {
            springStatSet = getSpringStatSetFromSysProperty();
        }

        return springStatSet;
    }

    public void addSpringStat(Object springStat) {
        getSpringStatSet().add(springStat);
    }

    @SuppressWarnings("unchecked")
    static Set<Object> getSpringStatSetFromSysProperty() {
        Properties properties = System.getProperties();
        Set<Object> webAppStats = (Set<Object>) properties.get(SYS_PROP_INSTANCES);

        if (webAppStats == null) {
            synchronized (properties) {
                webAppStats = (Set<Object>) properties.get(SYS_PROP_INSTANCES);

                if (webAppStats == null) {
                    webAppStats = new CopyOnWriteArraySet<Object>();
                    properties.put(SYS_PROP_INSTANCES, webAppStats);
                }
            }
        }

        return webAppStats;
    }

    public List<Map<String, Object>> getMethodStatData() {
        Set<Object> stats = getSpringStatSet();

        List<Map<String, Object>> allMethodStatDataList = new ArrayList<Map<String, Object>>();

        for (Object stat : stats) {
            List<Map<String, Object>> methodStatDataList = SpringStatUtils.getMethodStatDataList(stat);
            allMethodStatDataList.addAll(methodStatDataList);
        }

        return allMethodStatDataList;
    }
    
    public Map<String, Object> getMethodStatData(String clazz, String method) {
        Set<Object> stats = getSpringStatSet();
        
        for (Object stat : stats) {
            Map<String, Object> statData = SpringStatUtils.getMethodStatData(stat, clazz, method);
            if (statData != null) {
                return statData;
            }
        }
        
        return null;
    }
    
    public void resetStat() {
        Set<Object> stats = getSpringStatSet();

        for (Object stat : stats) {
            SpringStatUtils.reset(stat);
        }
    }
}
