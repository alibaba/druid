package com.alibaba.druid.admin.service;

import com.alibaba.druid.admin.model.ServiceNode;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class K8sDiscoveryClient {

    public Map<String, ServiceNode> getK8sPodsInfo(List<String> serviceNames, String kubeConfigFilePath, String namespace) throws IOException, ApiException {
        InputStream inputStream = new ClassPathResource(kubeConfigFilePath).getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(reader)).build();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();
        List<ServiceNode> serviceNodes = new ArrayList<>();
        V1PodList podList = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null, null);
        for (String serviceName : serviceNames) {
            V1Service service = api.readNamespacedService(serviceName, namespace, null);
            List<V1Pod> servicePods = podList.getItems().stream().filter(i -> Objects.requireNonNull(Objects.requireNonNull(i.getMetadata()).getName()).startsWith(serviceName)).collect(Collectors.toList());
            for (V1Pod pod : servicePods) {
                String podId = pod.getMetadata().getUid();
                String podIp = pod.getStatus().getPodIP();
                Integer port = Objects.requireNonNull(Objects.requireNonNull(service.getSpec()).getPorts()).stream().filter(i -> serviceName.equalsIgnoreCase(i.getName())).findFirst().get().getPort();
                ServiceNode serviceNode = new ServiceNode();
                serviceNode.setId(podId);
                serviceNode.setPort(port);
                serviceNode.setAddress(podIp);
                serviceNode.setServiceName(serviceName);
                serviceNodes.add(serviceNode);
                MonitorStatService.serviceIdMap.put(podId, serviceNode);
                log.info("pod info: " + serviceNode);
            }
        }
        return serviceNodes.stream().collect(Collectors.toMap(i -> i.getServiceName() + "-" + i.getAddress() + "-" + i.getPort(),
                Function.identity(), (v1, v2) -> v2));
    }


}
