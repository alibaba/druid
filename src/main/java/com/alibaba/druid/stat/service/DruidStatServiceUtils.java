package com.alibaba.druid.stat.service;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.stat.service.dto.SqlInfo;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class DruidStatServiceUtils {

    private final static Log LOG = LogFactory.getLog(DruidStatServiceUtils.class);

    public static SqlInfo createSqlInfo(JdbcSqlStat sqlStat) {
        SqlInfo info = new SqlInfo();

        String sql = sqlStat.getSql();
        try {
            sql = ParameterizedOutputVisitorUtils.parameterize(sql, sqlStat.getDbType());
        } catch (Exception e) {
            LOG.error("merge sql error", e);
        }

        info.setSql(sql);
        info.setExecuteCount((int) sqlStat.getExecuteCount());
        info.setRunningCount((int) sqlStat.getRunningCount());
        info.setConcurrentMax((int) sqlStat.getConcurrentMax());
        info.setErrorCount((int) sqlStat.getErrorCount());
        info.setInTransactionCount((int) sqlStat.getInTransactionCount());
        info.setFetchRowCount(sqlStat.getFetchRowCount());
        info.setUpdateCount(sqlStat.getUpdateCount());
        info.setResultSetHoldTimeMilis(sqlStat.getResultSetHoldTimeMilis());

        info.setHisogram(sqlStat.getHistogram().toArray());
        info.setExecuteAndResultHoldTimeHistogram(sqlStat.getExecuteAndResultHoldTimeHistogram().toArray());
        info.setFetchRowCountHistogram(sqlStat.getFetchRowCountHistogram().toArray());
        info.setUpdateCountHistogram(sqlStat.getUpdateCountHistogram().toArray());

        return info;
    }
}
