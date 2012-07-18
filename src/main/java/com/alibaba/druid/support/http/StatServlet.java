package com.alibaba.druid.support.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.VERSION;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.IOUtils;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class StatServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID    = 1L;

    private final static int  RESULT_CODE_SUCCESS = 1;
    private final static int  RESULT_CODE_ERROR   = -1;

    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestPath = req.getPathInfo();

        if (requestPath.startsWith("/json/basic")) {
            returnJSON_BasicStat(req, resp);
            return;
        }
        if (requestPath.startsWith("/json/datasource")) {
            returnJSON_DataSourceStat(req, resp);
            return;
        }
        if (requestPath.startsWith("/json/sql")) {
            returnJSON_DataSourceSqlStat(req, resp);
            return;
        }
        // find file in jar resources path
        returnResourceFile(requestPath, resp);
    }

    private JSONArray getJSONDrivers() {
        JSONArray drivers = new JSONArray();
        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
            Driver driver = e.nextElement();
            drivers.add(driver.getClass().getName());
        }
        return drivers;
    }

    private void returnJSON_BasicStat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Version", VERSION.getVersionNumber());
        json.put("Drivers", getJSONDrivers());
        json.put("DataSources", getJSONDataSources());

        returnJSONResult(req, resp, RESULT_CODE_SUCCESS, json);

    }

    private void returnJSON_DataSourceStat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer identity = null;
        try {
            identity = Integer.parseInt(req.getParameter("identity"));
        } catch (Exception e) {
        }
        JSONObject json = getJSONDataSourceStat(identity);
        if (identity == null) {
            returnJSONResult(req, resp, RESULT_CODE_ERROR, null);
        } else {
            returnJSONResult(req, resp, RESULT_CODE_SUCCESS, json);
        }

    }

    private void returnJSON_DataSourceSqlStat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer identity = null;
        try {
            identity = Integer.parseInt(req.getParameter("identity"));
        } catch (Exception e) {
        }
        JSONArray re = getJSONDataSourceSqlStat(identity);
        if (identity == null || re == null) {
            returnJSONResult(req, resp, RESULT_CODE_ERROR, null);
        } else {
            returnJSONResult(req, resp, RESULT_CODE_SUCCESS, re);
        }

    }

    private JSONArray getJSONDataSources() {
        JSONArray drivers = new JSONArray();
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            drivers.add(getJSONDataSource(dataSource));
        }
        return drivers;
    }

    private JSONObject getJSONDataSourceStat(Integer id) {
        if (id == null) {
            return null;
        }
        DruidDataSource ds = getDruidDataSourceById(id);
        if (ds == null) {
            return null;
        }

        return getJSONDataSource(ds);
    }

    private DruidDataSource getDruidDataSourceById(Integer identity) {
        if (identity == null) {
            return null;
        }
        for (DruidDataSource ds : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            if (System.identityHashCode(ds) == identity) {
                return ds;
            }
        }
        return null;
    }

    private JSONArray getJSONDataSourceSqlStat(Integer id) {
        if (id == null) {
            return null;
        }
        DruidDataSource ds = getDruidDataSourceById(id);
        if (ds == null) {
            return null;
        }
        JSONArray array = new JSONArray();
        for (JdbcSqlStat sqlStat : ds.getDataSourceStat().getSqlStatMap().values()) {
            JSONObject json = new JSONObject();
            json.put("SQL", sqlStat.getSql());
            json.put("File", sqlStat.getFile());
            json.put("Name", sqlStat.getName());
            json.put("ExecuteCount", sqlStat.getExecuteCount());
            json.put("ExecuteMillisTotal", sqlStat.getExecuteMillisTotal());
            json.put("ExecuteMillisMax", sqlStat.getExecuteMillisMax());
            json.put("InTxnCount", sqlStat.getInTransactionCount());
            json.put("ErrorCount", sqlStat.getErrorCount());
            json.put("UpdateCount", sqlStat.getUpdateCount());
            json.put("FetchRowCount", sqlStat.getFetchRowCount());
            json.put("RunningCount", sqlStat.getRunningCount());
            json.put("ConcurrentMax", sqlStat.getConcurrentMax());
            json.put("ExecHistogram", sqlStat.getHistogram().toArray());
            json.put("FetchRowHistogram", sqlStat.getFetchRowCountHistogram().toArray());
            json.put("UpdateCountHistogram", sqlStat.getUpdateCountHistogram().toArray());
            json.put("ExecAndRsHoldHistogram", sqlStat.getExecuteAndResultHoldTimeHistogram().toArray());

            array.add(json);
        }
        return array;
    }

    private Map<String, Object> getJSONDataSource(DruidDataSource dataSource) {

        Map<String, Object> json = new HashMap<String, Object>();
        json.put("Identity", System.identityHashCode(dataSource));
        json.put("Name", dataSource.getName());
        json.put("URL", dataSource.getUrl());
        json.put("DbType", dataSource.getDbType());
        json.put("UserName", dataSource.getUsername());
        json.put("DriverClassName", dataSource.getDriverClassName());
        json.put("InitialSize", dataSource.getInitialSize());
        json.put("MinIdle", dataSource.getMinIdle());
        json.put("MaxActive", dataSource.getMaxActive());
        json.put("TestOnBorrow", dataSource.isTestOnBorrow());
        json.put("TestWhileIdle", dataSource.isTestWhileIdle());
        json.put("LogicConnectCount", dataSource.getConnectCount());
        json.put("LogicCloseCount", dataSource.getCloseCount());
        json.put("LogicConnectErrorCount", dataSource.getConnectErrorCount());
        json.put("PhysicalConnectCount", dataSource.getCreateCount());
        json.put("PhysicalCloseCount", dataSource.getDestroyCount());
        json.put("PhysicalConnectErrorCount", dataSource.getCreateErrorCount());
        json.put("PSCacheAccessCount", dataSource.getCachedPreparedStatementAccessCount());
        json.put("PSCacheHitCount", dataSource.getCachedPreparedStatementHitCount());
        json.put("PSCacheMissCount", dataSource.getCachedPreparedStatementMissCount());
        return json;
    }

    private void returnJSONResult(HttpServletRequest req, HttpServletResponse resp, int resultCode, Object content)
                                                                                                                  throws IOException {
        PrintWriter out = resp.getWriter();

        Map<String, Object> json = new HashMap<String, Object>();
        json.put("ResultCode", resultCode);
        json.put("Content", content);
        
        String jsonString = JSON.toJSONString(json);
        out.print(jsonString);
    }

    private void returnResourceFile(String fileName, HttpServletResponse response) throws ServletException, IOException {
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream("support/http/resources" + fileName);
            if (in == null) {
                return;
            }

            String text = IOUtils.read(in);
            response.getWriter().write(text);
        } catch (IOException e) {
            throw new ServletException("error when response static file: " + fileName, e);
        } finally {
            JdbcUtils.close(in);
        }
    }
}
