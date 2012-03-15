package com.alibaba.druid.filter.wall;

import com.alibaba.druid.filter.wall.spi.MySqlWallProvider;

public class WallUtils {

    public static boolean isValidateMySql(String sql) {
        MySqlWallProvider provider = new MySqlWallProvider();
        return provider.check(sql, false);
    }
}
