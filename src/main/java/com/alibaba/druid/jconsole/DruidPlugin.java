package com.alibaba.druid.jconsole;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.sun.tools.jconsole.JConsolePlugin;

public class DruidPlugin extends JConsolePlugin {

    private final Map<String, JPanel> tabs = new HashMap<String, JPanel>();
    private DruidPanel                panel;

    public DruidPlugin(){
        panel = new DruidPanel();
        tabs.put("Druid", panel);
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
        return panel.doInBackground(this.getContext());
    }
}
