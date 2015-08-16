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
package com.alibaba.druid.pool.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.ObjectName;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.alibaba.druid.TestUtil;
import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceUIManager extends JFrame {

    private static final long                       serialVersionUID              = 1L;

    private DruidDataSource                         dataSource;

    private SpringLayout                            layout                        = new SpringLayout();

    private JButton                                 btnInitDataSource             = new JButton("Init Pool");
    private JButton                                 btnCloseDataSource            = new JButton("Close Pool");
    private JButton                                 btnConnect                    = new JButton("Get Connection");
    private JButton                                 btnClose                      = new JButton("Close Connection");

    private JButton                                 btnCase_0                     = new JButton("Case 0");

    private JPanel                                  mainPanel                     = new JPanel();
    private JScrollPane                             scrollPane                    = new JScrollPane(mainPanel);

    private JLabel                                  lbUrl                         = new JLabel("URL : ");
    private JTextField                              txtUrl                        = new JTextField(
                                                                                                   "jdbc:oracle:thin:@a.b.c.d:1521:ocndb");

    private JLabel                                  lbDriverClass                 = new JLabel("DriverClassName : ");
    private JTextField                              txtDriverClass                = new JTextField();

    private JLabel                                  lbUser                        = new JLabel("User : ");
    private JTextField                              txtUser                       = new JTextField();

    private JLabel                                  lbPassword                    = new JLabel("Password : ");
    private JTextField                              txtPassword                   = new JTextField();

    private JLabel                                  lbConnectionProperties        = new JLabel(
                                                                                               "Connection Properties : ");
    private JTextField                              txtConnectionProperties       = new JTextField();

    private JLabel                                  lbInitialSize                 = new JLabel("InitialSize : ");
    private JTextField                              txtInitialSize                = new JTextField("1");

    private JLabel                                  lbMaxActive                   = new JLabel("MaxActive : ");
    private JTextField                              txtMaxActive                  = new JTextField("14");

    private JLabel                                  lbMaxIdle                     = new JLabel("MaxIdle : ");
    private JTextField                              txtMaxIdle                    = new JTextField("14");

    private JLabel                                  lbMinIdle                     = new JLabel("MinIdle : ");
    private JTextField                              txtMinIdle                    = new JTextField("1");

    private JLabel                                  lbMaxWait                     = new JLabel("MaxWait : ");
    private JTextField                              txtMaxWait                    = new JTextField("-1");

    private JLabel                                  lbMinEvictableIdleTimeMillis  = new JLabel(
                                                                                               "MinEvictableIdleTimeMillis : ");
    private JTextField                              txtMinEvictableIdleTimeMillis = new JTextField("1800000");

    private DruidDataStatusPanel                    statusPanel                   = new DruidDataStatusPanel();

    private JLabel                                  lbValidationQuery             = new JLabel("ValidationQuery : ");
    private JTextField                              txtValidationQuery            = new JTextField("");

    private JLabel                                  lbTestWhileIdle               = new JLabel("TestWhileIdle : ");
    private JTextField                              txtTestWhileIdle              = new JTextField("false");

    private JLabel                                  lbTestOnBorrow                = new JLabel("TestOnBorrow : ");
    private JTextField                              txtTestOnBorrow               = new JTextField("false");

    private JTextField                              txtGetStep                    = new JTextField("1");
    private JTextField                              txtReleaseStep                = new JTextField("1");

    private AtomicInteger                           connectingCount               = new AtomicInteger();
    private AtomicInteger                           connectCount                  = new AtomicInteger();
    private AtomicInteger                           closeCount                    = new AtomicInteger();
    private AtomicInteger                           executingCount                = new AtomicInteger();

    private Thread                                  statusThread;

    private final ConcurrentLinkedQueue<Connection> activeConnections             = new ConcurrentLinkedQueue<Connection>();

    private ExecutorService                         executor;

    public DruidDataSourceUIManager(){
        this.setLayout(new BorderLayout());

        Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
        Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width / 2; // 获取屏幕的宽
        int screenHeight = screenSize.height / 2; // 获取屏幕的高
        int height = this.getHeight();
        int width = this.getWidth();

        setLocation(screenWidth - width / 2, screenHeight - height / 2);

        this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        mainPanel.setLayout(layout);

        mainPanel.add(lbUrl);
        layout.putConstraint(SpringLayout.NORTH, lbUrl, 10, SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.WEST, lbUrl, 10, SpringLayout.WEST, mainPanel);
        layout.putConstraint(SpringLayout.EAST, lbUrl, 200, SpringLayout.WEST, lbUrl);

        mainPanel.add(txtUrl);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtUrl, 0, SpringLayout.VERTICAL_CENTER, lbUrl);
        layout.putConstraint(SpringLayout.WEST, txtUrl, 10, SpringLayout.EAST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, txtUrl, -10, SpringLayout.EAST, mainPanel);

        // ////

        mainPanel.add(lbDriverClass);
        layout.putConstraint(SpringLayout.NORTH, lbDriverClass, 10, SpringLayout.SOUTH, lbUrl);
        layout.putConstraint(SpringLayout.WEST, lbDriverClass, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbDriverClass, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtDriverClass);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtDriverClass, 0, SpringLayout.VERTICAL_CENTER,
                             lbDriverClass);
        layout.putConstraint(SpringLayout.WEST, txtDriverClass, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtDriverClass, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbUser);
        layout.putConstraint(SpringLayout.NORTH, lbUser, 10, SpringLayout.SOUTH, lbDriverClass);
        layout.putConstraint(SpringLayout.WEST, lbUser, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbUser, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtUser);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtUser, 0, SpringLayout.VERTICAL_CENTER, lbUser);
        layout.putConstraint(SpringLayout.WEST, txtUser, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtUser, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbPassword);
        layout.putConstraint(SpringLayout.NORTH, lbPassword, 10, SpringLayout.SOUTH, lbUser);
        layout.putConstraint(SpringLayout.WEST, lbPassword, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbPassword, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtPassword);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtPassword, 0, SpringLayout.VERTICAL_CENTER, lbPassword);
        layout.putConstraint(SpringLayout.WEST, txtPassword, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtPassword, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbConnectionProperties);
        layout.putConstraint(SpringLayout.NORTH, lbConnectionProperties, 10, SpringLayout.SOUTH, lbPassword);
        layout.putConstraint(SpringLayout.WEST, lbConnectionProperties, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbConnectionProperties, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtConnectionProperties);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtConnectionProperties, 0, SpringLayout.VERTICAL_CENTER,
                             lbConnectionProperties);
        layout.putConstraint(SpringLayout.WEST, txtConnectionProperties, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtConnectionProperties, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbInitialSize);
        layout.putConstraint(SpringLayout.NORTH, lbInitialSize, 10, SpringLayout.SOUTH, lbConnectionProperties);
        layout.putConstraint(SpringLayout.WEST, lbInitialSize, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbInitialSize, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtInitialSize);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtInitialSize, 0, SpringLayout.VERTICAL_CENTER,
                             lbInitialSize);
        layout.putConstraint(SpringLayout.WEST, txtInitialSize, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtInitialSize, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbMaxActive);
        layout.putConstraint(SpringLayout.NORTH, lbMaxActive, 10, SpringLayout.SOUTH, lbInitialSize);
        layout.putConstraint(SpringLayout.WEST, lbMaxActive, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbMaxActive, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtMaxActive);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtMaxActive, 0, SpringLayout.VERTICAL_CENTER, lbMaxActive);
        layout.putConstraint(SpringLayout.WEST, txtMaxActive, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtMaxActive, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbMaxIdle);
        layout.putConstraint(SpringLayout.NORTH, lbMaxIdle, 10, SpringLayout.SOUTH, lbMaxActive);
        layout.putConstraint(SpringLayout.WEST, lbMaxIdle, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbMaxIdle, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtMaxIdle);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtMaxIdle, 0, SpringLayout.VERTICAL_CENTER, lbMaxIdle);
        layout.putConstraint(SpringLayout.WEST, txtMaxIdle, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtMaxIdle, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbMinIdle);
        layout.putConstraint(SpringLayout.NORTH, lbMinIdle, 10, SpringLayout.SOUTH, lbMaxIdle);
        layout.putConstraint(SpringLayout.WEST, lbMinIdle, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbMinIdle, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtMinIdle);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtMinIdle, 0, SpringLayout.VERTICAL_CENTER, lbMinIdle);
        layout.putConstraint(SpringLayout.WEST, txtMinIdle, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtMinIdle, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbMaxWait);
        layout.putConstraint(SpringLayout.NORTH, lbMaxWait, 10, SpringLayout.SOUTH, lbMinIdle);
        layout.putConstraint(SpringLayout.WEST, lbMaxWait, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbMaxWait, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtMaxWait);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtMaxWait, 0, SpringLayout.VERTICAL_CENTER, lbMaxWait);
        layout.putConstraint(SpringLayout.WEST, txtMaxWait, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtMaxWait, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbMinEvictableIdleTimeMillis);
        layout.putConstraint(SpringLayout.NORTH, lbMinEvictableIdleTimeMillis, 10, SpringLayout.SOUTH, lbMaxWait);
        layout.putConstraint(SpringLayout.WEST, lbMinEvictableIdleTimeMillis, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbMinEvictableIdleTimeMillis, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtMinEvictableIdleTimeMillis);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtMinEvictableIdleTimeMillis, 0,
                             SpringLayout.VERTICAL_CENTER, lbMinEvictableIdleTimeMillis);
        layout.putConstraint(SpringLayout.WEST, txtMinEvictableIdleTimeMillis, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtMinEvictableIdleTimeMillis, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbValidationQuery);
        layout.putConstraint(SpringLayout.NORTH, lbValidationQuery, 10, SpringLayout.SOUTH,
                             lbMinEvictableIdleTimeMillis);
        layout.putConstraint(SpringLayout.WEST, lbValidationQuery, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbValidationQuery, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtValidationQuery);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtValidationQuery, 0, SpringLayout.VERTICAL_CENTER,
                             lbValidationQuery);
        layout.putConstraint(SpringLayout.WEST, txtValidationQuery, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtValidationQuery, 0, SpringLayout.EAST, txtUrl);
        // ////

        mainPanel.add(lbTestWhileIdle);
        layout.putConstraint(SpringLayout.NORTH, lbTestWhileIdle, 10, SpringLayout.SOUTH, lbValidationQuery);
        layout.putConstraint(SpringLayout.WEST, lbTestWhileIdle, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbTestWhileIdle, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtTestWhileIdle);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtTestWhileIdle, 0, SpringLayout.VERTICAL_CENTER,
                             lbTestWhileIdle);
        layout.putConstraint(SpringLayout.WEST, txtTestWhileIdle, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtTestWhileIdle, 0, SpringLayout.EAST, txtUrl);

        mainPanel.add(lbTestOnBorrow);
        layout.putConstraint(SpringLayout.NORTH, lbTestOnBorrow, 10, SpringLayout.SOUTH, lbTestWhileIdle);
        layout.putConstraint(SpringLayout.WEST, lbTestOnBorrow, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbTestOnBorrow, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtTestOnBorrow);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtTestOnBorrow, 0, SpringLayout.VERTICAL_CENTER,
                             lbTestOnBorrow);
        layout.putConstraint(SpringLayout.WEST, txtTestOnBorrow, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtTestOnBorrow, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(btnInitDataSource);
        layout.putConstraint(SpringLayout.NORTH, btnInitDataSource, 10, SpringLayout.SOUTH, lbTestOnBorrow);
        layout.putConstraint(SpringLayout.WEST, btnInitDataSource, 0, SpringLayout.WEST, lbUrl);
        btnInitDataSource.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                init_actionPerformed(e);
            }
        });

        mainPanel.add(btnCloseDataSource);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, btnCloseDataSource, 0, SpringLayout.VERTICAL_CENTER,
                             btnInitDataSource);
        layout.putConstraint(SpringLayout.WEST, btnCloseDataSource, 10, SpringLayout.EAST, btnInitDataSource);
        btnCloseDataSource.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                closeDataSource_actionPerformed(e);
            }
        });
        btnCloseDataSource.setEnabled(false);
        btnConnect.setEnabled(false);
        btnClose.setEnabled(false);
        btnCase_0.setEnabled(false);

        mainPanel.add(btnConnect);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, btnConnect, 0, SpringLayout.VERTICAL_CENTER,
                             btnInitDataSource);
        layout.putConstraint(SpringLayout.WEST, btnConnect, 10, SpringLayout.EAST, btnCloseDataSource);
        btnConnect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                connect_actionPerformed(e);
            }
        });

        mainPanel.add(txtGetStep);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtGetStep, 0, SpringLayout.VERTICAL_CENTER,
                             btnInitDataSource);
        layout.putConstraint(SpringLayout.WEST, txtGetStep, 10, SpringLayout.EAST, btnConnect);
        layout.putConstraint(SpringLayout.EAST, txtGetStep, 40, SpringLayout.WEST, txtGetStep);

        // txtGetStep

        mainPanel.add(btnClose);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, btnClose, 0, SpringLayout.VERTICAL_CENTER, btnInitDataSource);
        layout.putConstraint(SpringLayout.WEST, btnClose, 10, SpringLayout.EAST, txtGetStep);
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int step = Integer.parseInt(txtReleaseStep.getText().trim());
                for (int i = 0; i < step; ++i) {
                    Connection conn = activeConnections.poll();
                    if (conn != null) {
                        try {
                            conn.close();
                            closeCount.incrementAndGet();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        // System.out.println("close connection is null");
                    }
                }
            }
        });

        mainPanel.add(txtReleaseStep);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtReleaseStep, 0, SpringLayout.VERTICAL_CENTER,
                             btnInitDataSource);
        layout.putConstraint(SpringLayout.WEST, txtReleaseStep, 10, SpringLayout.EAST, btnClose);
        layout.putConstraint(SpringLayout.EAST, txtReleaseStep, 40, SpringLayout.WEST, txtReleaseStep);

        mainPanel.add(btnCase_0);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, btnCase_0, 0, SpringLayout.VERTICAL_CENTER, txtReleaseStep);
        layout.putConstraint(SpringLayout.WEST, btnCase_0, 10, SpringLayout.EAST, txtReleaseStep);
        btnCase_0.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    case_0();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        // txtReleaseStep

        mainPanel.add(statusPanel);
        layout.putConstraint(SpringLayout.NORTH, statusPanel, 10, SpringLayout.SOUTH, btnInitDataSource);
        layout.putConstraint(SpringLayout.SOUTH, statusPanel, 120, SpringLayout.NORTH, statusPanel);
        layout.putConstraint(SpringLayout.WEST, statusPanel, 0, SpringLayout.WEST, lbUrl);
        // layout.putConstraint(SpringLayout.EAST, txtMaxWait, 0, SpringLayout.EAST, mainPanel);

        // ////

    }

    public void connect_actionPerformed(ActionEvent e) {
        int step = Integer.parseInt(txtGetStep.getText().trim());
        for (int i = 0; i < step; ++i) {
            final Runnable task = new Runnable() {

                public void run() {
                    try {
                        connectingCount.incrementAndGet();

                        Connection conn = dataSource.getConnection();

                        connectCount.incrementAndGet();
                        connectingCount.decrementAndGet();

                        if (conn == null) {
                            System.out.println("get connection is null");
                            return;
                        }

                        executingCount.incrementAndGet();
                        try {
                            Statement stmt = conn.createStatement();
                            stmt.setQueryTimeout(5);
                            ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
                            while (rs.next()) {
                                rs.getObject(1);
                            }
                            Thread.sleep(1000 * 3);
                            rs.close();
                            stmt.close();
                        } finally {
                            executingCount.decrementAndGet();
                        }

                        activeConnections.add(conn);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            executor.submit(task);
        }
    }

    public void closeDataSource_actionPerformed(ActionEvent e) {
        dataSource.close();

        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName(
                                                                                      "com.alibaba.druid:type=DruidDataSource"));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        txtUrl.setEnabled(true);
        txtDriverClass.setEnabled(true);
        txtConnectionProperties.setEnabled(true);
        txtUser.setEnabled(true);
        txtPassword.setEnabled(true);
        txtInitialSize.setEnabled(true);
        txtMaxActive.setEnabled(true);
        txtMaxIdle.setEnabled(true);
        txtMinIdle.setEnabled(true);
        txtMaxWait.setEnabled(true);
        txtMinEvictableIdleTimeMillis.setEnabled(true);

        btnInitDataSource.setEnabled(true);
        btnCloseDataSource.setEnabled(false);
        btnConnect.setEnabled(false);
        btnClose.setEnabled(false);
        btnCase_0.setEnabled(false);

        statusThread.interrupt();
    }

    public void init_actionPerformed(ActionEvent e) {
        try {
            dataSource = new DruidDataSource();
            dataSource.setUrl(txtUrl.getText().trim());
            dataSource.setDriverClassName(txtDriverClass.getText().trim());
            dataSource.setConnectionProperties(txtConnectionProperties.getText().trim());
            dataSource.setUsername(txtUser.getText().trim());
            dataSource.setPassword(txtPassword.getText().trim());
            dataSource.setInitialSize(Integer.parseInt(txtInitialSize.getText().trim()));
            dataSource.setMaxActive(Integer.parseInt(txtMaxActive.getText().trim()));
            dataSource.setMaxIdle(Integer.parseInt(txtMaxIdle.getText().trim()));
            dataSource.setMinIdle(Integer.parseInt(txtMinIdle.getText().trim()));
            dataSource.setMaxWait(Integer.parseInt(txtMaxWait.getText().trim()));
            dataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(txtMinEvictableIdleTimeMillis.getText().trim()));
            dataSource.setTestWhileIdle(Boolean.parseBoolean(txtTestWhileIdle.getText().trim()));
            dataSource.setTestOnBorrow(Boolean.parseBoolean(txtTestOnBorrow.getText().trim()));

            dataSource.setTimeBetweenEvictionRunsMillis(60000);
            dataSource.setNumTestsPerEvictionRun(20);

            ManagementFactory.getPlatformMBeanServer().registerMBean(dataSource,
                                                                     new ObjectName(
                                                                                    "com.alibaba.druid:type=DruidDataSource"));

            try {
                Connection conn = dataSource.getConnection();
                connectCount.incrementAndGet();
                conn.close();
                closeCount.incrementAndGet();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            executor = Executors.newCachedThreadPool();

            txtDriverClass.setText(dataSource.getDriverClassName());
            txtMinEvictableIdleTimeMillis.setText(Long.toString(dataSource.getMinEvictableIdleTimeMillis()));

            txtUrl.setEnabled(false);
            txtDriverClass.setEnabled(false);
            txtConnectionProperties.setEnabled(false);
            txtUser.setEnabled(false);
            txtPassword.setEnabled(false);
            txtInitialSize.setEnabled(false);
            txtMaxActive.setEnabled(false);
            txtMaxIdle.setEnabled(false);
            txtMinIdle.setEnabled(false);
            txtMaxWait.setEnabled(false);
            txtMinEvictableIdleTimeMillis.setEnabled(false);

            btnInitDataSource.setEnabled(false);
            btnCloseDataSource.setEnabled(true);
            btnConnect.setEnabled(true);
            btnClose.setEnabled(true);
            btnCase_0.setEnabled(true);

            statusThread = new Thread("Watch Status") {

                public void run() {
                    for (;;) {
                        statusPanel.set("CreateCount", dataSource.getCreateCount());
                        statusPanel.set("CreateErrorCount", dataSource.getCreateErrorCount());
                        statusPanel.set("CreateTimespanMillis", dataSource.getCreateTimespanMillis());
                        statusPanel.set("CreateTimespanNano", dataSource.getCreateTimespanNano());
                        statusPanel.set("DestroyCount", dataSource.getDestroyCount());
                        statusPanel.set("ConnectCount", dataSource.getConnectCount());
                        statusPanel.set("ConnectErrorCount", dataSource.getConnectErrorCount());
                        statusPanel.set("CloseCount", dataSource.getCloseCount());
                        statusPanel.set("RecycleCount", dataSource.getRecycleCount());
                        statusPanel.set("ActiveCount", dataSource.getActiveCount());
                        statusPanel.set("PoolingCount", dataSource.getPoolingCount());
                        statusPanel.set("UI_GettingCount", connectingCount.get());
                        statusPanel.set("UI_GetCount", connectCount.get());
                        statusPanel.set("UI_ReleaseCount", closeCount.get());
                        statusPanel.set("UI_ExecutingCount", executingCount.get());

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            };
            statusThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void case_0() throws Exception {
        Runnable task = new Runnable() {

            public void run() {
                final int threadCount = 20;
                final int LOOP_COUNT = 1000 * 1;
                final String sql = "SELECT 1 FROM DUAL";
                final CountDownLatch startLatch = new CountDownLatch(1);
                final CountDownLatch endLatch = new CountDownLatch(threadCount);
                for (int i = 0; i < threadCount; ++i) {
                    Thread thread = new Thread() {

                        public void run() {
                            try {
                                startLatch.await();

                                for (int i = 0; i < LOOP_COUNT; ++i) {
                                    Connection conn = dataSource.getConnection();
                                    Statement stmt = conn.createStatement();
                                    ResultSet rs = stmt.executeQuery(sql);
                                    while (rs.next()) {
                                        rs.getInt(1);
                                    }
                                    rs.close();
                                    stmt.close();

                                    Thread.sleep(1);

                                    conn.close();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            endLatch.countDown();
                        }
                    };
                    thread.start();
                }
                long startMillis = System.currentTimeMillis();
                long startYGC = TestUtil.getYoungGC();
                long startFullGC = TestUtil.getFullGC();
                startLatch.countDown();
                try {
                    endLatch.await();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                long millis = System.currentTimeMillis() - startMillis;
                long ygc = TestUtil.getYoungGC() - startYGC;
                long fullGC = TestUtil.getFullGC() - startFullGC;

                System.out.println("thread " + threadCount + " druid millis : "
                                   + NumberFormat.getInstance().format(millis) + ", YGC " + ygc + " FGC " + fullGC);
            }
        };

        executor.submit(task);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                DruidDataSourceUIManager manager = new DruidDataSourceUIManager();

                manager.pack();
                manager.setSize(820, 580);
                int w = (Toolkit.getDefaultToolkit().getScreenSize().width - manager.getWidth()) / 2;
                int h = (Toolkit.getDefaultToolkit().getScreenSize().height - manager.getHeight()) / 2;
                manager.setLocation(w, h);

                manager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                manager.setVisible(true);
            }
        });

    }
}
