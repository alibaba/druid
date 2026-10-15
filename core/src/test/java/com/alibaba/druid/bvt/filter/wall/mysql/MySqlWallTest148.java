package com.alibaba.druid.bvt.filter.wall.mysql;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlWallTest148 {
    @Test
    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "select * from TABLENAME cfgdatasou0_ where cfgdatasou0_.type=? and cfgdatasou0_.module_name=? and cfgdatasou0_.node_type=? or cfgdatasou0_.type=? and cfgdatasou0_.module_name=? and cfgdatasou0_.node_type=? or cfgdatasou0_.type=? and cfgdatasou0_.module_name=? and cfgdatasou0_.node_type=?";

        assertTrue(
                provider.checkValid(sql)
        );
    }
}
