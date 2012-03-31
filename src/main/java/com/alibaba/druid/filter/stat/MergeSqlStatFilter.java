package com.alibaba.druid.filter.stat;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.stat.JdbcSqlStat;

public class MergeSqlStatFilter extends StatFilter {

    private final static Log LOG = LogFactory.getLog(MergeSqlStatFilter.class);

    private String           dbType;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    @Override
    public void init(DataSourceProxy dataSource) {
        super.init(dataSource);

        if (this.dbType == null || this.dbType.trim().length() == 0) {
            this.dbType = dataSource.getDbType();
        }
    }

    public JdbcSqlStat getSqlStat(String sql) {
        try {
            sql = ParameterizedOutputVisitorUtils.parameterize(sql, sql);
        } catch (Exception e) {
            LOG.error("merge sql error", e);
        }
        return dataSourceStat.getSqlStat(sql);
    }
}
