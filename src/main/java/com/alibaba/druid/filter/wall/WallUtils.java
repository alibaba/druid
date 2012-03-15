package com.alibaba.druid.filter.wall;

import com.alibaba.druid.filter.wall.spi.MySqlWallProvider;
import com.alibaba.druid.filter.wall.spi.OracleWallProvider;

public class WallUtils {

    public static boolean isValidateMySql(String sql) {
        MySqlWallProvider provider = new MySqlWallProvider();
        return provider.check(sql, false);
    }
    
    public static boolean isValidateOracle(String sql) {
        OracleWallProvider provider = new OracleWallProvider();
        return provider.check(sql, false);
    }
}
