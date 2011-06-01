package com.alibaba.druid.jconsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcStatManager;
import com.sun.tools.jconsole.JConsoleContext;

public class DruidPanel extends JPanel {

    private static final long        serialVersionUID = 1L;

    protected JSplitPane             mainSplit;
    protected JTree                  tree;
    protected JPanel                 sheet;
    protected DefaultMutableTreeNode rootNode;
    protected DefaultMutableTreeNode dataSourcesNode;

    public DruidPanel(){
        setLayout(new BorderLayout());

        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(160);
        mainSplit.setBorder(BorderFactory.createEmptyBorder());

        rootNode = new DefaultMutableTreeNode("root", true);
        dataSourcesNode = new DefaultMutableTreeNode("DataSources", true);
        rootNode.add(dataSourcesNode);

        tree = new JTree(rootNode);
        tree.setRootVisible(false);

        tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                treeSelect(e);
            }
        });

        JScrollPane theScrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.add(theScrollPane, BorderLayout.CENTER);
        mainSplit.setLeftComponent(treePanel);

        sheet = new JPanel();
        mainSplit.setRightComponent(sheet);

        add(mainSplit);

        this.setBackground(Color.BLUE);

        // init();
    }

    private void treeSelect(TreeSelectionEvent e) {
    }

    public void init() {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(JdbcStatManager.getInstance(), new ObjectName("com.alibaba.druid:type=JdbcStatManager"));

            DruidDataSource dataSource = new DruidDataSource();

            ManagementFactory.getPlatformMBeanServer().registerMBean(dataSource, new ObjectName("com.alibaba.druid:type=DataSource"));

            dataSource.setUrl("jdbc:mock:");
            dataSource.setFilters("stat,trace");

            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {

        }
    }

    protected Object doInBackground(JConsoleContext context) throws Exception {
        doInBackground(context.getMBeanServerConnection());

        return null;
    }

    protected void doInBackground(MBeanServerConnection conn) throws Exception {
        // dataSourcesNode.removeAllChildren();

        List<ObjectInstance> stats = new ArrayList<ObjectInstance>();
        List<ObjectInstance> dataSourceInstances = new ArrayList<ObjectInstance>();

        Set<ObjectInstance> instances = conn.queryMBeans(null, null);
        for (ObjectInstance instance : instances) {
            MBeanInfo info = conn.getMBeanInfo(instance.getObjectName());
            if ("com.alibaba.druid.stat.JdbcStatManager".equals(info.getClassName())) {
                stats.add(instance);
                continue;
            }

            if ("com.alibaba.druid.pool.DruidDataSource".equals(info.getClassName())) {
                dataSourceInstances.add(instance);
                continue;
            }
        }

        if (stats.size() == 0) {

        }

        for (ObjectInstance statInstance : stats) {
            TabularData tabularValue = (TabularData) conn.getAttribute(statInstance.getObjectName(), "DataSourceList");
            for (Object item : tabularValue.values()) {
                CompositeData rowData = (CompositeData) item;

                DataSourceInfo dataSourceInfo = new DataSourceInfo(rowData);

                DefaultMutableTreeNode dataSourceNode = new DefaultMutableTreeNode(dataSourceInfo.getName(), true);
                dataSourceNode.setUserObject(dataSourceInfo);

                DefaultMutableTreeNode connections = new DefaultMutableTreeNode("Connections", true);
                {
                }
                dataSourceNode.add(connections);

                DefaultMutableTreeNode sqlListNode = new DefaultMutableTreeNode("SQL", true);
                {

                }
                dataSourceNode.add(sqlListNode);

                dataSourcesNode.add(dataSourceNode);
            }
        }

        for (ObjectInstance dataSourceInstance : dataSourceInstances) {
            String name = (String) conn.getAttribute(dataSourceInstance.getObjectName(), "Name");

            DefaultMutableTreeNode dataSource = new DefaultMutableTreeNode(name, true);

            DefaultMutableTreeNode connections = new DefaultMutableTreeNode("Connections", true);
            {
            }
            dataSource.add(connections);

            DefaultMutableTreeNode sqlListNode = new DefaultMutableTreeNode("SQL", true);
            {

            }
            dataSource.add(sqlListNode);

            dataSourcesNode.add(dataSource);
        }

        // TreePath path = new TreePath(dataSourcesNode.children());
        // tree.scrollPathToVisible(path);
    }

    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    JFrame frame = new JFrame();

                    DruidPanel panel = new DruidPanel();

                    frame.getContentPane().add(panel);
                    frame.pack();
                    frame.setSize(1024, 768);

                    final String urlPath = "/jndi/rmi://10.20.138.25:9006/jmxrmi";
                    JMXServiceURL jmxUrl = new JMXServiceURL("rmi", "", 0, urlPath);

                    JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
                    panel.doInBackground(connector.getMBeanServerConnection());

                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
