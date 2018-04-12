package com.alibaba.druid.filter.url;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for HostAndPortHolder
 *
 * @author DigitalSonic
 */
public class HostAndPortHolderTest {
    @Before
    public void setUp() {
        HostAndPortHolder.loadProperties("/com/alibaba/druid/filter/url/datasource.properties");
    }

    @After
    public void tearDown() {
        HostAndPortHolder.getInstance().clearBlacklist();
    }

    @Test
    public void testLoadFromFileNotExisted() {
        int size = HostAndPortHolder.getInstance().getHolder().size();
        HostAndPortHolder.loadProperties("FILE_NOT_FOUND");
        assertEquals(size, HostAndPortHolder.getInstance().getHolder().size());
    }

    @Test
    public void testLoadFromProperties() {
        Map<String, List<String>> holder = HostAndPortHolder.getInstance().getHolder();
        assertEquals(3, holder.size());
        assertEquals("127.0.0.1:3066", holder.get("datasource.foo").get(0));
        assertEquals("192.168.0.1:3066", holder.get("datasource.foo").get(1));
        assertFalse(holder.containsKey("datasource.empty"));
    }

    @Test
    public void testRandomGet() {
        boolean[] flags = new boolean[] { false, false };
        HostAndPortHolder holder = HostAndPortHolder.getInstance();
        for (int i = 0; i < 10; i++) {
            String v = holder.get("datasource.foo");
            if (v.equals("127.0.0.1:3066")) {
                flags[0] = true;
            } else if (v.equals("192.168.0.1:3066")) {
                flags[1] = true;
            }
        }
        assertTrue(flags[0] && flags[1]);
    }

    @Test
    public void testGetOne() {
        HostAndPortHolder holder = HostAndPortHolder.getInstance();
        String value = holder.get("datasource.foo");
        assertTrue(value.equals("127.0.0.1:3066") || value.equals("192.168.0.1:3066"));
        assertEquals(HostAndPortHolder.UNAVAILABLE, holder.get("UNAVAILABLE"));
    }

    @Test
    public void testBlackList() {
        HostAndPortHolder holder = HostAndPortHolder.getInstance();
        holder.addBlacklist("192.168.0.1:3066");
        for (int i = 0; i < 100; i++) {
            assertNotEquals("192.168.0.1:3066", holder.get("datasource.foo"));
        }
    }
}