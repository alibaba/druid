package com.alibaba.druid.bvt.bug;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.AfterClass;
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

import com.alibaba.druid.pool.DruidDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class TestRollBack {
	static ComboPooledDataSource c3p0;
	static DruidDataSource druid;
	static Dao dao_c3p0;
	static Dao dao_druid;
	
	static String url = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
	static String user = "alibaba";
	static String password = "ccbuauto";
	
//  jdbcUrl = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
//  user = "alibaba";
//  password = "ccbuauto";

	@BeforeClass
	public static void init() throws PropertyVetoException, SQLException {
		c3p0 = new ComboPooledDataSource();
		c3p0.setDriverClass("oracle.jdbc.driver.OracleDriver");
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
			dao_c3p0.execute(Sqls.create("create table msg(message varchar2(5))")); // 字段长度5
		}
	}

	@AfterClass
	public static void destory() {
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
		    e.printStackTrace(); // 把这里的异常打印出来
		}
		// abc插了进去,没有回滚
		Assert.assertNotNull(dao_druid.fetch("msg", Cnd.where("message", "=", "abc")));
	}

}
