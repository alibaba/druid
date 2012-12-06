package com.alibaba.druid.bvt.console;


import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import junit.framework.TestCase;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.console.DruidStat;
import com.alibaba.druid.support.console.Option;
import com.alibaba.druid.util.JdbcUtils;

public class DruidStatTest extends TestCase {

    private DruidDataSource dataSource;
    private DruidDataSource dataSource2;

    private static String getSelfPid() {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        return pid;
    }

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setMinIdle(1);
        dataSource.setUrl("jdbc:h2:mem:test;");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(600);
        JdbcUtils.execute(dataSource, "CREATE TABLE user (id INT, name VARCHAR(40))");
        JdbcUtils.execute(dataSource, "insert into user values(20,'name1')");
        JdbcUtils.execute(dataSource, "insert into user values(30,'name2')");

        dataSource2 = new DruidDataSource();
        dataSource2.setMinIdle(1);
        dataSource2.setUrl("jdbc:h2:mem:test2;");
        dataSource2.setTestOnBorrow(false);
        dataSource2.setFilters("stat");
        dataSource2.setRemoveAbandoned(true);
        dataSource2.setRemoveAbandonedTimeout(600);

        JdbcUtils.execute(dataSource2, "CREATE TABLE user (id INT, name VARCHAR(40))");
        JdbcUtils.execute(dataSource2, "insert into user values(20,'name1')");

    }

    protected void tearDown() throws Exception {
        JdbcUtils.execute(dataSource, "DROP TABLE user");
        JdbcUtils.close(dataSource);

        JdbcUtils.execute(dataSource2, "DROP TABLE user");
        JdbcUtils.close(dataSource2);
    }

    public void test_printDruidStat() throws Exception {
        String pid = getSelfPid();
        String[] cmdArray = {"-sql", pid};
        Option opt = Option.parseOptions(cmdArray);
        DruidStat.printDruidStat(opt);

        cmdArray = new String[] {"-sql","-id","1", pid};
        opt = Option.parseOptions(cmdArray);
        DruidStat.printDruidStat(opt);

        cmdArray = new String[] {"-sql","-detail", "-id","1", pid};
        opt = Option.parseOptions(cmdArray);
        DruidStat.printDruidStat(opt);

		cmdArray = new String[] {"-ds",pid};
        opt = Option.parseOptions(cmdArray);
		List<Integer> ids = DruidStat.getDataSourceIds(opt);
		opt.setDetailPrint(true);
		opt.setId( ids.get(0).intValue());
        DruidStat.printDruidStat(opt);

		
    }


	public void test_printDruidStat2() throws Exception {
        String pid = getSelfPid();
        String[] cmdArray = {"-act", pid};
        Option opt = Option.parseOptions(cmdArray);

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            stmt.execute("insert into user values(30,'name2')");
            DruidStat.printDruidStat(opt);
        } finally {
            if (stmt != null ) try { stmt.close(); } catch (Exception e) {}
            if (conn != null ) try { conn.close(); } catch (Exception e) {}
        }
    }

    public static void main(String[] args) {
		Result result = JUnitCore.runClasses(DruidStatTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

}
