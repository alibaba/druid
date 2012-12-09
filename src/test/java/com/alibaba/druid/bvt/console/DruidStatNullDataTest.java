package com.alibaba.druid.bvt.console;

import java.lang.management.ManagementFactory;

import junit.framework.TestCase;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.console.DruidStat;
import com.alibaba.druid.support.console.Option;
import com.alibaba.druid.util.JdbcUtils;

public class DruidStatNullDataTest extends TestCase {

    private DruidDataSource dataSource;

    private static String getSelfPid() {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        return pid;
    }

	protected void createDs() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setMinIdle(1);
        dataSource.setUrl("jdbc:h2:mem:test;");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(600);
		//do not execute any sql, just init the datasource.
		dataSource.init();
		
	}

	protected void dispose() throws Exception {
        JdbcUtils.close(dataSource);
    }

	public void test_printDruidStat() throws Exception {
		createDs();
        String pid = getSelfPid();
        String[] cmdArray = {"-sql", pid};
        Option opt = Option.parseOptions(cmdArray);
        DruidStat.printDruidStat(opt);

		cmdArray = new String[] {"-act", pid};
        opt = Option.parseOptions(cmdArray);
        DruidStat.printDruidStat(opt);
		dispose();
	}

    public static void main(String[] args) {
		Result result = JUnitCore.runClasses(DruidStatNullDataTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

	
	
}
