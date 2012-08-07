package com.alibaba.druid.support.jconsole;

import javax.management.MBeanServerConnection;
import javax.swing.JPanel;

import com.sun.tools.jconsole.JConsoleContext;

public class DruidPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    protected Object doInBackground(JConsoleContext context) throws Exception {
        doInBackground(context.getMBeanServerConnection());

        return null;
    }
    
    protected void doInBackground(MBeanServerConnection conn) throws Exception {
        System.out.println("doInBackground");
    }
}
