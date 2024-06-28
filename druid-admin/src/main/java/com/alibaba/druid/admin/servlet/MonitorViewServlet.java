package com.alibaba.druid.admin.servlet;

import com.alibaba.druid.admin.service.MonitorStatService;
import com.alibaba.druid.support.http.ResourceServlet;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;

/**
 * @author linchtech
 * @date 2020-09-16 11:10
 **/
@Slf4j
public class MonitorViewServlet extends ResourceServlet {
    private final MonitorStatService monitorStatService;

    public MonitorViewServlet(MonitorStatService monitorStatService) {
        super("support/http/resources");
        this.monitorStatService = monitorStatService;
    }

    @Override
    public void init() throws ServletException {
        log.info("init MonitorViewServlet");
        super.init();
    }

    @Override
    protected String process(String url) {
        return monitorStatService.service(url);
    }

}
