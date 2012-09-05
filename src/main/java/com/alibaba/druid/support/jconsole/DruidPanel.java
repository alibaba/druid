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

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

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
    private static final String COPYRIGHT_STRING = "<html>powered by <a href=\"http://blog.csdn.net/yunnysunny\">yunnysunny</a></html>";
    
    protected JScrollPane scrollPane;
    protected DruidTableModel tableModel;
    protected JTable table;
    protected JPanel copyrightPanel;
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

				scrollPane = new JScrollPane();
				scrollPane.setAutoscrolls(true);
				scrollPane.setBorder((TitledBorder)BorderFactory.createTitledBorder("数据区"));
				
				setLayout(null);
				scrollPane.setBounds(10, 10, getWidth()-20, getHeight()-80);				

				this.add(scrollPane);
				
				copyrightPanel = new JPanel();
				copyrightPanel.setBorder((TitledBorder)BorderFactory.createTitledBorder("版权区"));
				JLabel authorInfo = new JLabel(COPYRIGHT_STRING);
				copyrightPanel.add(authorInfo);
				
				this.add(copyrightPanel);
				copyrightPanel.setBounds(10, getHeight()-60, getWidth()-20, 60);
								
				needRefresh = true;
				lastRefreshTime = timeNow;
				this.addComponentListener(new ComponentListener() {
					
					@Override
					public void componentShown(ComponentEvent arg0) {
						
					}
					
					@Override
					public void componentResized(ComponentEvent arg0) {
						scrollPane.setBounds(10, 10, getWidth()-20,
								getHeight()-80);
						copyrightPanel.setBounds(10, getHeight()-60, getWidth()-20, 60);
					}
					
					@Override
					public void componentMoved(ComponentEvent arg0) {
						
					}
					
					@Override
					public void componentHidden(ComponentEvent arg0) {
						
					}
				});
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