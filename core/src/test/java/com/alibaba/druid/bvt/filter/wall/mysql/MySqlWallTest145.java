package com.alibaba.druid.bvt.filter.wall.mysql;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlWallTest145 {
    @Test
    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "SHOW FULL TABLES WHERE Table_type != 'VIEW'";
        assertTrue(
                provider.checkValid(sql)
        );
    }
}
