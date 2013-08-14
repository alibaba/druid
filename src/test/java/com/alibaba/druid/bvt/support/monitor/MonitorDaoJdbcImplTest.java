package com.alibaba.druid.bvt.support.monitor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.monitor.MonitorClient;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl.BeanInfo;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl.FieldInfo;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

public class MonitorDaoJdbcImplTest extends TestCase {

    private DruidDataSource dataSource;

    @Override
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:mem:test");
        dataSource.setInitialSize(2);
        dataSource.setMinIdle(2);
        dataSource.setFilters("stat,log4j");
        dataSource.init();
    }

    @Override
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void testBuildSql() throws Exception {
        MonitorDaoJdbcImpl dao = new MonitorDaoJdbcImpl();
        dao.setDataSource(dataSource);

        MonitorClient client = new MonitorClient();
        client.setDao(dao);
        
        {
            String sql = buildCreateSql(dao, new BeanInfo(JdbcSqlStatValue.class));
            System.out.println(SQLUtils.format(sql, JdbcConstants.MYSQL));
            JdbcUtils.execute(dataSource, sql, Collections.emptyList());
        }

        client.collectSql();

    }

    public String buildCreateSql(MonitorDaoJdbcImpl dao, BeanInfo beanInfo) {
        StringBuilder buf = new StringBuilder();

        buf.append("CREATE TABLE ") //
        .append(dao.getTableName(beanInfo));

        buf.append("( id bigint(20) NOT NULL AUTO_INCREMENT");
        buf.append(", domain varchar(45)");
        buf.append(", app varchar(45)");
        buf.append(", cluster varchar(45)");
        buf.append(", host varchar(128)");
        buf.append(", pid int(10)");
        buf.append(", collectTime datetime NOT NULL");
        List<FieldInfo> fields = beanInfo.getFields();
        for (int i = 0; i < fields.size(); ++i) {
            FieldInfo field = fields.get(i);
            buf.append(", ");
            buf.append(field.getColumnName());
            if (field.getFieldType().equals(int.class) || field.getFieldType().equals(Integer.class)) {
                buf.append(" int(10)");
            } else if (field.getFieldType().equals(long.class) || field.getFieldType().equals(Long.class)) {
                buf.append(" bigint(20)");
            } else if (field.getFieldType().equals(String.class)) {
                buf.append(" varchar(256)");
            } else if (field.getFieldType().equals(Date.class)) {
                buf.append(" datetime");
            }
        }
        buf.append(", PRIMARY KEY(id)");
        buf.append(")");

        return buf.toString();
    }
}
