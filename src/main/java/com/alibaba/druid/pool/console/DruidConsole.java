package com.alibaba.druid.pool.console;

import java.io.Console;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

public class DruidConsole {

    private ProxyClient           proxy;
    private Console               console;
    private PrintWriter           out;
    private List<DataSourceEntry> entries = new ArrayList<DataSourceEntry>();

    private DruidConsole(){
        console = System.console();
        out = console.writer();
    }

    public void run() throws Exception {
        proxy = ProxyClient.getProxyClient("10.20.133.134", 9006, null, null);
        proxy.connect();

        for (;;) {
            String line = console.readLine();
            if ("exit".equals(line)) {
                break;
            }

            if ("ls".equals(line)) {
                ls(line);
            }

            if ("desc".equals(line)) {
                desc(line);
            }

            if (line.startsWith("show ")) {
                show(line);
            }
        }
    }

    public void show(String line) throws Exception {
        String[] items = line.split(" ");

        DataSourceEntry current;
        {
            String arg = items[1];
            int index = Integer.parseInt(arg);
            current = entries.get(index);
        }

        long interval = 0;
        {
            String tmp = items[2];
            interval = Long.parseLong(tmp);
        }

        String[] attributes = new String[] { "CreateCount", "DestroyCount", "ActivePeak", "ActiveCount", "PoolingCount", "CommitCount", "RollbackCount" };
        String[] titles = attributes;
        
        for (int i = 0; i < titles.length; ++i) {
            if (i != 0) {
                out.print("\t\t");
            }
            out.print(titles[i]);
        }
        out.println();
        for (int i = 0; i < 10; ++i) {
            AttributeList values = proxy.getAttributes(current.getObjectName(), attributes);
            for (int j = 0; j < attributes.length; ++j) {
                Attribute attribute = (Attribute) values.get(j);
                if (j != 0) {
                    out.print("\t\t");
                }
                out.print(attribute.getValue());
            }
            out.println();
            Thread.sleep(interval);
        }
    }

    public void ls(String line) throws Exception {
        entries.clear();
        Map<ObjectName, MBeanInfo> beans = proxy.getMBeans(null);
        for (Map.Entry<ObjectName, MBeanInfo> entry : beans.entrySet()) {
            if ("com.alibaba.druid.pool.DruidDataSource".equals(entry.getValue().getClassName())) {
                entries.add(new DataSourceEntry(entry.getKey(), entry.getValue()));
            }
        }

        for (int i = 0; i < entries.size(); ++i) {
            out.println(i + " : " + entries.get(i).getObjectName());
        }
    }

    public void desc(String line) throws Exception {
        String arg = line.substring("desc ".length());
        int index = Integer.parseInt(arg);
        DataSourceEntry current = entries.get(index);

        MBeanInfo mbeanInfo = current.getMbeanInfo();
        String[] attributes = new String[mbeanInfo.getAttributes().length];
        for (int i = 0; i < attributes.length; ++i) {
            MBeanAttributeInfo attr = mbeanInfo.getAttributes()[i];
            attributes[i] = attr.getName();
        }
        AttributeList values = proxy.getAttributes(current.getObjectName(), attributes);
        for (int i = 0; i < attributes.length; ++i) {
            out.println("\t" + attributes[i] + " : " + values.get(i));
        }
    }

    public static void main(String[] args) throws Exception {
        DruidConsole console = new DruidConsole();
        console.run();

    }

    static class DataSourceEntry {

        private final ObjectName objectName;
        private final MBeanInfo  mbeanInfo;

        public DataSourceEntry(ObjectName objectName, MBeanInfo mbeanInfo){
            super();
            this.objectName = objectName;
            this.mbeanInfo = mbeanInfo;
        }

        public ObjectName getObjectName() {
            return objectName;
        }

        public MBeanInfo getMbeanInfo() {
            return mbeanInfo;
        }

    }
}
