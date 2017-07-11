package com.alibaba.druid.bvt;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by wenshao on 06/07/2017.
 */
public class ApacheDBUtilsTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://rm-bp1n325y4m6h78xt3.mysql.rds.aliyuncs.com:3306/oracle_info?allowMultiQueries=true&characterEncoding=UTF8");
        dataSource.setUsername("user1");
        dataSource.setPassword("ADAM2d3fb107bcda629fc");

//        druid.url=jdbc:mysql://rm-bp1n325y4m6h78xt3.mysql.rds.aliyuncs.com:3306/oracle_info?allowMultiQueries=true&characterEncoding=UTF8
//        druid.username=user1
//        druid.password=ADAM2d3fb107bcda629fc
    }

    public void test_txn() throws Exception {
        Connection conn = dataSource.getConnection();
        Connection conn3 = dataSource.getConnection();

        System.out.println("1:" + conn);
        QueryRunner runner = new QueryRunner();
        String sql = "select task_ida from worker limit 1";
        try {
            runner.query(conn, sql, new MapHandler());
            //Long count = new PgCopy().pipeline(conn, sql, conn3, "worker_0607", "task_id");
        } catch (Exception e) {
            e.printStackTrace();
            //DbUtils.closeQuietly(conn);//直接关闭close
            DbUtils.rollbackAndCloseQuietly(conn);
            DbUtils.commitAndCloseQuietly(conn);
        }


        //会抛sql.SQLException: ERROR: current transaction is aborted, commands ignored until end of transaction block Query:
        try {
            Connection conn1 = dataSource.getConnection();
//            conn1.setAutoCommit(true);
            System.out.println("2:" + conn1);
            String sql1 = "select * from worker limit 1";
            QueryRunner runner1 = new QueryRunner();
            runner1.query(conn1, sql1, new MapHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }



        //恢复正常
        try {
            Connection conn1 = dataSource.getConnection();
            System.out.println("3:" + conn1);
            String sql1 = "select * from worker limit 2";
            QueryRunner runner1 = new QueryRunner();
            runner1.query(conn1, sql1, new MapHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }
}
