package com.alibaba.druid.jconsole;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
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

        JScrollPane theScrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.add(theScrollPane, BorderLayout.CENTER);
        mainSplit.add(treePanel, JSplitPane.LEFT, 0);

        sheet = new JPanel();
        mainSplit.add(sheet, JSplitPane.RIGHT, 0);

        add(mainSplit);

        this.setBackground(Color.BLUE);

        // init();
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
        MBeanServerConnection conn = context.getMBeanServerConnection();
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

                String name = (String) rowData.get("Name");

                DefaultMutableTreeNode dataSourceNode = new DefaultMutableTreeNode(name, true);

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

        return null;
    }
}
