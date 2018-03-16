package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUpdateCheckHandler;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;

import java.util.List;
import java.util.Properties;

/**
 * Created by wenshao on 13/08/2017.
 */
public class WallUpdateCheckTest extends TestCase {
    private MySqlWallProvider wallProvider = new MySqlWallProvider();

    protected void setUp() throws Exception {
        Properties properties = new Properties();
        properties.put("druid.wall.updateCheckColumns", "t_orders.status");
        wallProvider.getConfig().configFromProperties(properties);
    }

    public void test_update_check_handler() throws Exception {
        {
            WallCheckResult result = wallProvider.check("update t_orders set status = 3 where id = 3 and status = 4");
            assertTrue(result.getViolations().size() == 0);
        }
        wallProvider.getConfig().setUpdateCheckHandler(new WallUpdateCheckHandler() {

            @Override
            public boolean check(String table, String column, Object setValue, List<Object> filterValues) {
                return false;
            }
        });
        {
            WallCheckResult result = wallProvider.check("update t_orders set status = 3 where id = 3 and status = 4");
            assertTrue(result.getViolations().size() > 0);
        }
        wallProvider.getConfig().setUpdateCheckHandler(new WallUpdateCheckHandler() {

            @Override
            public boolean check(String table, String column, Object setValue, List<Object> filterValues) {
                return true;
            }
        });
        {
            WallCheckResult result = wallProvider.check("update t_orders set status = 3 where id = 3 and status = 4");
            assertTrue(result.getViolations().size() == 0);
        }
        assertEquals(0, wallProvider.getWhiteListHitCount());
        assertEquals(0, wallProvider.getBlackListHitCount());
        wallProvider.getConfig().setUpdateCheckHandler(new WallUpdateCheckHandler() {

            @Override
            public boolean check(String table, String column, Object setValue, List<Object> filterValues) {
                //增加对in语句的支持， status in (1, 2) 应该返回的filterValue为1和2
                assertTrue(filterValues.size() == 2);
                return true;
            }
        });
        {
            WallCheckResult result = wallProvider.check("update t_orders set status = 3 where id = 3 and status in (1, 2)");
            assertTrue(result.getViolations().size() == 0);
        }
        {
            WallCheckResult result = wallProvider.check("update t_orders set status = 3 where id = 3 and status = 3 and status in (3, 4)");
            assertTrue(result.getViolations().size() == 0);
        }
    }
}
