package com.alibaba.druid.support.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.VERSION;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.IOUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class StatViewServlet extends HttpServlet {

    /**
     * 
     */
    private static final long   serialVersionUID            = 1L;

    private final static int    RESULT_CODE_SUCCESS         = 1;
    private final static int    RESULT_CODE_ERROR           = -1;

    private final static String RESOURCE_PATH               = "support/http/resources";
    private final static String TEMPLATE_PAGE_RESOURCE_PATH = RESOURCE_PATH + "/template.html";

    public String               templatePage;

    public void init() throws ServletException {
        try {
            templatePage = IOUtils.readFromResource(TEMPLATE_PAGE_RESOURCE_PATH);
        } catch (IOException e) {
            throw new ServletException("error read templatePage:" + TEMPLATE_PAGE_RESOURCE_PATH, e);
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        response.setCharacterEncoding("utf-8");

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

        if (path.equals("/reset-all.json")) {
            resetAllStat();
            returnJSONResult(request, response, RESULT_CODE_SUCCESS, null);
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
            if (path.endsWith(".json")) {
                Object result = getJSONSqlStat(id);
                returnJSONResult(request, response, result == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS, result);
                return;
            }

            if (path.endsWith(".html")) {
                JdbcSqlStat sqlStat = getSqlStatById(id);
                returnViewSqlStat(sqlStat, response);
                return;
            }
            return;
        }

        // find file in resources path
        returnResourceFile(path, response);
    }

    private void returnViewSqlStat(JdbcSqlStat sqlStat, HttpServletResponse response) throws IOException {
        if (sqlStat == null) return;

        StringBuilder content = new StringBuilder();

        content.append("<h2>FULL SQL</br></h2> <h4>" + sqlStat.getSql() + "</h4>");
        content.append("<h2>Format View:</h2>");
        content.append("<textarea style='width:99%;height:120px;;border:1px #A8C7CE solid;line-height:20px;font-size:12px;'>");
        content.append(SQLUtils.format(sqlStat.getSql(), sqlStat.getDbType()));
        content.append("</textarea><br>");
        content.append("<p>API:com.alibaba.druid.sql.SQLUtils.format(sql,DBType);</p>");
        content.append("<br>");

        List<SQLStatement> statementList = SQLUtils.parseStatements(sqlStat.getSql(), sqlStat.getDbType());
        if (!statementList.isEmpty()) {
            content.append("<h2>Parse View:</h2>");

            SQLStatement statemen = statementList.get(0);
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(statementList, sqlStat.getDbType());
            statemen.accept(visitor);
            content.append("<table cellpadding='5' cellspacing='1' width='99%'>");
            content.append("<tr>");
            content.append("<td class='td_lable' width='130'>Tables</td>");
            content.append("<td>" + visitor.getTables() + "</td>");
            content.append("</tr>");

            content.append("<tr>");
            content.append("<td class='td_lable'>Fields</td>");
            content.append("<td>" + visitor.getColumns() + "</td>");
            content.append("</tr>");
            content.append("<tr>");
            content.append("<td class='td_lable'>Coditions</td>");
            content.append("<td>" + visitor.getConditions() + "</td>");
            content.append("</tr>");

            content.append("<tr>");
            content.append("<td class='td_lable'>Relationships</td>");
            content.append("<td>" + visitor.getRelationships() + "</td>");
            content.append("</tr>");
            
            content.append("<tr>");
            content.append("<td class='td_lable'>OrderByColumns</td>");
            content.append("<td>" + visitor.getOrderByColumns() + "</td>");
            content.append("</tr>");

            content.append("</table>");

            content.append("<br>");
            content.append("<p>API:</p>");
            content.append("<p>");
            content.append("List<SQLStatement> statementList = SQLUtils.parseStatements(sqlStat.getSql(), sqlStat.getDbType())</br>");
            content.append("SQLStatement statemen = statementList.get(0);</br>");
            content.append("SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(statementList, sqlStat.getDbType());</br>");
            content.append("statemen.accept(visitor);</br>");
            content.append("visitor.getTables() / visitor.getColumns() / visitor.getOrderByColumns() / visitor.getConditions() / visitor.getRelationships()</br>");
            content.append("</p>");
            content.append("<br>");
        }

        response.getWriter().print(mergeTemplatePage("Druid Sql View", content.toString()));

    }

    private void resetAllStat() {
        JdbcStatManager.getInstance().reset();
        DruidDataSourceStatManager.getInstance().reset();
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
        Map<String, Object> json = new LinkedHashMap<String, Object>();
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
        for (DruidDataSource datasource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            if (System.identityHashCode(datasource) == identity) {
                return datasource;
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
        Map<String, Object> json = new LinkedHashMap<String, Object>();

        json.put("ID", sqlStat.getId());
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

        Map<String, Object> json = new LinkedHashMap<String, Object>();
        json.put("Identity", System.identityHashCode(dataSource));
        json.put("Name", dataSource.getName());
        json.put("DbType", dataSource.getDbType());
        json.put("DriverClassName", dataSource.getDriverClassName());

        json.put("URL", dataSource.getUrl());
        json.put("UserName", dataSource.getUsername());
        json.put("FilterClassNames", dataSource.getFilterClassNames());

        json.put("WaitThreadCount", dataSource.getWaitThreadCount());
        json.put("NotEmptyWaitCount", dataSource.getNotEmptyWaitCount());
        json.put("NotEmptyWaitMillis", dataSource.getNotEmptyWaitMillis());

        json.put("PoolingCount", dataSource.getPoolingCount());
        json.put("PoolingPeak", dataSource.getPoolingPeak());
        json.put("PoolingPeakTime", dataSource.getPoolingPeakTime());

        json.put("ActiveCount", dataSource.getActiveCount());
        json.put("ActivePeak", dataSource.getActivePeak());
        json.put("ActivePeakTime", dataSource.getActivePeakTime());

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

        json.put("StartTransactionCount", dataSource.getStartTransactionCount());
        json.put("TransactionHistogramValues", dataSource.getTransactionHistogramValues());
        return json;
    }

    private void returnJSONResult(HttpServletRequest request, HttpServletResponse response, int resultCode,
                                  Object content) throws IOException {
        PrintWriter out = response.getWriter();

        Map<String, Object> json = new LinkedHashMap<String, Object>();
        json.put("ResultCode", resultCode);
        json.put("Content", content);

        out.print(JSON.toJSONString(json));
    }

    private void returnResourceFile(String fileName, HttpServletResponse response) throws ServletException, IOException {
        String text = IOUtils.readFromResource(RESOURCE_PATH + fileName);
        response.getWriter().write(text);
    }

    private String mergeTemplatePage(String title, String content) {
        return templatePage.replaceAll("\\{title\\}", title).replaceAll("\\{content\\}", content);
    }
}
