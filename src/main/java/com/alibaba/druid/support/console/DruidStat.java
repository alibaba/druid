package com.alibaba.druid.support.console;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.json.JSONUtils;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

@SuppressWarnings({ "restriction" })
public class DruidStat {

    private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printHelp();
            return;
        }
        
        String option = args[0];
        String url;

        if (option.equals("-sql")) {
            url = "/sql.json";
        } else {
            printHelp();
            return;
        }
        
        String arg = args[1];
        int vmid = Integer.parseInt(arg);

        String address = loadManagementAgentAndGetAddress(vmid);
        JMXServiceURL jmxUrl = new JMXServiceURL(address);
        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl);
        MBeanServerConnection jmxConn = jmxc.getMBeanServerConnection();

        ObjectName name = new ObjectName(DruidStatService.MBEAN_NAME);

        String result = (String) jmxConn.invoke(name, "service", new String[] { url },
                                                new String[] { String.class.getName() });
        Map<String, Object> o = (Map<String, Object>) JSONUtils.parse(result);
        
        if (option.equals("-sql")) {
            List<Map<String, Object>> content = (List<Map<String, Object>>) o.get("Content");
            
            for (Map<String, Object> sqlStat : content) {
                Number id = (Number) sqlStat.get("ID");
                String sql = (String) sqlStat.get("SQL");
                System.out.print(id);
                System.out.print(" : ");
                System.out.println();
                System.out.println(sql);
                System.out.println();
            }
            
            String[] fields = new String[] {"ID", "RunningCount", "ExecuteCount" };
            for (int i = 0; i < fields.length; ++i) {
                if (i != 0) {
                    System.out.print('\t');
                }
                System.out.print(fields[i]);
            }
            System.out.println();
            
            for (Map<String, Object> sqlStat : content) {
                for (int i = 0; i < fields.length; ++i) {
                    if (i != 0) {
                        System.out.print('\t');
                    }
                    Object value = sqlStat.get(fields[i]);
                    System.out.print(value);
                }
                System.out.println();
            }
        }

        //System.out.println(o);

        // ... ...
    }

    private static void printHelp() {
        System.out.println("Usage: druidStat -help|-options");
        System.out.println("       druidStat -<option> <vmid>");
        System.out.println();
        System.out.println("Definitions: druidStat -help|-options");
        System.out.println("  <option>      An option reported by the -options option");
        
    }

    private static String loadManagementAgentAndGetAddress(int vmid) throws IOException {
        VirtualMachine vm = null;
        String name = String.valueOf(vmid);
        try {
            vm = VirtualMachine.attach(name);
        } catch (AttachNotSupportedException x) {
            IOException ioe = new IOException(x.getMessage());
            ioe.initCause(x);
            throw ioe;
        }

        String home = vm.getSystemProperties().getProperty("java.home");

        // Normally in ${java.home}/jre/lib/management-agent.jar but might
        // be in ${java.home}/lib in build environments.

        String agent = home + File.separator + "jre" + File.separator + "lib" + File.separator + "management-agent.jar";
        File f = new File(agent);
        if (!f.exists()) {
            agent = home + File.separator + "lib" + File.separator + "management-agent.jar";
            f = new File(agent);
            if (!f.exists()) {
                throw new IOException("Management agent not found");
            }
        }

        agent = f.getCanonicalPath();
        try {
            vm.loadAgent(agent, "com.sun.management.jmxremote");
        } catch (AgentLoadException x) {
            IOException ioe = new IOException(x.getMessage());
            ioe.initCause(x);
            throw ioe;
        } catch (AgentInitializationException x) {
            IOException ioe = new IOException(x.getMessage());
            ioe.initCause(x);
            throw ioe;
        }

        // get the connector address
        Properties agentProps = vm.getAgentProperties();
        String address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);
        vm.detach();

        return address;
    }
}
