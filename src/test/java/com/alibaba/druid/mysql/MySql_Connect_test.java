package com.alibaba.druid.mysql;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.MySqlUtils;
import com.alibaba.fastjson.util.TypeUtils;
//import com.mysql.jdbc.ConnectionImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by wenshao on 23/07/2017.
 */
public class MySql_Connect_test extends DbTestCase {
    public MySql_Connect_test() {
        super("pool_config/mysql_tddl.properties");
    }

    public void test_oracle_2() throws Exception {
        for (int i = 0; i < 10; ++i) {
            Connection conn = getConnection();

            Statement stmt = conn.createStatement();
            int updateCnt = stmt.executeUpdate("update tb1 set fid = '3' where fid = '4'");
            System.out.println("update : " + updateCnt);

            System.out.println(
                    MySqlUtils.getLastPacketReceivedTimeMs(conn));


            stmt.close();
//        rs.close();

            conn.close();
        }
    }

    public void test_oracle() throws Exception {
        Connection conn = getConnection();

        System.out.println(
                MySqlUtils.getLastPacketReceivedTimeMs(conn));

        //String createTableScript = JdbcUtils.getCreateTableScript(conn, JdbcConstants.MYSQL);
        //System.out.println(createTableScript);

        Statement stmt = conn.createStatement();
        int updateCnt = stmt.executeUpdate("update tb1 set fid = '3' where fid = '4'");
        System.out.println("update : " + updateCnt);

        System.out.println(
                MySqlUtils.getLastPacketReceivedTimeMs(conn));

        Thread.sleep(500);

        System.out.println(
                MySqlUtils.getLastPacketReceivedTimeMs(conn));

        Class<?> class_connImpl_5 = TypeUtils.loadClass("com.mysql.jdbc.ConnectionImpl");
        if (class_connImpl_5 != null) {
            conn.unwrap(class_connImpl_5);
        }
        dataSource.validateConnection(conn);

        System.out.println(
                MySqlUtils.getLastPacketReceivedTimeMs(conn));

        stmt.close();
//        rs.close();

        conn.close();
    }
}
