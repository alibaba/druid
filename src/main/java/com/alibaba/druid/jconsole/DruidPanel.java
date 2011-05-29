package com.alibaba.druid.jconsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;

import com.sun.tools.jconsole.JConsoleContext;

public class DruidPanel extends JPanel {

    protected JSplitPane      mainSplit;
    protected JTree           tree;
    protected JPanel          sheet;

    private static final long serialVersionUID = 1L;

    public DruidPanel(){
        setLayout(new BorderLayout());

        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(160);
        mainSplit.setBorder(BorderFactory.createEmptyBorder());

        tree = new JTree();

        JScrollPane theScrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.add(theScrollPane, BorderLayout.CENTER);
        mainSplit.add(treePanel, JSplitPane.LEFT, 0);

        sheet = new JPanel();
        mainSplit.add(sheet, JSplitPane.RIGHT, 0);

        add(mainSplit);

        this.setBackground(Color.BLUE);
    }

    public void init() {

    }

    protected Object doInBackground(JConsoleContext context) throws Exception {
        MBeanServerConnection conn = context.getMBeanServerConnection();
        List<ObjectInstance> stats = new ArrayList<ObjectInstance>();

        Set<ObjectInstance> instances = conn.queryMBeans(null, null);
        for (ObjectInstance instance : instances) {
            MBeanInfo info = conn.getMBeanInfo(instance.getObjectName());
            if ("com.alibaba.druid.stat.JdbcStatManager".equals(info.getClassName())) {
                stats.add(instance);
                break;
            }
        }

        if (stats.size() == 0) {

        }

        this.setPreferredSize(new Dimension(400, 500));

        return null;
    }
}
