/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.jconsole.model.DruidTableModel;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.sun.tools.jconsole.JConsoleContext;

public abstract class DruidPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    protected static final int RESP_SUCCESS_RESULT = 1;
    protected static final String RESP_JSON_RESULT_KEY = "ResultCode";
    protected static final String RESP_JSON_CONTENT_KEY = "Content";
    protected static final long DEFAULT_ACTIVE_TIME = 5*60*1000;
    
    protected static final int DRUID_TABLE_WIDTH = 600;
    protected static final int DRUID_TABLE_HEIGHT = 500;
    
    protected JScrollPane scrollPane;
    protected DruidTableModel tableModel;
    protected JTable table;
    protected String url;
    
    /** 界面刷新的间隔时间，单位为毫秒. */
    protected long activeTime;
    protected long lastRefreshTime;
    private final static Log LOG = LogFactory.getLog(DruidPanel.class);
    
    protected DruidPanel(long  activeTime) {
    	this.activeTime = activeTime;
    }
    
    protected DruidPanel() {
    	activeTime = DEFAULT_ACTIVE_TIME;
    }
    
    @SuppressWarnings("unchecked")
    protected ArrayList<LinkedHashMap<String,Object>> parseData(Object respData) {
    	ArrayList<LinkedHashMap<String,Object>> data = null;
		if (respData instanceof Map) {
			LinkedHashMap<String,Object> map = (LinkedHashMap<String, Object>)respData;
			int rv = (Integer)map.get(RESP_JSON_RESULT_KEY);

			if (rv == RESP_SUCCESS_RESULT) {
				Object content = map.get(RESP_JSON_CONTENT_KEY);
				if (content instanceof List) {
					data = (ArrayList<LinkedHashMap<String, Object>>)content;
				} else if (content instanceof Map) {
					LinkedHashMap<String, Object> contentEle
						= (LinkedHashMap<String, Object>)content;
					data = new ArrayList<LinkedHashMap<String,Object>>();
					data.add(contentEle);
				}
			}
		}
    	return data;
    }
    
    protected Object getData(String url,MBeanServerConnection conn) throws Exception {
    	Object o = null;
    	ObjectName name = new ObjectName(DruidStatService.MBEAN_NAME);
        
        String result = (String) conn.invoke(name, "service",
         		new String[] { url }, new String[] { String.class.getName() });
        o = JSONUtils.parse(result);
        if (LOG.isDebugEnabled()) {
        	LOG.debug(o.toString());
        }
        return o;
    }
    
    protected abstract void tableDataProcess(ArrayList<LinkedHashMap<String,Object>> data);
    
    protected void addOrRefreshTable(String url,MBeanServerConnection conn) throws Exception {
		if (url != null) {
			boolean needRefresh = false;
			long timeNow = new Date().getTime();
			if (scrollPane == null) {
				table = new JTable();

				scrollPane = new JScrollPane(table);
				scrollPane.setAutoscrolls(true);
				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				setLayout(gridbag);

				c.fill = GridBagConstraints.BOTH;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 9;
				c.weighty = 9;

				gridbag.setConstraints(scrollPane, c);

				this.add(scrollPane);
				needRefresh = true;
				lastRefreshTime = timeNow;
			} else {
				if (lastRefreshTime + activeTime < timeNow) {
					needRefresh = true;
					lastRefreshTime = timeNow;
				}
			}
			if (needRefresh) {
				LOG.debug("refresh" + timeNow);
				ArrayList<LinkedHashMap<String, Object>> data = parseData(getData(
						url, conn));
				if (data != null) {
					tableDataProcess(data);
				}
			}

		} else {
			// url不存在
			LOG.warn("url不存在");
		}
    }

    protected Object doInBackground(JConsoleContext context) throws Exception {
        doInBackground(context.getMBeanServerConnection());

        return null;
    }
    
    protected void doInBackground(MBeanServerConnection conn) {
    	
        try {
			addOrRefreshTable(url,conn);
		} catch (Exception e) {
			LOG.warn("", e);
		}
    }
}