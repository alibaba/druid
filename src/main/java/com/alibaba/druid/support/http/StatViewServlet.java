package com.alibaba.druid.support.http;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.druid.support.JSONDruidStatService;
import com.alibaba.druid.util.IOUtils;
import com.alibaba.druid.util.StringUtils;

/**
 * 注意：避免直接调用Druid相关对象例如DruidDataSource等，相关调用要到DruidStatManagerFacade里用反射实现
 * 
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class StatViewServlet extends HttpServlet {

    /**
     * 
     */
    private static final long             serialVersionUID            = 1L;

    private final static String           RESOURCE_PATH               = "support/http/resources";
    private final static String           TEMPLATE_PAGE_RESOURCE_PATH = RESOURCE_PATH + "/template.html";

    private static DruidStatManagerFacade druidStatManager            = DruidStatManagerFacade.getInstance();
    private static JSONDruidStatService   jsonDruidStatService        = JSONDruidStatService.getInstance();

    public String                         templatePage;
    private static DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss:SSS");

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

        if (path.length() == 0 || "/".equals(path)) {
            returnResourceFile("/index.html", response);
            return;
        }

        if (path.indexOf(".json") >= 0) {
            String fullUrl = path;
            if (request.getQueryString() != null && request.getQueryString().length() > 0) {
                fullUrl += "?" + request.getQueryString();
            }
            response.getWriter().print(jsonDruidStatService.service(fullUrl));
            return;
        }

        if (path.startsWith("/connectionInfo-") && path.endsWith(".html")) {
            Integer id = StringUtils.subStringToInteger(path, "connectionInfo-", ".");
            returnViewConnectionInfo(id, request, response);
            return;
        }

        if (path.startsWith("/activeConnectionStackTrace-") && path.endsWith(".html")) {
            Integer id = StringUtils.subStringToInteger(path, "activeConnectionStackTrace-", ".");
            returnViewActiveConnectionStackTrace(id, request, response);
            return;
        }

        if (path.startsWith("/sql-") && path.endsWith(".html")) {
            Integer id = StringUtils.subStringToInteger(path, "sql-", ".");
            returnViewSqlStat(druidStatManager.getSqlStatData(id), response);
            return;
        }

        // find file in resources path
        returnResourceFile(path, response);
    }

    private void returnViewActiveConnectionStackTrace(Integer id, HttpServletRequest request,
                                                      HttpServletResponse response) throws IOException {

        String text = IOUtils.readFromResource(RESOURCE_PATH + "/activeConnectionStackTrace.html");
        text = text.replaceAll("\\{datasourceId\\}", id.toString());
        response.getWriter().print(text);
    }

    private void returnViewConnectionInfo(Integer id, HttpServletRequest request, HttpServletResponse response)
                                                                                                               throws IOException {
        String text = IOUtils.readFromResource(RESOURCE_PATH + "/connectionInfo.html");
        text = text.replaceAll("\\{datasourceId\\}", id.toString());
        response.getWriter().print(text);
    }

    private void returnViewSqlStat(Map<String, Object> sqlStat, HttpServletResponse response) throws IOException {
        if (sqlStat == null) return;

        StringBuilder content = new StringBuilder();

        content.append("<h2>FULL SQL</h2> <h4>" + sqlStat.get("SQL") + "</h4>");
        content.append("<h2>Format View:</h2>");
        content.append("<textarea style='width:99%;height:120px;;border:1px #A8C7CE solid;line-height:20px;font-size:12px;'>");
        content.append(SQLUtils.format((String) sqlStat.get("SQL"), (String) sqlStat.get("DbType")));
        content.append("</textarea><br />");
        content.append("<p>API:com.alibaba.druid.sql.SQLUtils.format(sql,DBType);</p>");
        content.append("<br />");
        
		if (sqlStat.get("LastSlowParameters") != null
				&& sqlStat.get("LastSlowParameters").toString().trim().length() > 0) {
			content.append("<h2>LastSlow SQL View:</h2>");
			content.append("<table cellpadding='5' cellspacing='1' width='99%'>");
			content.append("<tr>");
			content.append("<td class='td_lable' width='130'>SlowestSqlOccurTime</td>");
			content.append("<td>" + format.format(sqlStat.get("MaxTimespanOccurTime")) + "</td>");
			content.append("</tr>");
			content.append("<tr>");
			content.append("<td class='td_lable' width='130'>LastSlowParameters</td>");
			content.append("<td>" + sqlStat.get("LastSlowParameters") + "</td>");
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
		}
		
		List<SQLStatement> statementList = SQLUtils.parseStatements((String) sqlStat.get("SQL"),
				(String) sqlStat.get("DbType"));
		
        if (!statementList.isEmpty()) {
            content.append("<h2>Parse View:</h2>");

            SQLStatement statemen = statementList.get(0);
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(statementList, (String) sqlStat.get("DbType"));
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

            content.append("<br />");
            content.append("<p>API:</p>");
            content.append("<p>");
            content.append("List<SQLStatement> statementList = SQLUtils.parseStatements(sqlStat.getSql(), sqlStat.getDbType())<br />");
            content.append("SQLStatement statemen = statementList.get(0);</br>");
            content.append("SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(statementList, sqlStat.getDbType());<br />");
            content.append("statemen.accept(visitor);<br />");
            content.append("visitor.getTables() / visitor.getColumns() / visitor.getOrderByColumns() / visitor.getConditions() / visitor.getRelationships()<br />");
            content.append("</p>");
            content.append("<br />");
        }

        response.getWriter().print(mergeTemplatePage("Druid Sql View", content.toString()));

    }

    private void returnResourceFile(String fileName, HttpServletResponse response) throws ServletException, IOException {
        String text = IOUtils.readFromResource(RESOURCE_PATH + fileName);
        if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
        } else if (fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        response.getWriter().write(text);
    }

    private String mergeTemplatePage(String title, String content) {
        return templatePage.replaceAll("\\{title\\}", title).replaceAll("\\{content\\}", content);
    }
}
