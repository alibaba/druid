package com.alibaba.druid.admin.service;

import com.alibaba.druid.admin.model.ServiceNode;
import com.alibaba.druid.admin.config.MonitorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author linchtech
 * @date 2020-09-22 13:52
 **/
@Component
@Slf4j
@ConditionalOnProperty(value = "eureka.client.enabled")
public class EurekaMonitorStatService extends MonitorStatService {

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private MonitorProperties monitorProperties;

    @Override
    public Map<String, ServiceNode> getAllServiceNodeMap() {
        List<String> services = discoveryClient.getServices();
        List<ServiceNode> serviceNodes = new ArrayList<>();
        for (String serviceId : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            for (ServiceInstance serviceInstance : instances) {
                if (monitorProperties.getApplications().contains(serviceId)) {
                    ServiceNode serviceNode = new ServiceNode();
                    serviceNode.setId(serviceInstance.getInstanceId());
                    serviceNode.setPort(serviceInstance.getPort());
                    serviceNode.setAddress(serviceInstance.getHost());
                    serviceNode.setServiceName(serviceId);
                    serviceNodes.add(serviceNode);
                    serviceIdMap.put(serviceInstance.getInstanceId(), serviceNode);
                }
            }
        }
        return serviceNodes.parallelStream().collect(Collectors.toMap(i -> i.getServiceName() + "-" + i.getAddress() + "-" + i.getPort(),
                Function.identity(), (v1, v2) -> v2));
    }

    @Override
    public Map<String, ServiceNode> getServiceAllNodeMap(Map<String, String> parameters) {
        String requestServiceName = parameters.get("serviceName");

        List<String> services = discoveryClient.getServices();
        List<ServiceNode> serviceNodes = new ArrayList<>();
        for (String serviceId : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            for (ServiceInstance serviceInstance : instances) {
                if (requestServiceName.equals(serviceId)) {
                    ServiceNode serviceNode = new ServiceNode();
                    serviceNode.setId(serviceInstance.getInstanceId());
                    serviceNode.setPort(serviceInstance.getPort());
                    serviceNode.setAddress(serviceInstance.getHost());
                    serviceNode.setServiceName(serviceId);
                    serviceNodes.add(serviceNode);
                    serviceIdMap.put(serviceInstance.getInstanceId(), serviceNode);
                }
            }
        }
        return serviceNodes.parallelStream().collect(Collectors.toMap(i -> i.getServiceName() + "-" + i.getAddress() + "-" + i.getPort(),
                Function.identity(), (v1, v2) -> v2));
    }
}
