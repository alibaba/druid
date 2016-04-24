package com.alibaba.druid.pvt.support.monitor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.monitor.MonitorClient;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl.BeanInfo;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl.FieldInfo;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.WallFunctionStatValue;
import com.alibaba.druid.wall.WallProviderStatValue;
import com.alibaba.druid.wall.WallSqlStatValue;
import com.alibaba.druid.wall.WallTableStatValue;

public class MonitorDaoJdbcImplTest extends TestCase {

    private DruidDataSource dataSource;

    @Override
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:mem:test");
        //dataSource.setUrl("jdbc:mysql://localhost:3306/druid-monitor");
        dataSource.setUsername("druid");
        dataSource.setPassword("druid");
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setFilters("wall,stat,log4j");
        dataSource.init();
    }

    @Override
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void testBuildSql() throws Exception {
        MonitorDaoJdbcImpl dao = new MonitorDaoJdbcImpl();
        dao.setDataSource(dataSource);

//        System.out.println(buildCreateSql(dao, new BeanInfo(WallProviderStatValue.class)));
//        System.out.println(buildCreateSql(dao, new BeanInfo(WallSqlStatValue.class)));
//        System.out.println(buildCreateSql(dao, new BeanInfo(WallTableStatValue.class)));
//        System.out.println(buildCreateSql(dao, new BeanInfo(WallFunctionStatValue.class)));

//         dao.createTables("mysql");

        MonitorClient client = new MonitorClient();
        client.setDao(dao);
        
        client.checkInst();

        client.collectSql();

        {
            List<JdbcSqlStatValue> sqlList = client.loadSqlList(Collections.<String, Object> emptyMap());
            for (JdbcSqlStatValue sqlStatValue : sqlList) {
                System.out.println(sqlStatValue.getData());
            }
            // Assert.assertEquals(11, sqlList.size());
        }

        client.collectSql();

        {
            List<JdbcSqlStatValue> sqlList = client.loadSqlList(Collections.<String, Object> emptyMap());
            for (JdbcSqlStatValue sqlStatValue : sqlList) {
                System.out.println(sqlStatValue.getData());
            }
            // Assert.assertEquals(14, sqlList.size());
        }
    }

    public String buildCreateSql(MonitorDaoJdbcImpl dao, BeanInfo beanInfo) {
        StringBuilder buf = new StringBuilder();

        buf.append("CREATE TABLE ") //
        .append(dao.getTableName(beanInfo));

        buf.append("( id bigint(20) NOT NULL AUTO_INCREMENT");
        buf.append(", domain varchar(45)  NOT NULL");
        buf.append(", app varchar(45)  NOT NULL");
        buf.append(", cluster varchar(45)  NOT NULL");
        buf.append(", host varchar(128)");
        buf.append(", pid int(10)  NOT NULL");
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
            } else if (field.getFieldType().equals(boolean.class) || field.getFieldType().equals(Boolean.class)) {
                buf.append(" int(1)");
            }
        }
        buf.append(", PRIMARY KEY(id)");
        buf.append(");");

        buf.append("\n\nCREATE INDEX ");
        buf.append(dao.getTableName(beanInfo));
        buf.append("_index ON ");
        buf.append(dao.getTableName(beanInfo));
        buf.append(" (collectTime, domain, app);");
        return buf.toString();
    }
}
