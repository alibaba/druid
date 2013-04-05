package com.alibaba.druid.bvt.filter.wall;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.wall.WallContext;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

public class WallStatTest_statMap extends TestCase {

    protected void setUp() throws Exception {
        WallContext.clearContext();
    }

    protected void tearDown() throws Exception {
        WallContext.clearContext();
    }

    public void testMySql() throws Exception {
        WallProvider providerA = new MySqlWallProvider();

        {
            String sql = "select * from t where len(fname) = 1";
            Assert.assertTrue(providerA.checkValid(sql));
        }

        WallProvider providerB = new MySqlWallProvider();
        {
            String sql = "select * from t where len(fname) = 2";
            Assert.assertTrue(providerB.checkValid(sql));
        }

        Map<String, Object> statMapA = providerA.getStatsMap();
        Map<String, Object> statMapB = providerB.getStatsMap();

        System.out.println(JSONUtils.toJSONString(statMapA));
        System.out.println(JSONUtils.toJSONString(statMapB));

        Map<String, Object> statMapMerged = DruidStatManagerFacade.mergWallStat(statMapA, statMapB);
        System.out.println(JSONUtils.toJSONString(statMapMerged));
        
        Assert.assertEquals(2L, statMapMerged.get("checkCount"));
    }

}
