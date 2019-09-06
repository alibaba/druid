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
public class Test3455 {


    @Test
    public void testFunc(){
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
            String sql = "CREATE function getVal() RETURNS int(11) " +
                    "BEGIN " +
                    "    DECLARE VALUE INTEGER; " +
                    "    SET VALUE = 0; " +
                    "    RETURN VALUE; " +
                    "END;";

            pstmt = conn.prepareStatement(sql);
           pstmt.executeUpdate();

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
