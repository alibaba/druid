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
package com.alibaba.druid;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by admin on 2019/8/24.
 */
public class Test3395 {


    @Test
    public void testCreateTemporaryTables(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/druid3395.properties"));
            DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
            // 获得连接:
            conn = dataSource.getConnection();
            // 编写SQL：
            String sql = "grant CREATE TEMPORARY TABLES   on *.* to mark@localhost";

            //String sql="GRANT ALTER ROUTINE  on *.* to mark@localhost";
           // String sql="CREATE USER 'test5'@'%' IDENTIFIED    by '123456'";
            pstmt = conn.prepareStatement(sql);
            // 执行sql:
           pstmt.executeUpdate();
           // while(rs.next()){
           //     System.out.println(rs.getString("name"));
           // }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(rs!=null){
                try {
                    rs.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Test
    public void testGrantAlterRoutine(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/druid3395.properties"));
            DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
            // 获得连接:
            conn = dataSource.getConnection();
            // 编写SQL：

            String sql="GRANT ALTER ROUTINE  on *.* to mark@localhost";
            // String sql="CREATE USER 'test5'@'%' IDENTIFIED    by '123456'";
            pstmt = conn.prepareStatement(sql);
            // 执行sql:
            pstmt.executeUpdate();
            // while(rs.next()){
            //     System.out.println(rs.getString("name"));
            // }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(rs!=null){
                try {
                    rs.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Test
    public void testCreateUser(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/druid3395.properties"));
            DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
            // 获得连接:
            conn = dataSource.getConnection();
            // 编写SQL：
            String sql="CREATE USER 'test6'@'%' IDENTIFIED    by '123456'";
            pstmt = conn.prepareStatement(sql);
            // 执行sql:
            pstmt.executeUpdate();
            // while(rs.next()){
            //     System.out.println(rs.getString("name"));
            // }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(rs!=null){
                try {
                    rs.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Test
    public void testGrantCreate(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/druid3395.properties"));
            DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
            // 获得连接:
            conn = dataSource.getConnection();
            // 编写SQL：
            String sql="GRANT  CREATE  on *.* to mark@localhost";
            pstmt = conn.prepareStatement(sql);
            // 执行sql:
            pstmt.executeUpdate();
            // while(rs.next()){
            //     System.out.println(rs.getString("name"));
            // }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(rs!=null){
                try {
                    rs.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Test
    public void testGrantAlter(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/druid3395.properties"));
            DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
            // 获得连接:
            conn = dataSource.getConnection();
            // 编写SQL：
            String sql="GRANT  ALTER   on *.* to mark@localhost";
            pstmt = conn.prepareStatement(sql);
            // 执行sql:
            pstmt.executeUpdate();
            // while(rs.next()){
            //     System.out.println(rs.getString("name"));
            // }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(rs!=null){
                try {
                    rs.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
