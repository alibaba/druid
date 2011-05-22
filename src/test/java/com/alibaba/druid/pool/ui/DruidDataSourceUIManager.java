package com.alibaba.druid.pool.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceUIManager extends JFrame {

    private static final long                       serialVersionUID              = 1L;

    private DruidDataSource                         dataSource;

    private SpringLayout                            layout                        = new SpringLayout();

    private JButton                                 btnInitDataSource             = new JButton("Init Pool");
    private JButton                                 btnCloseDataSource            = new JButton("Close Pool");
    private JButton                                 btnConnect                    = new JButton("Get Connection");
    private JButton                                 btnClose                      = new JButton("Close Connection");

    private JPanel                                  mainPanel                     = new JPanel();
    private JScrollPane                             scrollPane                    = new JScrollPane(mainPanel);

    private JLabel                                  lbUrl                         = new JLabel("URL : ");
    private JTextField                              txtUrl                        = new JTextField("jdbc:mock:");

    private JLabel                                  lbDriverClass                 = new JLabel("DriverClassName : ");
    private JTextField                              txtDriverClass                = new JTextField();

    private JLabel                                  lbUser                        = new JLabel("User : ");
    private JTextField                              txtUser                       = new JTextField();

    private JLabel                                  lbPassword                    = new JLabel("Password : ");
    private JTextField                              txtPassword                   = new JTextField();

    private JLabel                                  lbConnectionProperties        = new JLabel("Connection Properties : ");
    private JTextField                              txtConnectionProperties       = new JTextField();

    private JLabel                                  lbInitialSize                 = new JLabel("InitialSize : ");
    private JTextField                              txtInitialSize                = new JTextField("0");

    private JLabel                                  lbMaxActive                   = new JLabel("MaxActive : ");
    private JTextField                              txtMaxActive                  = new JTextField("8");

    private JLabel                                  lbMaxIdle                     = new JLabel("MaxIdle : ");
    private JTextField                              txtMaxIdle                    = new JTextField("8");

    private JLabel                                  lbMinIdle                     = new JLabel("MinIdle : ");
    private JTextField                              txtMinIdle                    = new JTextField("0");

    private JLabel                                  lbMaxWait                     = new JLabel("MaxWait : ");
    private JTextField                              txtMaxWait                    = new JTextField("-1");

    private JLabel                                  lbMinEvictableIdleTimeMillis  = new JLabel("MinEvictableIdleTimeMillis : ");
    private JTextField                              txtMinEvictableIdleTimeMillis = new JTextField("1800000");

    private DruidDataStatusPanel                    statusPanel                   = new DruidDataStatusPanel();

    private JLabel                                  lbConnecting                  = new JLabel("Connecting : ");
    private JTextField                              txtConnecting                 = new JTextField("0");

    private AtomicInteger                           connectingCount               = new AtomicInteger();

    private Thread                                  statusThread;

    private final ConcurrentLinkedQueue<Connection> activeConnections             = new ConcurrentLinkedQueue<Connection>();

    public DruidDataSourceUIManager(){
        this.setLayout(new BorderLayout());

        this.setSize(800, 600);

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
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtDriverClass, 0, SpringLayout.VERTICAL_CENTER, lbDriverClass);
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
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtConnectionProperties, 0, SpringLayout.VERTICAL_CENTER, lbConnectionProperties);
        layout.putConstraint(SpringLayout.WEST, txtConnectionProperties, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtConnectionProperties, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(lbInitialSize);
        layout.putConstraint(SpringLayout.NORTH, lbInitialSize, 10, SpringLayout.SOUTH, lbConnectionProperties);
        layout.putConstraint(SpringLayout.WEST, lbInitialSize, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbInitialSize, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtInitialSize);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtInitialSize, 0, SpringLayout.VERTICAL_CENTER, lbInitialSize);
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
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtMinEvictableIdleTimeMillis, 0, SpringLayout.VERTICAL_CENTER, lbMinEvictableIdleTimeMillis);
        layout.putConstraint(SpringLayout.WEST, txtMinEvictableIdleTimeMillis, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtMinEvictableIdleTimeMillis, 0, SpringLayout.EAST, txtUrl);

        // ////

        mainPanel.add(btnInitDataSource);
        layout.putConstraint(SpringLayout.NORTH, btnInitDataSource, 10, SpringLayout.SOUTH, lbMinEvictableIdleTimeMillis);
        layout.putConstraint(SpringLayout.WEST, btnInitDataSource, 0, SpringLayout.WEST, lbUrl);
        btnInitDataSource.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                init_actionPerformed(e);
            }
        });

        mainPanel.add(btnCloseDataSource);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, btnCloseDataSource, 0, SpringLayout.VERTICAL_CENTER, btnInitDataSource);
        layout.putConstraint(SpringLayout.WEST, btnCloseDataSource, 10, SpringLayout.EAST, btnInitDataSource);
        btnCloseDataSource.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                closeDataSource_actionPerformed(e);
            }
        });
        btnCloseDataSource.setEnabled(false);

        mainPanel.add(btnConnect);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, btnConnect, 0, SpringLayout.VERTICAL_CENTER, btnInitDataSource);
        layout.putConstraint(SpringLayout.WEST, btnConnect, 10, SpringLayout.EAST, btnCloseDataSource);
        btnConnect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                connect_actionPerformed(e);
            }
        });

        mainPanel.add(btnClose);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, btnClose, 0, SpringLayout.VERTICAL_CENTER, btnInitDataSource);
        layout.putConstraint(SpringLayout.WEST, btnClose, 10, SpringLayout.EAST, btnConnect);
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Connection conn = activeConnections.poll();
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        mainPanel.add(statusPanel);
        layout.putConstraint(SpringLayout.NORTH, statusPanel, 10, SpringLayout.SOUTH, btnInitDataSource);
        layout.putConstraint(SpringLayout.SOUTH, statusPanel, 90, SpringLayout.NORTH, statusPanel);
        layout.putConstraint(SpringLayout.WEST, statusPanel, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, txtMaxWait, 0, SpringLayout.EAST, mainPanel);

        // ////

        mainPanel.add(lbConnecting);
        layout.putConstraint(SpringLayout.NORTH, lbConnecting, 10, SpringLayout.SOUTH, statusPanel);
        layout.putConstraint(SpringLayout.WEST, lbConnecting, 0, SpringLayout.WEST, lbUrl);
        layout.putConstraint(SpringLayout.EAST, lbConnecting, 0, SpringLayout.EAST, lbUrl);

        mainPanel.add(txtConnecting);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtConnecting, 0, SpringLayout.VERTICAL_CENTER, lbConnecting);
        layout.putConstraint(SpringLayout.WEST, txtConnecting, 0, SpringLayout.WEST, txtUrl);
        layout.putConstraint(SpringLayout.EAST, txtConnecting, 100, SpringLayout.WEST, txtConnecting);
        txtConnecting.setEditable(false);
    }

    public void connect_actionPerformed(ActionEvent e) {
        Thread connectThread = new Thread() {

            public void run() {
                try {
                    connectingCount.incrementAndGet();
                    txtConnecting.setText(Integer.toString(connectingCount.get()));

                    Connection conn = dataSource.getConnection();

                    connectingCount.decrementAndGet();
                    txtConnecting.setText(Integer.toString(connectingCount.get()));

                    activeConnections.add(conn);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        connectThread.start();
    }

    public void closeDataSource_actionPerformed(ActionEvent e) {
        dataSource.close();

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

            Connection conn = dataSource.getConnection();
            conn.close();

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

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                DruidDataSourceUIManager manager = new DruidDataSourceUIManager();

                manager.pack();

                manager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                manager.setVisible(true);
            }
        });

    }
}
