package com.alibaba.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class TestRollBack {
	static ComboPooledDataSource c3p0;
	static DruidDataSource druid;
	static Dao dao_c3p0;
	static Dao dao_druid;
	
//	static String url = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
//	static String user = "alibaba";
//	static String password = "ccbuauto";
//	static String driver = "oracle.jdbc.driver.OracleDriver";
	        
	static String url = "jdbc:jtds:sqlserver://a.b.c.d:1433/druid_db";
	static String user = "sa";
	static String password = "hello123";
	static String driver = "net.sourceforge.jtds.jdbc.Driver";
	
//  jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
//  user = "alibaba";
//  password = "ccbuauto";

	@BeforeClass
	public static void init() throws PropertyVetoException, SQLException {
		c3p0 = new ComboPooledDataSource();
		//c3p0.setDriverClass("oracle.jdbc.driver.OracleDriver");
		c3p0.setDriverClass(driver);
		c3p0.setJdbcUrl(url);
		c3p0.setUser(user);
		c3p0.setPassword(password);

		druid = new DruidDataSource();
		druid.setUrl(url);
		druid.setUsername(user);
		druid.setPassword(password);
		druid.setFilters("stat,trace,encoding");
		druid.setDefaultAutoCommit(false);

		dao_c3p0 = new NutDao(c3p0);
		dao_druid = new NutDao(druid);
		if (!dao_c3p0.exists("msg")) {
			dao_c3p0.execute(Sqls.create("create table msg(message varchar(5))")); // 字段长度5
		}
	}

	@AfterClass
	public static void destroy() {
		c3p0.close();
		druid.close();
	}

	@Before
	public void before() {
		// 清空所有数据
		dao_c3p0.clear("msg");
	}

	@Test
	public void test_c3p0() {
		try {
			// 将两条插入语句包裹在一个事务内执行,第一条可以正常插入,第二条超过字段长度,会抛异常,事务会回滚
			Trans.exec(new Atom() {
				@Override
				public void run() {
					dao_c3p0.insert("msg", Chain.make("message", "abc"));
					dao_c3p0.insert("msg", Chain.make("message", "1234567"));
				}
			});
		} catch (Exception e) {
		    
		}
		// abc也跟着回滚了
		Assert.assertNull(dao_c3p0.fetch("msg", Cnd.where("message", "=", "abc")));
	}

	@Test
	public void test_druid() {
		try {
			Trans.exec(new Atom() {
				@Override
				public void run() {
					dao_druid.insert("msg", Chain.make("message", "abc"));
					dao_druid.insert("msg", Chain.make("message", "1234567"));
				}
			});
		} catch (Exception e) {
		    // e.printStackTrace(); // 把这里的异常打印出来
		}
		// abc插了进去,没有回滚
		Assert.assertNotNull(dao_druid.fetch("msg", Cnd.where("message", "=", "abc")));
	}

}
