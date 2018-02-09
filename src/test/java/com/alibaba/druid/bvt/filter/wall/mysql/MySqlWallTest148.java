package com.alibaba.druid.bvt.filter.wall.mysql;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;
import org.junit.Assert;

public class MySqlWallTest148 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        String sql = "select * from TABLENAME cfgdatasou0_ where cfgdatasou0_.type=? and cfgdatasou0_.module_name=? and cfgdatasou0_.node_type=? or cfgdatasou0_.type=? and cfgdatasou0_.module_name=? and cfgdatasou0_.node_type=? or cfgdatasou0_.type=? and cfgdatasou0_.module_name=? and cfgdatasou0_.node_type=?";

        Assert.assertTrue(
                provider.checkValid(sql)
        );
    }
}
