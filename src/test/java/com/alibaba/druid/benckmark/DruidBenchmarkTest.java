package com.alibaba.druid.benckmark;

import junit.framework.TestCase;

import com.alibaba.druid.benckmark.sqlcase.*;
import com.alibaba.druid.benckmark.sqlcase.dragoon.*;

public class DruidBenchmarkTest extends TestCase {
	public void test_druid_benchmark() throws Exception {
		
		BenchmarkExecutor executor = new BenchmarkExecutor();
		executor.getSqlExecutors().add(createExecutorDirect());
		executor.getSqlExecutors().add(createExecutorDruid());
		
		executor.setExecuteCount(10);
		executor.setLoopCount(1000 * 100);
		executor.getCaseList().add(new SelectNow());
		//executor.getCaseList().add(new SelectSysUser());
		//executor.getCaseList().add(new Select1());
		//executor.getCaseList().add(new SelectEmptyTable());
		
		executor.execute();
	}
	
	public DirectSQLExecutor createExecutorDirect() {
		String name = "direct";
		String jdbcUrl = "jdbc:mysql://10.20.129.146/dragoon_v25masterdb?useUnicode=true&characterEncoding=UTF-8";
		String user = "dragoon25";
		String password = "dragoon25";
		return new DirectSQLExecutor(name, jdbcUrl, user, password);
	}
	
	public DirectSQLExecutor createExecutorDruid() {
		String name = "druid";
		String jdbcUrl = "jdbc:wrap-jdbc:filters=default:name=benchmark:jdbc:mysql://10.20.129.146/dragoon_v25masterdb?useUnicode=true&characterEncoding=UTF-8";
		String user = "dragoon25";
		String password = "dragoon25";
		return new DirectSQLExecutor(name, jdbcUrl, user, password);
	}
}
