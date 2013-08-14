package com.alibaba.druid.bvt.support.monitor;

import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl.BeanInfo;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl.FieldInfo;
import com.alibaba.druid.util.JdbcConstants;

public class MonitorDaoJdbcImplTest extends TestCase {

    public void testBuildSql() throws Exception {
        MonitorDaoJdbcImpl dao = new MonitorDaoJdbcImpl();

        String sql = dao.buildInsertSql(new BeanInfo(JdbcSqlStatValue.class));

        System.out.println(SQLUtils.format(sql, JdbcConstants.MYSQL));
    }
    
    public String buildInsertSql(MonitorDaoJdbcImpl dao, BeanInfo beanInfo) {
        StringBuilder buf = new StringBuilder();

        buf.append("CREATE TABLE ") //
        .append(dao.getTableName(beanInfo));

        buf.append(" (domain, app, cluster, host, pid, collectTime");
        List<FieldInfo> fields = beanInfo.getFields();
        for (int i = 0; i < fields.size(); ++i) {
            FieldInfo field = fields.get(i);
            buf.append(", ");
            buf.append(field.getColumnName());
        }
        buf.append(")\nVALUES (?, ?, ?, ?, ?, ?");
        for (int i = 0; i < fields.size(); ++i) {
            buf.append(", ?");
        }
        
        buf.append(")");
        
        return buf.toString();
    }
}
