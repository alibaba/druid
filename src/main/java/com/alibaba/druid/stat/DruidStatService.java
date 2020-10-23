/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.stat;

import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.support.spring.stat.SpringStatManager;
import com.alibaba.druid.util.MapComparator;
import com.alibaba.druid.util.StringUtils;

/**
 * 注意：避免直接调用Druid相关对象例如DruidDataSource等，相关调用要到DruidStatManagerFacade里用反射实现
 * 
 * @author sandzhang[sandzhangtoo@gmail.com]
 */
public final class DruidStatService implements DruidStatServiceMBean {

    private final static Log              LOG                    = LogFactory.getLog(DruidStatService.class);

    public final static String            MBEAN_NAME             = "com.alibaba.druid:type=DruidStatService";

    private final static DruidStatService instance               = new DruidStatService();

    private static DruidStatManagerFacade statManagerFacade      = DruidStatManagerFacade.getInstance();

    public final static int               RESULT_CODE_SUCCESS    = 1;
    public final static int               RESULT_CODE_ERROR      = -1;

    private final static int              DEFAULT_PAGE           = 1;
    private final static int              DEFAULT_PER_PAGE_COUNT = Integer.MAX_VALUE;
    private static final String           ORDER_TYPE_DESC        = "desc";
    private static final String           ORDER_TYPE_ASC         = "asc";
    private static final String           DEFAULT_ORDER_TYPE     = ORDER_TYPE_ASC;
    private static final String           DEFAULT_ORDERBY        = "SQL";

    private DruidStatService(){
    }

    public static DruidStatService getInstance() {
        return instance;
    }

    public boolean isResetEnable() {
        return statManagerFacade.isResetEnable();
    }

    public void setResetEnable(boolean value) {
        statManagerFacade.setResetEnable(value);
    }

