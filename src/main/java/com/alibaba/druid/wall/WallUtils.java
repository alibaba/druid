package com.alibaba.druid.wall;

import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.SQLServerProvider;

public class WallUtils {

    public static boolean isValidateMySql(String sql) {
        MySqlWallProvider provider = new MySqlWallProvider();
        return provider.checkValid(sql);
    }

    public static boolean isValidateMySql(String sql, WallConfig config) {
        MySqlWallProvider provider = new MySqlWallProvider(config);
        return provider.checkValid(sql);
    }

    public static boolean isValidateOracle(String sql) {
        OracleWallProvider provider = new OracleWallProvider();
        return provider.checkValid(sql);
    }

    public static boolean isValidateOracle(String sql, WallConfig config) {
        OracleWallProvider provider = new OracleWallProvider(config);
        return provider.checkValid(sql);
    }
    
    public static boolean isValidateSqlServer(String sql) {
        SQLServerProvider provider = new SQLServerProvider();
        return provider.checkValid(sql);
    }
    
    public static boolean isValidateSqlServer(String sql, WallConfig config) {
        SQLServerProvider provider = new SQLServerProvider(config);
        return provider.checkValid(sql);
    }
}
