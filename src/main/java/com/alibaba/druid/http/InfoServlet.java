package com.alibaba.druid.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.VERSION;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcSqlStat;

public class InfoServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        
        out.println("<body>");
        
        out.println("<table border=\"1\">");
        
        printVersions(out);
        
        printDrivers(out);
        
        printDataSources(out);
        
        out.println("</table>");
        
        printDataSourceDetal(out);
        
        out.println("</body>");
        
        out.println("</html>");
    }

    private void printVersions(PrintWriter out) {
        out.println("<tr><td>Version</td><td>" + VERSION.getVersionNumber() + "</td></tr>");
    }

    private void printDrivers(PrintWriter out) {
        out.print("<tr><td>Drivers</td><td>");
        for (Enumeration<Driver> e = DriverManager.getDrivers();e.hasMoreElements();) {
            Driver driver = e.nextElement();
            out.print(driver.getClass().getName());
            out.print("<br/>");
            out.println();
        }
        
        out.print("</td></tr>");
        out.println();
    }
    
    private void printDataSources(PrintWriter out) {
        out.print("<tr><td>DataSources</td><td>");
        
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            out.println("<a href=\"#" + System.identityHashCode(dataSource) + "\">" + dataSource.getName()  + "</a>");
            out.print("<br/>");
        }
        
        out.print("</td></tr>");
        out.println();
    }
    
    private void printDataSourceDetal(PrintWriter out) {
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            printDataSource(out, dataSource);
        }
    }
    
    private void printDataSource(PrintWriter out, DruidDataSource dataSource) {
        out.println("<a name=\"" + System.identityHashCode(dataSource) + "\"><h1>" + dataSource.getName() + "</h1></a>");
        out.println("<table border=\"1\">");
        out.println("<tr><td>URL</td><td>" + dataSource.getUrl() + "</td></tr>");
        out.println("<tr><td>DbType</td><td>" + dataSource.getDbType() + "</td></tr>");
        out.println("<tr><td>UserName</td><td>" + dataSource.getUsername() + "</td></tr>");
        out.println("<tr><td>DriverClassName</td><td>" + dataSource.getDriverClassName() + "</td></tr>");
        out.println("<tr><td>InitialSize</td><td>" + dataSource.getInitialSize() + "</td></tr>");
        out.println("<tr><td>MinIdle</td><td>" + dataSource.getMinIdle() + "</td></tr>");
        out.println("<tr><td>MaxActive</td><td>" + dataSource.getMaxActive() + "</td></tr>");
        out.println("<tr><td>TestOnBorrow</td><td>" + dataSource.isTestOnBorrow() + "</td></tr>");
        out.println("<tr><td>TestWhileIdle</td><td>" + dataSource.isTestWhileIdle() + "</td></tr>");
        out.println("<tr><td>LogicConnectCount</td><td>" + dataSource.getConnectCount() + "</td></tr>");
        out.println("<tr><td>LogicCloseCount</td><td>" + dataSource.getCloseCount() + "</td></tr>");
        out.println("<tr><td>LogicConnectErrorCount</td><td>" + dataSource.getConnectErrorCount() + "</td></tr>");
        out.println("<tr><td>PhysicalConnectCount</td><td>" + dataSource.getCreateCount() + "</td></tr>");
        out.println("<tr><td>PhysicalCloseCount</td><td>" + dataSource.getDestroyCount() + "</td></tr>");
        out.println("<tr><td>PhysicalConnectErrorCount</td><td>" + dataSource.getCreateErrorCount() + "</td></tr>");
        out.println("<tr><td>PSCacheAccessCount()</td><td>" + dataSource.getCachedPreparedStatementAccessCount() + "</td></tr>");
        out.println("<tr><td>PSCacheHitCount()</td><td>" + dataSource.getCachedPreparedStatementHitCount() + "</td></tr>");
        out.println("<tr><td>PSCacheMissCount()</td><td>" + dataSource.getCachedPreparedStatementMissCount() + "</td></tr>");
        out.println("</table>");
        
        out.println("<a name=\"" + System.identityHashCode(dataSource) + "\"><h2>" + dataSource.getName() + " SQL </h2></a>");
        JdbcDataSourceStat dataSourceStat = dataSource.getDataSourceStat();
        out.println("<table border=\"1\">");
        out.println("<tr>");
        out.println("<td>SQL</td>");
        out.println("<td>File</td>");
        out.println("<td>Name</td>");
        out.println("<td>ExecuteCount</td>");
        out.println("<td>ExecuteMillis</td>");
        out.println("<td>ExecuteMillisMax</td>");
        out.println("<td>InTxnCount</td>");
        out.println("<td>ErrorCount</td>");
        out.println("<td>EffectedRowCount</td>");
        out.println("<td>FetchRowCount</td>");
        out.println("<td>RunningCount</td>");
        out.println("<td>ConcurrentMax</td>");
        out.println("<td>ExecHistogram</td>");
        out.println("<td>FetchRowHistogram</td>");
        out.println("<td>UpdateCountHistogram</td>");
        out.println("<td>ExecAndRsHoldHistogram</td>");
        out.println("</tr>");  
        
        for (JdbcSqlStat sqlStat : dataSourceStat.getSqlStatMap().values()) {
            out.println("<tr>");
            
            out.println("<td>" + sqlStat.getSql() + "</td>");
            if (sqlStat.getFile() != null) {
                out.println("<td>" + sqlStat.getFile() + "</td>");    
            } else {
                out.println("<td></td>");
            }
            if (sqlStat.getName() != null) {
                out.println("<td>" + sqlStat.getName() + "</td>");                
            } else {
                out.println("<td></td>");
            }
            out.println("<td>" + sqlStat.getExecuteCount() + "</td>");
            out.println("<td>" + sqlStat.getExecuteMillisTotal() + "</td>");
            out.println("<td>" + sqlStat.getExecuteMillisMax() + "</td>");
            out.println("<td>" + sqlStat.getInTransactionCount() + "</td>");
            out.println("<td>" + sqlStat.getErrorCount() + "</td>");
            out.println("<td>" + sqlStat.getUpdateCount() + "</td>");
            out.println("<td>" + sqlStat.getFetchRowCount() + "</td>");
            out.println("<td>" + sqlStat.getRunningCount() + "</td>");
            out.println("<td>" + sqlStat.getConcurrentMax() + "</td>");
            out.println("<td>" + sqlStat.getHistogram() + "</td>");
            out.println("<td>" + sqlStat.getFetchRowCountHistogram() + "</td>");
            out.println("<td>" + sqlStat.getUpdateCountHistogram() + "</td>");
            out.println("<td>" + sqlStat.getExecuteAndResultHoldTimeHistogram() + "</td>");
            
            out.println("</tr>");    
        }
        out.println("</table>");
    }
}
