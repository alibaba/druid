package com.alibaba.druid.filter.wall;

import java.util.List;

import com.alibaba.druid.filter.wall.spi.MySqlWallProvider;
import com.alibaba.druid.filter.wall.spi.OracleWallProvider;

public class WallUtils {

    public static boolean isValidateMySql(String sql) {
        MySqlWallProvider provider = new MySqlWallProvider();
        List<Violation> violations =  provider.check(sql);
        return violations.size() == 0;
    }

    public static boolean isValidateMySql(String sql, WallConfig config) {
        MySqlWallProvider provider = new MySqlWallProvider(config);
        List<Violation> violations =  provider.check(sql);
        return violations.size() == 0;
    }

    public static boolean isValidateOracle(String sql) {
        OracleWallProvider provider = new OracleWallProvider();
        List<Violation> violations =  provider.check(sql);
        return violations.size() == 0;
    }

    public static boolean isValidateOracle(String sql, WallConfig config) {
        OracleWallProvider provider = new OracleWallProvider(config);
        List<Violation> violations =  provider.check(sql);
        return violations.size() == 0;
    }
}
