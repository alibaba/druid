package com.alibaba.druid.bvt.hibernate;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import com.alibaba.druid.bvt.hibernate.entity.Sample;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author yinheli [yinheli@gmail.com]
 * @date 2012-11-26 下午11:41
 */
public class HibernateCRUDTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(HibernateCRUDTest.class);

	private DruidDataSource dataSource;

	private SessionFactory sessionFactory;

	@Override
	public void setUp() throws Exception {
		/*init dataSource*/
		dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:h2:file:~/.h2/HibernateCRUDTest;AUTO_SERVER=TRUE");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dataSource.setDefaultAutoCommit(false);
		dataSource.setFilters("log4j");

		/*init sessionFactory*/
		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
		factoryBean.setDataSource(dataSource);

		Properties prop = new Properties();
		prop.put("hibernate.show_sql", "true");
		//prop.put("hibernate.format_sql", "true");
		prop.put("hibernate.hbm2ddl.auto", "create");
		prop.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		factoryBean.setHibernateProperties(prop);
		factoryBean.setAnnotatedClasses(new Class<?>[]{Sample.class});

		try {
			factoryBean.afterPropertiesSet();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sessionFactory = factoryBean.getObject();
	}

	@Override
	public void tearDown() throws Exception {
		sessionFactory.close();
		JdbcUtils.close(dataSource);
	}

	private void doCreate(Session session) {
		Sample sample = new Sample();
		sample.setId(1L);
		sample.setDesc("sample");
		sample.setCreateTime(new Date());
		session.save(sample);
	}

	private void doGet(Session session) {
		Sample sample = (Sample) session.get(Sample.class, 1L);
		log.debug("**sample:{}", sample);
		assert sample != null;
	}

	private void doUpdate(Session session) {
		Sample sample = (Sample) session.get(Sample.class, 1L);
		assert sample != null;
		sample.setDesc("update sample");
		sample.setUpdateTime(new Date());
		session.update(sample);
	}

	private void doDelete(Session session) {
		Sample sample = (Sample) session.get(Sample.class, 1L);
		assert sample != null;
		session.delete(sample);
	}

	/*-------- test start --------*/

	public void test_create() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			doCreate(session);
		} finally {
			if (session != null){
				session.flush();
				session.close();
			}
		}
	}

	public void test_get() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			doCreate(session);
			doGet(session);
		} finally {
			if (session != null){
				session.flush();
				session.close();
			}
		}
	}

	public void test_update() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			doCreate(session);
			doUpdate(session);
		} finally {
			if (session != null){
				session.flush();
				session.close();
			}
		}
	}

	public void test_delete() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			doCreate(session);
			doDelete(session);
		} finally {
			if (session != null){
				session.flush();
				session.close();
			}
		}
	}

	public void test_transactional_create() {
		Session session = null;
		Transaction tran = null;
		try {
			session = sessionFactory.openSession();
			tran = session.beginTransaction();
			doCreate(session);
		} finally {
			if (tran != null) {
				tran.commit();
			}
			if (session != null){
				session.flush();
				session.close();
			}
		}
	}

	public void test_transactional_update() {
		Session session = null;
		Transaction tran = null;
		try {
			session = sessionFactory.openSession();
			tran = session.beginTransaction();
			doCreate(session);
			doUpdate(session);
		} finally {
			if (tran != null) {
				tran.commit();
			}
			if (session != null){
				session.flush();
				session.close();
			}
		}
	}

	public void test_transactional_delete() {
		Session session = null;
		Transaction tran = null;
		try {
			session = sessionFactory.openSession();
			tran = session.beginTransaction();
			doCreate(session);
			doDelete(session);
		} finally {
			if (tran != null) {
				tran.commit();
			}
			if (session != null){
				session.flush();
				session.close();
			}
		}
	}

}