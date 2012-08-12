package com.alibaba.druid.support.http.stat;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class WebAppStatManager {

    public final static String             SYS_PROP_INSTANCES = "druid.web.webAppStat";

    private final static WebAppStatManager instance           = new WebAppStatManager();

    private Set<Object>                    webAppStatSet      = null;

    public static WebAppStatManager getInstance() {
        return instance;
    }

    public Set<Object> getWebAppStatSet() {
        if (webAppStatSet == null) {
            webAppStatSet = getWebAppStatSet0();
        }

        return webAppStatSet;
    }

    public void addWebAppStatSet(Object webAppStat) {
        getWebAppStatSet().add(webAppStat);
    }

    public boolean remove(Object webAppStat) {
        return getWebAppStatSet().remove(webAppStat);
    }

    @SuppressWarnings("unchecked")
    static Set<Object> getWebAppStatSet0() {
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

}
