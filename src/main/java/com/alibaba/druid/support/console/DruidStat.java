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
package com.alibaba.druid.support.console;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
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

public class DruidStat {

    private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";

    public static void main(String[] args) throws Exception {
        Option opt = null;

        if (Option.isPrintHelp(args)) {
            Option.printHelp();
            return;
        }

        try {
            opt = Option.parseOptions(args);
        } catch (OptionParseException e) {
            Option.printHelp(e.getMessage());
            return;
        }

        printDruidStat(opt);
    }

    @SuppressWarnings("all")
    public static void printDruidStat(Option option) throws Exception {

		PrintStream out = option.getPrintStream();
        String address = loadManagementAgentAndGetAddress(option.getPid());
        JMXServiceURL jmxUrl = new JMXServiceURL(address);
        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl);
        MBeanServerConnection jmxConn = jmxc.getMBeanServerConnection();

        if (option.printDataSourceData()) {
            List<Map<String, Object>> content = (List<Map<String, Object>>) invokeService(jmxConn, Option.DATA_SOURCE);
            TabledDataPrinter.printDataSourceData(content, option);
        }
        
        if (option.printSqlData()) {
            List<Map<String, Object>> content = (List<Map<String, Object>>) invokeService(jmxConn, Option.SQL);
			if (content == null ) { 
				out.println("无SqlStat统计数据,请检查是否已执行了SQL");
			} else {
				TabledDataPrinter.printSqlData(content, option);
			}
        }
       
        if (option.printActiveConn()) {
            List<List<String>> content = (List<List<String>>) invokeService(jmxConn, Option.ACTIVE_CONN);
			if (content == null || content.size() == 0 ) {
				out.println("目前无活动中的数据库连接");
			} else {
				TabledDataPrinter.printActiveConnStack(content, option);
			}
        }
        
    }

    @SuppressWarnings("all")
	public static List<Integer> getDataSourceIds(Option option) throws Exception{
		String address = loadManagementAgentAndGetAddress(option.getPid());
        JMXServiceURL jmxUrl = new JMXServiceURL(address);
        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl);
        MBeanServerConnection jmxConn = jmxc.getMBeanServerConnection();
		List<Map<String, Object>> content = (List<Map<String, Object>>) invokeService(jmxConn, Option.DATA_SOURCE);
		TabledDataPrinter.printDataSourceData(content, option);

		List<Integer> result = new ArrayList<Integer>();
		for (Map<String, Object> dsStat : content) {
			Integer id = (Integer)dsStat.get("Identity");
			result.add(id);
		}
		return result;
	}

    @SuppressWarnings("all")
    public static Object invokeService(MBeanServerConnection jmxConn, int dataType) throws Exception {
            String url = Option.getUrl(dataType);
            ObjectName name = new ObjectName(DruidStatService.MBEAN_NAME);
            String result = (String) jmxConn.invoke(name, "service", new String[] { url }, new String[] { String.class.getName() });
            Map<String, Object> o = (Map<String, Object>) JSONUtils.parse(result);
            List<Map<String, Object>> content = (List<Map<String, Object>>) o.get("Content");
            return content;
    }

    private static String loadManagementAgentAndGetAddress(int vmid) throws IOException {
        VirtualMachine vm = null;
        String name = String.valueOf(vmid);
        try {
            vm = VirtualMachine.attach(name);
        } catch (AttachNotSupportedException x) {
            throw new IOException(x.getMessage(), x);
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
            throw new IOException(x.getMessage(), x);
        } catch (AgentInitializationException x) {
            throw new IOException(x.getMessage(), x);
        }

        // get the connector address
        Properties agentProps = vm.getAgentProperties();
        String address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);
        vm.detach();

        return address;
    }
}
