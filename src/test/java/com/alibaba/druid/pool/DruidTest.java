/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.pool;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class DruidTest {

    private static DruidDataSource DS;

    public DruidTest(String connectURI){
        initDS(connectURI);
    }

    public DruidTest(String connectURI, String username, String pswd, String driverClass, int initialSize,
                     int maxActive, int maxIdle, int minIdle, int maxWait){
        initDS(connectURI, username, pswd, driverClass, initialSize, maxActive, minIdle, maxIdle, maxWait);
    }

    public Connection getConn() {
        Connection con = null;
        if (DS != null) {
            try {
                con = DS.getConnection();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }

            try {
                con.setAutoCommit(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return con;
        }
        return con;
    }

    public static void initDS(String connectURI, String username, String pswd, String driverClass, int initialSize,
                              int maxActive, int maxIdle, int minIdle, int maxWait) {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driverClass);
        ds.setUsername(username);
        ds.setPassword(pswd);
        ds.setUrl(connectURI);
        ds.setInitialSize(initialSize); // 初始的连接数；
        ds.setMaxActive(maxActive);
        ds.setMaxIdle(maxIdle);
        ds.setMinIdle(minIdle);
        ds.setMaxWait(maxWait);
        DS = ds;
    }

    public static void initDS(String connectURI) {
        initDS(connectURI, "root", "12345", "com.mysql.jdbc.Driver", 40, 40, 40, 10, 5);
    }

    public static void main(String[] args) throws IOException, SQLException {

        DruidTest db = new DruidTest("jdbc:mysql://a.b.c.d:8066/amoeba");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        FileWriter fileWriter = new FileWriter("D:\\data.txt");
        try {
            conn = db.getConn();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        long sum = 0;
        for (int i = 1; i < 10; i++) {
            try {
                stmt = conn.createStatement();
                Date start = new Date();
                rs = stmt.executeQuery("select * from offer where member_id = 'forwd251'");
                Date end = new Date();
                sum = sum + (end.getTime() - start.getTime());
                fileWriter.write(String.valueOf((end.getTime() - start.getTime())));
                fileWriter.write("/\n");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println((float) sum / 10);

        conn.close();
        stmt.close();
        rs.close();
        fileWriter.flush();
        fileWriter.close();
    }

}
