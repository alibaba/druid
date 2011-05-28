package com.alibaba.druid.jconsole;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.sun.tools.jconsole.JConsolePlugin;

public class DruidPlugin extends JConsolePlugin {

    private final Map<String, JPanel> tabs = new HashMap<String, JPanel>();

    public DruidPlugin(){
        tabs.put("Druid", new DruidPanel());
    }

    @Override
    public Map<String, JPanel> getTabs() {
        return tabs;
    }

    @Override
    public SwingWorker<?, ?> newSwingWorker() {
        SwingWorker worer = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                return null;
            }

        };

        return worer;
    }

}
