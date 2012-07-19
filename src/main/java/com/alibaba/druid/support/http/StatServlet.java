package com.alibaba.druid.support.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;

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

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        if (contextPath == null) { // root context
            contextPath = "";
        }

        String path = requestURI.substring(contextPath.length() + servletPath.length());

        if (path.length() == 0) {
            returnResourceFile("/index.html", response);
            return;
        }

        if (path.equals("/basic.json")) {
            returnJSONBasicStat(request, response);
            return;
        }

        if (path.equals("/datasource.html")) {
            returnResourceFile(path, response);
            return;
        }
        if (path.equals("/datasource.json")) {
            returnJSONResult(request, response, RESULT_CODE_SUCCESS, getJSONDataSourceStatList());
            return;
        }
        if (path.startsWith("/datasource-")) {
            Integer id = StringUtils.subStringToInteger(path, "datasource-", ".");
            Object result = getJSONDataSourceStat(id);
            returnJSONResult(request, response, result == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS, result);
            return;
        }

        if (path.equals("/sql.json")) {
            returnJSONResult(request, response, RESULT_CODE_SUCCESS, getJSONSqlStat());
            return;
        }
        if (path.startsWith("/sql-")) {
            Integer id = StringUtils.subStringToInteger(path, "sql-", ".");
            Object result = getJSONSqlStat(id);
            returnJSONResult(request, response, result == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS, result);
            return;
        }

        // find file in resources path
        returnResourceFile(path, response);
    }

    private List<String> getJSONDrivers() {
        List<String> drivers = new ArrayList<String>();
        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
            Driver driver = e.nextElement();
            drivers.add(driver.getClass().getName());
        }
        return drivers;
    }

    private void returnJSONBasicStat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("Version", VERSION.getVersionNumber());
        json.put("Drivers", getJSONDrivers());

        returnJSONResult(request, response, RESULT_CODE_SUCCESS, json);

    }

    private List<Object> getJSONDataSourceStatList() {
        List<Object> datasourceList = new ArrayList<Object>();
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            datasourceList.add(toJSONDataSource(dataSource));
        }
        return datasourceList;
    }

    private Map<String, Object> getJSONDataSourceStat(Integer id) {
        if (id == null) {
            return null;
        }
        DruidDataSource datasource = getDruidDataSourceById(id);
        return datasource == null ? null : toJSONDataSource(datasource);
    }

    private Map<String, Object> getJSONSqlStat(Integer id) {
        if (id == null) {
            return null;
        }
        JdbcSqlStat sqlStat = getSqlStatById(id);
        return sqlStat == null ? null : toJSONSqlStat(sqlStat);
    }

    private JdbcSqlStat getSqlStatById(Integer id) {
        for (DruidDataSource ds : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            JdbcSqlStat sqlStat = ds.getDataSourceStat().getSqlStat(id);
            if (sqlStat != null) return sqlStat;
        }
        return null;
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

    private List<Object> getJSONSqlStat() {
        List<Object> array = new ArrayList<Object>();
        for (DruidDataSource datasource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            for (JdbcSqlStat sqlStat : datasource.getDataSourceStat().getSqlStatMap().values()) {
                array.add(toJSONSqlStat(sqlStat));
            }
        }
        return array;
    }

    private Map<String, Object> toJSONSqlStat(JdbcSqlStat sqlStat) {
        Map<String, Object> json = new HashMap<String, Object>();
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

        return json;
    }

    private Map<String, Object> toJSONDataSource(DruidDataSource dataSource) {

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

    private void returnJSONResult(HttpServletRequest request, HttpServletResponse response, int resultCode,
                                  Object content) throws IOException {
        PrintWriter out = response.getWriter();

        Map<String, Object> json = new HashMap<String, Object>();
        json.put("ResultCode", resultCode);
        json.put("Content", content);

        out.print(JSON.toJSONString(json));
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
