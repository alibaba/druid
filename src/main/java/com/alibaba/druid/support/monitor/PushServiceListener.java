package com.alibaba.druid.support.monitor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class PushServiceListener implements ServletContextListener {

    private final static Log    LOG                = LogFactory.getLog(PushServiceListener.class);

    private final static String MONITOR_SERVER_URL = "monitorServerUrl";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PushService pushService = PushService.getInstance();
        String serverUrl = sce.getServletContext().getInitParameter(MONITOR_SERVER_URL);
        if (serverUrl == null) {
            LOG.error(MONITOR_SERVER_URL + " can't be null");
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(MONITOR_SERVER_URL + "= " + serverUrl);
        }
        pushService.setServerUrl(serverUrl);
        pushService.start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub

    }

}