    public String service(String url) {

        Map<String, String> parameters = getParameters(url);

        if (url.equals("/basic.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, statManagerFacade.returnJSONBasicStat());
        }

        if (url.equals("/reset-all.json")) {
            statManagerFacade.resetAll();

            return returnJSONResult(RESULT_CODE_SUCCESS, null);
        }

        if (url.equals("/log-and-reset.json")) {
            statManagerFacade.logAndResetDataSource();

            return returnJSONResult(RESULT_CODE_SUCCESS, null);
        }

        if (url.equals("/datasource.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, statManagerFacade.getDataSourceStatDataList());
        }

        if (url.equals("/activeConnectionStackTrace.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, statManagerFacade.getActiveConnStackTraceList());
        }

        if (url.startsWith("/datasource-")) {
            Integer id = StringUtils.subStringToInteger(url, "datasource-", ".");
            Object result = statManagerFacade.getDataSourceStatData(id);
            return returnJSONResult(result == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS, result);
        }

        if (url.startsWith("/connectionInfo-") && url.endsWith(".json")) {
            Integer id = StringUtils.subStringToInteger(url, "connectionInfo-", ".");
            List<?> connectionInfoList = statManagerFacade.getPoolingConnectionInfoByDataSourceId(id);
            return returnJSONResult(connectionInfoList == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS,
                                    connectionInfoList);
        }

        if (url.startsWith("/activeConnectionStackTrace-") && url.endsWith(".json")) {
            Integer id = StringUtils.subStringToInteger(url, "activeConnectionStackTrace-", ".");
            return returnJSONActiveConnectionStackTrace(id);
        }

        if (url.startsWith("/sql.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, getSqlStatDataList(parameters));
        }

        if (url.startsWith("/wall.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, getWallStatMap(parameters));
        }

        if (url.startsWith("/wall-") && url.indexOf(".json") > 0) {
            Integer dataSourceId = StringUtils.subStringToInteger(url, "wall-", ".json");
            Object result = statManagerFacade.getWallStatMap(dataSourceId);
            return returnJSONResult(result == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS, result);
        }

        if (url.startsWith("/sql-") && url.indexOf(".json") > 0) {
            Integer id = StringUtils.subStringToInteger(url, "sql-", ".json");
            return getSqlStat(id);
        }

        if (url.startsWith("/weburi.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, getWebURIStatDataList(parameters));
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

    private List<Map<String, Object>> getSpringStatDataList(Map<String, String> parameters) {
        List<Map<String, Object>> array = SpringStatManager.getInstance().getMethodStatData();
        return comparatorOrderBy(array, parameters);
    }

    private List<Map<String, Object>> getWebURIStatDataList(Map<String, String> parameters) {
        List<Map<String, Object>> array = WebAppStatManager.getInstance().getURIStatData();
        return comparatorOrderBy(array, parameters);
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

    private List<Map<String, Object>> getSqlStatDataList(Map<String, String> parameters) {
        Integer dataSourceId = null;

        String dataSourceIdParam = parameters.get("dataSourceId");
        if (dataSourceIdParam != null && dataSourceIdParam.length() > 0) {
            dataSourceId = Integer.parseInt(dataSourceIdParam);
        }

        List<Map<String, Object>> array = statManagerFacade.getSqlStatDataList(dataSourceId);
        List<Map<String, Object>> sortedArray = comparatorOrderBy(array, parameters);
        return sortedArray;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getWallStatMap(Map<String, String> parameters) {
        Integer dataSourceId = null;

        String dataSourceIdParam = parameters.get("dataSourceId");
        if (dataSourceIdParam != null && dataSourceIdParam.length() > 0) {
            dataSourceId = Integer.parseInt(dataSourceIdParam);
        }

        Map<String, Object> result = statManagerFacade.getWallStatMap(dataSourceId);

        if (result != null) {
            List<Map<String, Object>> tables = (List<Map<String, Object>>) result.get("tables");
            if (tables != null) {
                List<Map<String, Object>> sortedArray = comparatorOrderBy(tables, parameters);
                result.put("tables", sortedArray);
            }
            
            List<Map<String, Object>> functions = (List<Map<String, Object>>) result.get("functions");
            if (functions != null) {
                List<Map<String, Object>> sortedArray = comparatorOrderBy(functions, parameters);
                result.put("functions", sortedArray);
            }
        } else {
            result = Collections.emptyMap();
        }

        return result;
    }

    private String getSqlStat(Integer id) {
        Map<String, Object> map = statManagerFacade.getSqlStatData(id);

        if (map == null) {
            return returnJSONResult(RESULT_CODE_ERROR, null);
        }

        DbType dbType = DbType.of((String) map.get("DbType"));
        String sql = (String) map.get("SQL");

        map.put("formattedSql", SQLUtils.format(sql, dbType));
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);

        if (!statementList.isEmpty()) {
            SQLStatement sqlStmt = statementList.get(0);
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
            sqlStmt.accept(visitor);
            map.put("parsedTable", visitor.getTables().toString());
            map.put("parsedFields", visitor.getColumns().toString());
            map.put("parsedConditions", visitor.getConditions().toString());
            map.put("parsedRelationships", visitor.getRelationships().toString());
            map.put("parsedOrderbycolumns", visitor.getOrderByColumns().toString());
        }

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        Date maxTimespanOccurTime = (Date) map.get("MaxTimespanOccurTime");
        if (maxTimespanOccurTime != null) {
            map.put("MaxTimespanOccurTime", format.format(maxTimespanOccurTime));
        }

        return returnJSONResult(map == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS, map);
    }

    private String returnJSONActiveConnectionStackTrace(Integer id) {
        List<String> result = statManagerFacade.getActiveConnectionStackTraceByDataSourceId(id);

        if (result == null) {
            return returnJSONResult(RESULT_CODE_ERROR, "require set removeAbandoned=true");
        }
        return returnJSONResult(RESULT_CODE_SUCCESS, result);
    }

    public static String returnJSONResult(int resultCode, Object content) {
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("ResultCode", resultCode);
        dataMap.put("Content", content);
        return JSONUtils.toJSONString(dataMap);
    }

    public static void registerMBean() {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        try {

            ObjectName objectName = new ObjectName(MBEAN_NAME);
            if (!mbeanServer.isRegistered(objectName)) {
                mbeanServer.registerMBean(instance, objectName);
            }
        } catch (JMException ex) {
            LOG.error("register mbean error", ex);
        }
    }

    public static void unregisterMBean() {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

        try {
            mbeanServer.unregisterMBean(new ObjectName(MBEAN_NAME));
        } catch (JMException ex) {
            LOG.error("unregister mbean error", ex);
        }
    }

    public static Map<String, String> getParameters(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            return Collections.<String, String> emptyMap();
        }

        String parametersStr = StringUtils.subString(url, "?", null);
        if (parametersStr == null || parametersStr.length() == 0) {
            return Collections.<String, String> emptyMap();
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
}
