/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import javax.management.MBeanServerConnection;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import com.alibaba.druid.support.jconsole.model.DruidTableModel;
import com.alibaba.druid.support.jconsole.util.TableDataProcessor;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.sun.tools.jconsole.JConsoleContext;

/**
 * druid面板的抽象类 在类在实现的时候，通过url地址获取数据，解析此数据，然后显示在界面中
 * 
 * @author yunnysunny<yunnysunny@gmail.com>
 */
public abstract class DruidPanel extends JPanel {

    private static final long   serialVersionUID    = 1L;

    /** 默认面板刷新的间隔时间 */
    protected static final long DEFAULT_ACTIVE_TIME = 5 * 60 * 1000;
    /** 版权信息字符串 */
    private static final String COPYRIGHT_STRING    = "<html>powered by <a href=\"http://blog.csdn.net/yunnysunny\">yunnysunny</a></html>";

    /** 滚动条面板 */
    protected JScrollPane       scrollPane;
    /** 表格模板 */
    protected DruidTableModel   tableModel;
    /** 表格 */
    protected JTable            table;
    /** 版权面板 */
    protected JPanel            copyrightPanel;
    /** json请求的地址 */
    protected String            url;

    /** 界面刷新的间隔时间，单位为毫秒. */
    protected long              activeTime;
    /** 上次刷新的时间 */
    protected long              lastRefreshTime;
    /** 与界面管理的MBeanServerConnection对象 */
    protected MBeanServerConnection conn;
    private final static Log    LOG                 = LogFactory.getLog(DruidPanel.class);

    /**
     * 根据传入的刷新时间间隔来初始化.
     * 
     * @param activeTime 刷新时间间隔
     */
    protected DruidPanel(long activeTime){
        this.activeTime = activeTime;
    }

    /**
     * 初始化刷新时间间隔为默认值
     */
    protected DruidPanel(){
        activeTime = DEFAULT_ACTIVE_TIME;
    }

    

    /**
     * 调用完service之后，各个子类对于解析后的数据的具体处理
     * 
     * @param data 解析后的数据
     */
    protected abstract void tableDataProcess(ArrayList<LinkedHashMap<String, Object>> data);

    /**
     * 如果是第一次调用，则生成表格对象；否则根据当前时间来和上次刷新时间的间隔，
     * 是否大于对象初始化时设定的时间间隔来判断是否刷新表格数据。
     * 
     * @param url service的地址
     * @param conn MBeanServerConnection对象
     * @throws Exception
     */
    protected void addOrRefreshTable(String url) throws Exception {
        if (url != null) {
            boolean needRefresh = false;
            long timeNow = new Date().getTime();
            if (scrollPane == null) {
                table = new JTable();

                scrollPane = new JScrollPane();
                scrollPane.setAutoscrolls(true);
                scrollPane.setBorder((TitledBorder) BorderFactory.createTitledBorder("数据区"));

                setLayout(null);
                scrollPane.setBounds(10, 10, getWidth() - 20, getHeight() - 80);

                this.add(scrollPane);

                copyrightPanel = new JPanel();
                copyrightPanel.setBorder((TitledBorder) BorderFactory.createTitledBorder("版权区"));
                JLabel authorInfo = new JLabel(COPYRIGHT_STRING);
                copyrightPanel.add(authorInfo);

                this.add(copyrightPanel);
                copyrightPanel.setBounds(10, getHeight() - 60, getWidth() - 20, 60);

                needRefresh = true;
                lastRefreshTime = timeNow;
                this.addComponentListener(new ComponentListener() {

                    @Override
                    public void componentShown(ComponentEvent arg0) {

                    }

                    @Override
                    public void componentResized(ComponentEvent arg0) {
                        scrollPane.setBounds(10, 10, getWidth() - 20, getHeight() - 80);
                        copyrightPanel.setBounds(10, getHeight() - 60, getWidth() - 20, 60);
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
                ArrayList<LinkedHashMap<String, Object>> data = TableDataProcessor.parseData(TableDataProcessor.getData(url,
                                                                                                                        conn));
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
        if (conn == null) {
            LOG.warn("MBeanServerConnection is null");
            return;
        }
        try {
            this.conn = conn;
            addOrRefreshTable(url);
        } catch (Exception e) {
            LOG.warn("", e);
        }
    }
}
