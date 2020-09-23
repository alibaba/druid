package com.alibaba.druid.admin.service;

import com.alibaba.druid.admin.model.ServiceNode;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.Service;
import com.alibaba.druid.admin.config.MonitorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author linchtech
 * @date 2020-09-22 11:14
 **/
@Component
@Slf4j
@ConditionalOnProperty(value = "spring.cloud.consul.enabled")
public class ConsulMonitorStatService extends MonitorStatService {

    @Autowired
    private ConsulClient consulClient;
    @Autowired
    private MonitorProperties monitorProperties;

    @Override
    public Map<String, ServiceNode> getAllServiceNodeMap() {
        Response<Map<String, Service>> agentServices = consulClient.getAgentServices();
        Map<String, Service> value = agentServices.getValue();
        List<ServiceNode> serviceNodes = new ArrayList<>();
        for (String key : value.keySet()) {
            Service service = value.get(key);
            String address = service.getAddress();
            Integer port = service.getPort();
            String id = service.getId();
            String serviceName = id.substring(0, id.lastIndexOf("-"));
            // 根据前端参数采集指定的服务
            if (monitorProperties.getApplications().contains(serviceName)) {
                ServiceNode serviceNode = new ServiceNode();
                serviceNode.setId(id);
                serviceNode.setPort(port);
                serviceNode.setAddress(address);
                serviceNode.setServiceName(serviceName);
                serviceNodes.add(serviceNode);
                serviceIdMap.put(id, serviceNode);
            }
        }
        return serviceNodes.parallelStream().collect(Collectors.toMap(i -> i.getServiceName() + "-" + i.getAddress() + "-" + i.getPort(),
                Function.identity(), (v1, v2) -> v2));
    }

    @Override
    public Map<String, ServiceNode> getServiceAllNodeMap(Map<String, String> parameters) {
        String requestServiceName = parameters.get("serviceName");
        Response<Map<String, Service>> agentServices = consulClient.getAgentServices();
        Map<String, Service> value = agentServices.getValue();
        List<ServiceNode> serviceNodes = new ArrayList<>();
        for (String key : value.keySet()) {
            Service service = value.get(key);
            String address = service.getAddress();
            Integer port = service.getPort();
            String id = service.getId();
            String serviceName = id.substring(0, id.lastIndexOf("-"));
            if (serviceName.equals(requestServiceName)) {
                ServiceNode serviceNode = new ServiceNode();
                serviceNode.setPort(port);
                serviceNode.setId(id);
                serviceNode.setAddress(address);
                serviceNode.setServiceName(serviceName);
                serviceNodes.add(serviceNode);
                serviceIdMap.put(id, serviceNode);
            }
        }
        return serviceNodes.parallelStream().collect(Collectors.toMap(i -> i.getServiceName() + "-" + i.getAddress() + "-" + i.getPort(),
                Function.identity(), (v1, v2) -> v2));
    }
}
