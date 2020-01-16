package com.alibaba.druid.bvt.filter.wall;

import java.util.Collection;
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

    @SuppressWarnings("unchecked")
    public void testMySql() throws Exception {
        WallProvider providerA = new MySqlWallProvider();

        {
            String sql = "select * from t where len(fname1) = 1 OR 1 = 1";
            Assert.assertFalse(providerA.checkValid(sql));
            providerA.addViolationEffectRowCount(10);
        }

        WallProvider providerB = new MySqlWallProvider();
        {
            String sql = "select * from t where len(fname2) = 2 OR 1 = 1";
            Assert.assertFalse(providerB.checkValid(sql));
            providerB.addViolationEffectRowCount(11);
        }
        
        WallProvider providerC = new MySqlWallProvider();
        {
            String sql = "select * from t where len(fname2) = 2 OR 1 = 1";
            Assert.assertFalse(providerC.checkValid(sql));
            providerC.addViolationEffectRowCount(12);
        }

        Map<String, Object> statMapA = providerA.getStatsMap();
        Map<String, Object> statMapB = providerB.getStatsMap();
        Map<String, Object> statMapC = providerC.getStatsMap();

        System.out.println(JSONUtils.toJSONString(statMapA));
        System.out.println(JSONUtils.toJSONString(statMapB));
        System.out.println(JSONUtils.toJSONString(statMapC));

        Map<String, Object> statMapMerged = DruidStatManagerFacade.mergeWallStat(statMapA, statMapB);
        System.out.println(JSONUtils.toJSONString(statMapMerged));
        
        Assert.assertEquals(2L, statMapMerged.get("checkCount"));
        Assert.assertEquals(21L, statMapMerged.get("violationEffectRowCount"));
        Assert.assertEquals(2, ((Collection<Map<String, Object>>) statMapMerged.get("blackList")).size());
        
        statMapMerged = DruidStatManagerFacade.mergeWallStat(statMapMerged, statMapC);
        System.out.println(JSONUtils.toJSONString(statMapMerged));
        Assert.assertEquals(2, ((Collection<Map<String, Object>>) statMapMerged.get("blackList")).size());
        Assert.assertEquals(33L, statMapMerged.get("violationEffectRowCount"));
    }

}
