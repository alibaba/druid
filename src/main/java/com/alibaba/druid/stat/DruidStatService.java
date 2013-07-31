/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
 * @author sandzhang<sandzhangtoo@gmail.com>
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
    private static final String           DEFAULT_ORDER_TYPE     = "asc";
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
             return "{\"ResultCode\":1,\"Content\":{\"checkCount\":31,\"hardCheckCount\":8,\"violationCount\":0,\"violationEffectRowCount\":0,\"blackListHitCount\":0,\"blackListSize\":0,\"whiteListHitCount\":23,\"whiteListSize\":8,\"syntaxErrrorCount\":0,\"tables\":[{\"name\":\"acct_group_permission\",\"selectCount\":9,\"fetchRowCount\":30},{\"name\":\"acct_user_group\",\"selectCount\":4,\"deleteCount\":2,\"insertCount\":3,\"deleteDataCount\":3,\"fetchRowCount\":31},{\"name\":\"acct_user\",\"selectCount\":12,\"fetchRowCount\":50},{\"name\":\"acct_group\",\"selectCount\":8,\"fetchRowCount\":67}],\"functions\":null,\"blackList\":[],\"whiteList\":[{\"sql\":\"SELECT user0.id AS id1_, user0.email AS email1_, user0.login_name AS login3_1_, user0.name AS name1_, user0.password AS password1_\\nFROM acct_user user0_\\nWHERE user0.login_name = ?\\nLIMIT ?\",\"sample\":\"select user0_.id as id1_, user0_.email as email1_, user0_.login_name as login3_1_, user0_.name as name1_, user0_.password as password1_ from acct_user user0_ where user0_.login_name=? limit ?\",\"executeCount\":4,\"fetchRowCount\":4},{\"sql\":\"SELECT grouplist0.user_id AS user1_1_1_, grouplist0.group_id AS group2_1_, group1.id AS id0_0_, group1.name AS name0_0_\\nFROM acct_user_group grouplist0_\\n\\tINNER JOIN acct_group group1_ ON grouplist0.group_id = group1.id\\nWHERE grouplist0.user_id = ?\\nORDER BY group1.id ASC\",\"sample\":\"select grouplist0_.user_id as user1_1_1_, grouplist0_.group_id as group2_1_, group1_.id as id0_0_, group1_.name as name0_0_ from acct_user_group grouplist0_ inner join acct_group group1_ on grouplist0_.group_id=group1_.id where grouplist0_.user_id=? order by group1_.id asc\",\"executeCount\":1,\"fetchRowCount\":5},{\"sql\":\"SELECT permission0.group_id AS group1_0_0_, permission0.permission AS permission0_\\nFROM acct_group_permission permission0_\\nWHERE permission0.group_id = ?\",\"sample\":\"select permission0_.group_id as group1_0_0_, permission0_.permission as permission0_ from acct_group_permission permission0_ where permission0_.group_id=?\",\"executeCount\":9,\"fetchRowCount\":30},{\"sql\":\"SELECT user0.id AS id1_, user0.email AS email1_, user0.login_name AS login3_1_, user0.name AS name1_, user0.password AS password1_\\nFROM acct_user user0_\\nORDER BY user0.id ASC\",\"sample\":\"select user0_.id as id1_, user0_.email as email1_, user0_.login_name as login3_1_, user0_.name as name1_, user0_.password as password1_ from acct_user user0_ order by user0_.id asc\",\"executeCount\":5,\"fetchRowCount\":20},{\"sql\":\"SELECT grouplist0.user_id AS user1_1_1_, grouplist0.group_id AS group2_1_, group1.id AS id0_0_, group1.name AS name0_0_\\nFROM acct_user_group grouplist0_\\n\\tINNER JOIN acct_group group1_ ON grouplist0.group_id = group1.id\\nWHERE grouplist0.user_id IN (SELECT user0.id\\n\\tFROM acct_user user0_)\\nORDER BY group1.id ASC\",\"sample\":\"select grouplist0_.user_id as user1_1_1_, grouplist0_.group_id as group2_1_, group1_.id as id0_0_, group1_.name as name0_0_ from acct_user_group grouplist0_ inner join acct_group group1_ on grouplist0_.group_id=group1_.id where grouplist0_.user_id in (select user0_.id from acct_user user0_ ) order by group1_.id asc\",\"executeCount\":3,\"fetchRowCount\":26},{\"sql\":\"SELECT group0.id AS id0_, group0.name AS name0_\\nFROM acct_group group0_\\nORDER BY group0.id ASC\",\"sample\":\"select group0_.id as id0_, group0_.name as name0_ from acct_group group0_ order by group0_.id asc\",\"executeCount\":4,\"fetchRowCount\":36},{\"sql\":\"delete from acct_user_group where user_id=?\",\"executeCount\":2,\"updateCount\":3},{\"sql\":\"insert into acct_user_group (user_id, group_id) values (?, ?)\",\"executeCount\":3,\"updateCount\":3}]}}";
            //return returnJSONResult(RESULT_CODE_SUCCESS, getWallStatMap(parameters));
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
            String uri = StringUtils.subString(url, "weburi-", ".json");
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
            orderBy = DEFAULT_ORDER_TYPE;
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

        if (!"desc".equals(orderType)) {
            orderType = DEFAULT_ORDER_TYPE;
        }

        // orderby the statData array
        if (orderBy != null && orderBy.trim().length() != 0) {
            Collections.sort(array, new MapComparator<String, Object>(orderBy, DEFAULT_ORDER_TYPE.equals(orderType)));
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
                sortedArray = comparatorOrderBy((List<Map<String, Object>>) result.get("functions"), parameters);
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

        String dbType = (String) map.get("DbType");
        String sql = (String) map.get("SQL");

        map.put("formattedSql", SQLUtils.format(sql, dbType));
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);

        if (!statementList.isEmpty()) {
            SQLStatement statemen = statementList.get(0);
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(statementList, dbType);
            statemen.accept(visitor);
            map.put("parsedTable", visitor.getTables().toString());
            map.put("parsedFields", visitor.getColumns().toString());
            map.put("parsedConditions", visitor.getConditions().toString());
            map.put("parsedRelationships", visitor.getRelationships().toString());
            map.put("parsedOrderbycolumns", visitor.getOrderByColumns().toString());
        }

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss:SSS");
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
