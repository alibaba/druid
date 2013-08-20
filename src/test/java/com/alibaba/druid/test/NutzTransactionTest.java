package com.alibaba.druid.test;

import javax.sql.DataSource;

import org.junit.Assert;
import junit.framework.TestCase;

import org.nutz.dao.Chain;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import com.alibaba.druid.pool.DruidDataSource;

public class NutzTransactionTest extends TestCase {

    private DataSource dataSource;

    protected void setUp() throws Exception {
         DruidDataSource dataSource = new DruidDataSource();
         dataSource.setUrl("jdbc:jtds:sqlserver://192.168.1.105/petstore");
         dataSource.setUsername("sa");
         dataSource.setPassword("hello");
         dataSource.setFilters("log4j");

//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
//        dataSource.setUrl("jdbc:jtds:sqlserver://192.168.1.105/petstore");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("hello");

        this.dataSource = dataSource;
    }

    public void test_trans() throws Exception {
        Dao dao = new NutDao(dataSource);

        dao.clear("test");
        // doTran1(dao);
        doTran2(dao);
    }

    void doTran1(final Dao dao) {
        try {
            Trans.exec(new Atom() {

                @Override
                public void run() {
                    dao.insert("[test]", Chain.make("name", "1"));
                    throw new RuntimeException();
                }
            });
        } catch (Exception e) {
        }
        Assert.assertEquals(0, dao.count("[test]"));
    }

    void doTran2(final Dao dao) {
        try {
            Trans.exec(new Atom() {

                @Override
                public void run() {
                    dao.insert("[test]", Chain.make("name", "1"));
                    dao.insert("[test]", Chain.make("name", "111111111111111111111111111111"));
                }
            });
        } catch (Exception e) {
            // e.printStackTrace();
        }
        Assert.assertEquals(0, dao.count("[test]"));
    }

}
