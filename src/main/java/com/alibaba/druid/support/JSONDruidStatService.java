package com.alibaba.druid.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.spring.stat.SpringStatManager;
import com.alibaba.druid.util.MapComparator;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;

/**
 * 注意：避免直接调用Druid相关对象例如DruidDataSource等，相关调用要到DruidStatManagerFacade里用反射实现
 * 
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class JSONDruidStatService {

    private final static JSONDruidStatService instance               = new JSONDruidStatService();

    private static DruidStatManagerFacade     druidStatManager       = DruidStatManagerFacade.getInstance();

    public final static int                   RESULT_CODE_SUCCESS    = 1;
    public final static int                   RESULT_CODE_ERROR      = -1;

    private final static int                  DEFAULT_PAGE           = 1;
    private final static int                  DEFAULT_PER_PAGE_COUNT = Integer.MAX_VALUE;
    private static final String               DEFAULT_ORDER_TYPE     = "asc";
    private static final String               DEFAULT_ORDERBY        = "SQL";

    private JSONDruidStatService(){
    }

    public static JSONDruidStatService getInstance() {
        return instance;
    }

    public String service(String url) {

        Map<String, String> parameters = StringUtils.getParameters(url);

        if (url.equals("/basic.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, druidStatManager.returnJSONBasicStat());
        }

        if (url.equals("/reset-all.json")) {
            druidStatManager.resetAll();
            SpringStatManager.getInstance().resetStat();
            WebAppStatManager.getInstance().resetStat();
            
            return returnJSONResult(RESULT_CODE_SUCCESS, null);
        }

        if (url.equals("/datasource.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, druidStatManager.getDataSourceStatList());
        }

        if (url.startsWith("/datasource-")) {
            Integer id = StringUtils.subStringToInteger(url, "datasource-", ".");
            Object result = druidStatManager.getDataSourceStatData(id);
            return returnJSONResult(result == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS, result);
        }

        if (url.startsWith("/connectionInfo-") && url.endsWith(".json")) {
            Integer id = StringUtils.subStringToInteger(url, "connectionInfo-", ".");
            List<?> connectionInfoList = druidStatManager.getPoolingConnectionInfoByDataSourceId(id);
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

        if (url.startsWith("/sql-") && url.indexOf(".json") > 0) {
            Integer id = StringUtils.subStringToInteger(url, "sql-", ".json");
            return returnJSONSqlInfo(id);
        }
        
        if (url.startsWith("/weburi.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, getWebURIStatDataList(parameters));
        }
        
        if (url.startsWith("/websession.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, getWebSessionStatDataList(parameters));
        }
        
        if (url.startsWith("/spring.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, getSpringStatDataList(parameters));
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
    
    private List<Map<String, Object>> getWebSessionStatDataList(Map<String, String> parameters) {
        List<Map<String, Object>> array = WebAppStatManager.getInstance().getSessionStatData();
        return array;
    }
    
    private List<Map<String,Object>> comparatorOrderBy(List<Map<String, Object>> array,Map<String, String> parameters){
        // when open the stat page before executing some sql
        if (array == null || array.isEmpty()) return null;

        // when parameters is null
        String orderBy, orderType = null;
        Integer page, perPageCount = null;
        if (parameters == null) {
            orderBy = DEFAULT_ORDER_TYPE;
            orderType = DEFAULT_ORDER_TYPE;
            page = DEFAULT_PAGE;
            perPageCount = DEFAULT_PER_PAGE_COUNT;
        } else {
            orderBy = parameters.get("orderBy");
            orderType = parameters.get("orderType");
            page = Integer.parseInt(parameters.get("page"));
            perPageCount = Integer.parseInt(parameters.get("perPageCount"));
        }

        // others,such as order
        orderBy = orderBy == null ? DEFAULT_ORDERBY : orderBy;
        orderType = orderType == null ? DEFAULT_ORDER_TYPE : orderType;
        page = page == null ? DEFAULT_PAGE : page;
        perPageCount = perPageCount == null ? DEFAULT_PER_PAGE_COUNT : perPageCount;

        if (!"desc".equals(orderType)) orderType = DEFAULT_ORDER_TYPE;

        // orderby the statData array
        if (orderBy != null && orderBy.trim().length() != 0) {
            Collections.sort(array, new MapComparator<String, Object>(orderBy, DEFAULT_ORDER_TYPE.equals(orderType)));
        }

        // page
        int fromIndex = (page - 1) * perPageCount;
        int toIndex = page * perPageCount;
        if (toIndex > array.size()) toIndex = array.size();

        return array.subList(fromIndex, toIndex);
    }
    
    private List<Map<String, Object>> getSqlStatDataList(Map<String, String> parameters) {
        List<Map<String, Object>> array = druidStatManager.getSqlStatDataList();
        return comparatorOrderBy(array, parameters);
    }

    private String returnJSONSqlInfo(Integer id) {
        Map<String,Object> map = druidStatManager.getSqlStatData(id);
        if (map == null) {
	        return returnJSONResult(RESULT_CODE_ERROR , null);
        }
        map.put("formattedSql",SQLUtils.format((String) map.get("SQL"),(String) map.get("DbType")));
        List<SQLStatement> statementList = SQLUtils.parseStatements((String) map.get("SQL"), (String) map.get("DbType"));
    
        if (!statementList.isEmpty()) {
            SQLStatement statemen = statementList.get(0);
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(statementList, (String) map.get("DbType"));
            statemen.accept(visitor);
            map.put("parsedTable", visitor.getTables().toString());
            map.put("parsedFields", visitor.getColumns().toString());
            map.put("parsedConditions", visitor.getConditions().toString());
            map.put("parsedRelationships", visitor.getRelationships().toString());
            map.put("parsedOrderbycolumns", visitor.getOrderByColumns().toString());
        }
        
	    DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss:SSS");
	    Date maxTimespanOccurTime = (Date)map.get("MaxTimespanOccurTime");
	    if (maxTimespanOccurTime != null) {
		    map.put("MaxTimespanOccurTime", format.format(maxTimespanOccurTime));
	    }
        return returnJSONResult(map == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS, map);
    }

    private String returnJSONActiveConnectionStackTrace(Integer id) {
        List<String> result = druidStatManager.getActiveConnectionStackTraceByDataSourceId(id);

        if (result == null) {
            return returnJSONResult(RESULT_CODE_ERROR, "require set removeAbandoned=true");
        }
        return returnJSONResult(RESULT_CODE_SUCCESS, result);
    }

    private String returnJSONResult(int resultCode, Object content) {
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("ResultCode", resultCode);
        dataMap.put("Content", content);
        return JSON.toJSONStringWithDateFormat(dataMap, "yyyy-MM-dd hh:mm:ss");
    }

}
