/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
