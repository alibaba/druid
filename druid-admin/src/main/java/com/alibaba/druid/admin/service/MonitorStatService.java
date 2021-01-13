package com.alibaba.druid.admin.service;

import com.alibaba.druid.admin.config.MonitorProperties;
import com.alibaba.druid.admin.model.ServiceNode;
import com.alibaba.druid.admin.model.dto.ConnectionResult;
import com.alibaba.druid.admin.model.dto.DataSourceResult;
import com.alibaba.druid.admin.model.dto.SqlDetailResult;
import com.alibaba.druid.admin.model.dto.SqlListResult;
import com.alibaba.druid.admin.model.dto.WallResult;
import com.alibaba.druid.admin.model.dto.WebResult;
import com.alibaba.druid.admin.util.HttpUtil;
import com.alibaba.druid.stat.DruidStatServiceMBean;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.support.spring.stat.SpringStatManager;
import com.alibaba.druid.util.MapComparator;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author linchtech
 * @date 2020-09-16 11:12
 **/
@Slf4j
@Component
public class MonitorStatService implements DruidStatServiceMBean {

    public final static int RESULT_CODE_SUCCESS = 1;
    public final static int RESULT_CODE_ERROR = -1;

    private final static int DEFAULT_PAGE = 1;
    private final static int DEFAULT_PER_PAGE_COUNT = Integer.MAX_VALUE;
    private static final String ORDER_TYPE_DESC = "desc";
    private static final String ORDER_TYPE_ASC = "asc";
    private static final String DEFAULT_ORDER_TYPE = ORDER_TYPE_ASC;
    private static final String DEFAULT_ORDERBY = "SQL";
    /**
     * 以consul注册的服务的id为key,value为某个微服务节点
     */
    public static Map<String, ServiceNode> serviceIdMap = new HashMap<>();

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private MonitorProperties monitorProperties;

    /**
     * 获取所有服务信息
     *
     * @return
     */
    public Map<String, ServiceNode> getAllServiceNodeMap(){
        List<String> services = discoveryClient.getServices();
        List<ServiceNode> serviceNodes = new ArrayList<>();
        for (String service : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            for (ServiceInstance instance : instances) {
                String host = instance.getHost();
                String instanceId = instance.getInstanceId();
                if (instanceId == null) {
                    instanceId = instance.getMetadata().get("nacos.instanceId").replaceAll("#", "-").replaceAll("@@", "-");
                }
                int port = instance.getPort();
                String serviceId = instance.getServiceId();
                // 根据前端参数采集指定的服务
                if (monitorProperties.getApplications().contains(serviceId)) {
                    ServiceNode serviceNode = new ServiceNode();
                    serviceNode.setId(instanceId);
                    serviceNode.setPort(port);
                    serviceNode.setAddress(host);
                    serviceNode.setServiceName(serviceId);
                    serviceNodes.add(serviceNode);
                    serviceIdMap.put(instanceId, serviceNode);
                }
            }
        }
        return serviceNodes.parallelStream().collect(Collectors.toMap(i -> i.getServiceName() + "-" + i.getAddress() + "-" + i.getPort(),
                Function.identity(), (v1, v2) -> v2));
    }

