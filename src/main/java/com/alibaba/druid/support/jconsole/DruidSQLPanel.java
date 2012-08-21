package com.alibaba.druid.support.jconsole;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.json.JSONUtils;

public class DruidSQLPanel extends DruidPanel {

    private static final long serialVersionUID = 1L;

    protected void doInBackground(MBeanServerConnection conn) throws Exception {
        ObjectName name = new ObjectName(DruidStatService.MBEAN_NAME);
        String url = "/sql.json";
        String result = (String) conn.invoke(name, "service", new String[] { url }, new String[] { String.class.getName() });
        Object o = JSONUtils.parse(result);

        System.out.println(o);
    }
}
