package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;


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
            assertFalse(providerA.checkValid(sql));
            providerA.addViolationEffectRowCount(10);
        }

        WallProvider providerB = new MySqlWallProvider();
        {
            String sql = "select * from t where len(fname2) = 2 OR 1 = 1";
            assertFalse(providerB.checkValid(sql));
            providerB.addViolationEffectRowCount(11);
        }

        WallProvider providerC = new MySqlWallProvider();
        {
            String sql = "select * from t where len(fname2) = 2 OR 1 = 1";
            assertFalse(providerC.checkValid(sql));
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

        assertEquals(2L, statMapMerged.get("checkCount"));
        assertEquals(21L, statMapMerged.get("violationEffectRowCount"));
        assertEquals(2, ((Collection<Map<String, Object>>) statMapMerged.get("blackList")).size());

        statMapMerged = DruidStatManagerFacade.mergeWallStat(statMapMerged, statMapC);
        System.out.println(JSONUtils.toJSONString(statMapMerged));
        assertEquals(2, ((Collection<Map<String, Object>>) statMapMerged.get("blackList")).size());
        assertEquals(33L, statMapMerged.get("violationEffectRowCount"));
    }

}
