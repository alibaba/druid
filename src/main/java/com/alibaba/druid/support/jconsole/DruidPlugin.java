package com.alibaba.druid.support.jconsole;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.sun.tools.jconsole.JConsolePlugin;

public class DruidPlugin extends JConsolePlugin {

    private final Map<String, JPanel> tabs = new LinkedHashMap<String, JPanel>();

    public DruidPlugin(){
        tabs.put("Druid-Driver", new DruidDriverPanel());
        tabs.put("Druid-DataSource", new DruidDataSourcePanel());
        tabs.put("Druid-SQL", new DruidSQLPanel());
    }

    @Override
    public Map<String, JPanel> getTabs() {
        return tabs;
    }

    @Override
    public SwingWorker<?, ?> newSwingWorker() {
        SwingWorker<?, ?> worer = new SwingWorker<Object, Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                return DruidPlugin.this.doInBackground();
            }
        };

        return worer;
    }

    protected Object doInBackground() throws Exception {
        for (JPanel panel : tabs.values()) {
            ((DruidPanel) panel).doInBackground(this.getContext());
        }
        
        return null;
    }

}