    /**
     * 获取指定服务名的所有节点
     *
     * @param parameters
     * @return
     */
    public Map<String, ServiceNode> getServiceAllNodeMap(Map<String, String> parameters){
        String requestServiceName = parameters.get("serviceName");
        List<String> services = discoveryClient.getServices();
        List<ServiceNode> serviceNodes = new ArrayList<>();

        for (String service : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            for (ServiceInstance instance : instances) {
                String host = instance.getHost();
                String instanceId = instance.getInstanceId();
                if (instanceId == null) {
                    instanceId = instance.getMetadata().get("nacos.instanceId").replaceAll("#", "-").replaceAll("@@", "-");
                }
                int port = instance.getPort();
                String serviceId = instance.getServiceId();
                // 根据前端参数采集指定的服务
                if (serviceId.equals(requestServiceName)) {
                    ServiceNode serviceNode = new ServiceNode();
                    serviceNode.setId(instanceId);
                    serviceNode.setPort(port);
                    serviceNode.setAddress(host);
                    serviceNode.setServiceName(serviceId);
                    serviceNodes.add(serviceNode);
                    serviceIdMap.put(instanceId, serviceNode);
                }
            }
        }
        return serviceNodes.parallelStream().collect(Collectors.toMap(i -> i.getServiceName() + "-" + i.getAddress() + "-" + i.getPort(),
                Function.identity(), (v1, v2) -> v2));
    }
    @Override
    public String service(String url) {
        Map<String, String> parameters = getParameters(url);
        if (url.endsWith("serviceList.json")) {
            return JSON.toJSONString(monitorProperties.getApplications());
        }

        if (url.equals("/datasource.json")) {
            String serviceName = StringUtils.subString(url, "serviceName=", "&sql-");
            Integer id = StringUtils.subStringToInteger(url, "datasource-", ".");
            return getDataSourceStatData();
        }


        if (url.startsWith("/datasource-")) {
            String serviceName = StringUtils.subString(url, "serviceName=", "&sql-");
            Integer id = StringUtils.subStringToInteger(url, "datasource-", ".");
            Object result = getDataSourceStatData();
            return returnJSONResult(result == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS, result);
        }
        // 活跃连接数查看
        if (url.startsWith("/connectionInfo-") && url.endsWith(".json")) {
            String serviceId = StringUtils.subString(url, "&serviceId=", ".json");
            Integer id = StringUtils.subStringToInteger(url, "connectionInfo-", "&");
            return getPoolingConnectionInfoByDataSourceId(id, serviceId);
        }

        // SQL监控列表
        if (url.startsWith("/sql.json")) {
            return getSqlStatDataList(parameters);
        }

        // SQL防火墙
        if (url.startsWith("/wall.json")) {
            return getWallStatMap(parameters);
        }

        // SQL详情
        if (url.startsWith("/serviceId") && url.indexOf(".json") > 0) {
            String serviceId = StringUtils.subString(url, "serviceId=", "&");
            Integer id = StringUtils.subStringToInteger(url, "sql-", ".json");
            return getSqlStat(id, serviceId);
        }

        if (url.startsWith("/weburi.json")) {
            return getWebURIStatDataList(parameters);
        }

        if (url.startsWith("/weburi-") && url.indexOf(".json") > 0) {
            String uri = StringUtils.subString(url, "weburi-", ".json", true);
            return returnJSONResult(RESULT_CODE_SUCCESS, getWebURIStatData(uri));
        }

        if (url.startsWith("/webapp.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, getWebAppStatDataList(parameters));
        }

        if (url.startsWith("/websession.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, getWebSessionStatDataList(parameters));
        }

        if (url.startsWith("/websession-") && url.indexOf(".json") > 0) {
            String id = StringUtils.subString(url, "websession-", ".json");
            return returnJSONResult(RESULT_CODE_SUCCESS, getWebSessionStatData(id));
        }

        if (url.startsWith("/spring.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, getSpringStatDataList(parameters));
        }

        if (url.startsWith("/spring-detail.json")) {
            String clazz = parameters.get("class");
            String method = parameters.get("method");
            return returnJSONResult(RESULT_CODE_SUCCESS, getSpringMethodStatData(clazz, method));
        }

        return returnJSONResult(RESULT_CODE_ERROR, "Do not support this request, please contact with administrator.");
    }

    public static String returnJSONResult(int resultCode, Object content) {
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("ResultCode", resultCode);
        dataMap.put("Content", content);
        return JSONUtils.toJSONString(dataMap);
    }


    public String getWallStatMap(Map<String, String> parameters) {
        Map<String, ServiceNode> allNodeMap = getServiceAllNodeMap(parameters);
        List<WallResult> countResult = new ArrayList<>();
        for (String nodeKey : allNodeMap.keySet()) {
            ServiceNode serviceNode = allNodeMap.get(nodeKey);
            String url = getRequestUrl(parameters, serviceNode, "/druid/wall.json");
            WallResult wallResult = HttpUtil.get(url, WallResult.class);
            countResult.add(wallResult);
        }
        WallResult lastCount = new WallResult();

        for (WallResult wallResult : countResult) {
            lastCount.sum(wallResult, lastCount);
        }
        return JSON.toJSONString(lastCount);
    }


    private List<Map<String, Object>> getSpringStatDataList(Map<String, String> parameters) {
        List<Map<String, Object>> array = SpringStatManager.getInstance().getMethodStatData();
        return comparatorOrderBy(array, parameters);
    }

    @SuppressWarnings("unchecked")
    private String getWebURIStatDataList(Map<String, String> parameters) {
        Map<String, ServiceNode> allNodeMap = getServiceAllNodeMap(parameters);
        List<Map<String, Object>> arrayMap = new ArrayList<>();
        for (String nodeKey : allNodeMap.keySet()) {
            ServiceNode serviceNode = allNodeMap.get(nodeKey);
            String url = getRequestUrl(parameters, serviceNode, "/druid/weburi.json");
            WebResult dataSourceResult = HttpUtil.get(url, WebResult.class);
            if (dataSourceResult != null) {
                List<WebResult.ContentBean> nodeContent = dataSourceResult.getContent();
                if (nodeContent != null) {
                    for (WebResult.ContentBean contentBean : nodeContent) {
                        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(contentBean), Map.class);
                        arrayMap.add(map);
                    }
                }
            }
        }
        List<Map<String, Object>> maps = comparatorOrderBy(arrayMap, parameters);
        String jsonString = JSON.toJSONString(maps);
        JSONArray objects = JSON.parseArray(jsonString);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ResultCode", RESULT_CODE_SUCCESS);
        jsonObject.put("Content", objects);
        return jsonObject.toJSONString();
    }

    private Map<String, Object> getWebURIStatData(String uri) {
        return WebAppStatManager.getInstance().getURIStatData(uri);
    }

    private Map<String, Object> getWebSessionStatData(String sessionId) {
        return WebAppStatManager.getInstance().getSessionStat(sessionId);
    }

    private Map<String, Object> getSpringMethodStatData(String clazz, String method) {
        return SpringStatManager.getInstance().getMethodStatData(clazz, method);
    }

    private List<Map<String, Object>> getWebSessionStatDataList(Map<String, String> parameters) {
        List<Map<String, Object>> array = WebAppStatManager.getInstance().getSessionStatData();
        return comparatorOrderBy(array, parameters);
    }

    private List<Map<String, Object>> getWebAppStatDataList(Map<String, String> parameters) {
        List<Map<String, Object>> array = WebAppStatManager.getInstance().getWebAppStatData();
        return comparatorOrderBy(array, parameters);
    }

    /**
     * 获取sql详情
     *
     * @param id
     * @param serviceId consul获取的服务id
     * @return
     */
    private String getSqlStat(Integer id, String serviceId) {
        log.info("serviceId:{}", serviceId);
        ServiceNode serviceNode = serviceIdMap.get(serviceId);
        String url = "http://" + serviceNode.getAddress() + ":" + serviceNode.getPort() + "/druid/sql-" + id + ".json";
        SqlDetailResult sqlDetailResult = HttpUtil.get(url, SqlDetailResult.class);
        return JSON.toJSONString(sqlDetailResult);
    }

    public String getPoolingConnectionInfoByDataSourceId(Integer id, String serviceId) {
        getAllServiceNodeMap();
        ServiceNode serviceNode = serviceIdMap.get(serviceId);
        String url = "http://" + serviceNode.getAddress() + ":" + serviceNode.getPort() + "/druid/connectionInfo-" + id + ".json";
        ConnectionResult connectionResult = HttpUtil.get(url, ConnectionResult.class);
        return JSON.toJSONString(connectionResult);
    }

    /**
     * SQL监控列表
     *
     * @param parameters
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getSqlStatDataList(Map<String, String> parameters) {
        Map<String, ServiceNode> serviceAllNodeMap = getServiceAllNodeMap(parameters);
        List<Map<String, Object>> arrayMap = new ArrayList<>();
        for (String nodeKey : serviceAllNodeMap.keySet()) {
            ServiceNode serviceNode = serviceAllNodeMap.get(nodeKey);
            String serviceName = serviceNode.getServiceName();

            String url = getRequestUrl(parameters, serviceNode, "/druid/sql.json");
            SqlListResult sqlListResult = HttpUtil.get(url, SqlListResult.class);
            if (sqlListResult != null) {
                List<SqlListResult.ContentBean> nodeContent = sqlListResult.getContent();
                if (nodeContent != null) {
                    for (SqlListResult.ContentBean contentBean : nodeContent) {
                        contentBean.setName(serviceName);
                        contentBean.setAddress(serviceNode.getAddress());
                        contentBean.setPort(serviceNode.getPort());
                        contentBean.setServiceId(serviceNode.getId());
                        Map map = JSONObject.parseObject(JSONObject.toJSONString(contentBean), Map.class);
                        arrayMap.add(map);
                    }
                }
            }
        }
        List<Map<String, Object>> maps = comparatorOrderBy(arrayMap, parameters);
        String jsonString = JSON.toJSONString(maps);
        JSONArray objects = JSON.parseArray(jsonString);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ResultCode", RESULT_CODE_SUCCESS);
        jsonObject.put("Content", objects);
        return jsonObject.toJSONString();
    }


    /**
     * 数据源监控
     *
     * @param
     * @return
     */
    public String getDataSourceStatData() {
        Map<String, ServiceNode> allNodeMap = getAllServiceNodeMap();

        DataSourceResult lastResult = new DataSourceResult();
        List<DataSourceResult.ContentBean> contentBeans = new ArrayList<>();
        for (String nodeKey : allNodeMap.keySet()) {
            ServiceNode serviceNode = allNodeMap.get(nodeKey);
            String serviceName = serviceNode.getServiceName();

            String url = "http://" + serviceNode.getAddress() + ":" + serviceNode.getPort() + "/druid/datasource.json";
            DataSourceResult dataSourceResult = HttpUtil.get(url, lastResult.getClass());
            if (dataSourceResult != null) {
                List<DataSourceResult.ContentBean> nodeContent = dataSourceResult.getContent();
                if (nodeContent != null) {
                    for (DataSourceResult.ContentBean contentBean : nodeContent) {
                        contentBean.setName(serviceName);
                        contentBean.setServiceId(serviceNode.getId());
                    }
                    contentBeans.addAll(nodeContent);
                }
            }
        }
        lastResult.setContent(contentBeans);
        return JSON.toJSONString(lastResult);
    }


    /**
     * 拼接url
     *
     * @param parameters
     * @param serviceNode
     * @param prefix
     * @return
     */
    private String getRequestUrl(Map<String, String> parameters, ServiceNode serviceNode, String prefix) {
        StringBuilder stringBuilder = new StringBuilder("http://");
        stringBuilder.append(serviceNode.getAddress());
        stringBuilder.append(":");
        stringBuilder.append(serviceNode.getPort());
        stringBuilder.append(prefix);
        stringBuilder.append("?orderBy=");
        stringBuilder.append(parameters.get("orderBy"));
        stringBuilder.append("&orderType=");
        stringBuilder.append(parameters.get("orderType"));
        stringBuilder.append("&page=");
        stringBuilder.append(parameters.get("page"));
        stringBuilder.append("&perPageCount=");
        stringBuilder.append(parameters.get("perPageCount"));
        stringBuilder.append("&");
        return stringBuilder.toString();
    }

    /**
     * 处理请求参数
     *
     * @param url
     * @return
     */
    public static Map<String, String> getParameters(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            return Collections.<String, String>emptyMap();
        }

        String parametersStr = StringUtils.subString(url, "?", null);
        if (parametersStr == null || parametersStr.length() == 0) {
            return Collections.<String, String>emptyMap();
        }

        String[] parametersArray = parametersStr.split("&");
        Map<String, String> parameters = new LinkedHashMap<String, String>();

        for (String parameterStr : parametersArray) {
            int index = parameterStr.indexOf("=");
            if (index <= 0) {
                continue;
            }

            String name = parameterStr.substring(0, index);
            String value = parameterStr.substring(index + 1);
            parameters.put(name, value);
        }
        return parameters;
    }

    private List<Map<String, Object>> comparatorOrderBy(List<Map<String, Object>> array, Map<String, String> parameters) {
        // when open the stat page before executing some sql
        if (array == null || array.isEmpty()) {
            return null;
        }

        // when parameters is null
        String orderBy, orderType = null;
        Integer page = DEFAULT_PAGE;
        Integer perPageCount = DEFAULT_PER_PAGE_COUNT;
        if (parameters == null) {
            orderBy = DEFAULT_ORDERBY;
            orderType = DEFAULT_ORDER_TYPE;
            page = DEFAULT_PAGE;
            perPageCount = DEFAULT_PER_PAGE_COUNT;
        } else {
            orderBy = parameters.get("orderBy");
            orderType = parameters.get("orderType");
            String pageParam = parameters.get("page");
            if (pageParam != null && pageParam.length() != 0) {
                page = Integer.parseInt(pageParam);
            }
            String pageCountParam = parameters.get("perPageCount");
            if (pageCountParam != null && pageCountParam.length() > 0) {
                perPageCount = Integer.parseInt(pageCountParam);
            }
        }

        // others,such as order
        orderBy = orderBy == null ? DEFAULT_ORDERBY : orderBy;
        orderType = orderType == null ? DEFAULT_ORDER_TYPE : orderType;

        if (!ORDER_TYPE_DESC.equals(orderType)) {
            orderType = ORDER_TYPE_ASC;
        }

        // orderby the statData array
        if (orderBy.trim().length() != 0) {
            Collections.sort(array, new MapComparator<String, Object>(orderBy, ORDER_TYPE_DESC.equals(orderType)));
        }

        // page
        int fromIndex = (page - 1) * perPageCount;
        int toIndex = page * perPageCount;
        if (toIndex > array.size()) {
            toIndex = array.size();
        }

        return array.subList(fromIndex, toIndex);
    }
}