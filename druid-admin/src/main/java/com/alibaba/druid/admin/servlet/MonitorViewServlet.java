package com.alibaba.druid.admin.servlet;

import com.alibaba.druid.admin.util.SpringContextUtils;
import com.alibaba.druid.support.http.ResourceServlet;
import com.alibaba.druid.admin.service.MonitorStatService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;

/**
 * @author linchtech
 * @date 2020-09-16 11:10
 **/
@Slf4j
public class MonitorViewServlet extends ResourceServlet {

    private MonitorStatService monitorStatService;

    public MonitorViewServlet() {
        super("support/http/resources");
    }

    public void init() throws ServletException {
        log.info("init MonitorViewServlet");
        super.init();
        monitorStatService = SpringContextUtils.getBean(MonitorStatService.class);
    }

    @Override
    protected String process(String url) {
        return monitorStatService.service(url);
    }

}
