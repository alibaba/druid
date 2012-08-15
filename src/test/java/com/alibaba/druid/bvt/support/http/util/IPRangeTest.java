package com.alibaba.druid.bvt.support.http.util;

import junit.framework.TestCase;

import com.alibaba.druid.support.http.util.IPAddress;
import com.alibaba.druid.support.http.util.IPRange;

public class IPRangeTest extends TestCase {

    public void test_ipRange_0() throws Exception {
        IPRange rang = new IPRange("128.242.127.0/24");

        assertEquals("128.242.127.0", rang.getIPAddress().toString());
        assertEquals("255.255.255.0", rang.getIPSubnetMask().toString());

        assertTrue(rang.isIPAddressInRange(new IPAddress("128.242.127.2")));

        assertFalse(rang.isIPAddressInRange(new IPAddress("128.242.126.2")));

    }

    public void test_ipRange_1() throws Exception {
        IPRange rang = new IPRange("128.242.127.0/30");

        assertEquals("128.242.127.0", rang.getIPAddress().toString());
        assertEquals("255.255.255.252", rang.getIPSubnetMask().toString());

        assertTrue(rang.isIPAddressInRange(new IPAddress("128.242.127.0")));
        assertTrue(rang.isIPAddressInRange(new IPAddress("128.242.127.1")));
        assertTrue(rang.isIPAddressInRange(new IPAddress("128.242.127.2")));
        assertTrue(rang.isIPAddressInRange(new IPAddress("128.242.127.3")));

        assertFalse(rang.isIPAddressInRange(new IPAddress("128.242.127.4")));

    }

    public void test_ipRange_2() throws Exception {
        IPRange rang = new IPRange("10.16.200.0/24");

        assertTrue(rang.isIPAddressInRange(new IPAddress("10.16.200.0")));
        assertTrue(rang.isIPAddressInRange(new IPAddress("10.16.200.255")));

    }

    public void test_ipRange_3() throws Exception {
        IPRange rang = new IPRange("0.0.0.0/0");
        assertTrue(rang.isIPAddressInRange(new IPAddress("2.16.200.0")));

    }

    public void test_ipRange_4() throws Exception {
        IPRange rang = new IPRange("1.1.1.1/0");

        assertTrue(rang.isIPAddressInRange(new IPAddress("2.16.200.0")));
    }
    
    public void test_ipRange_5() throws Exception {
        IPRange rang = new IPRange("1.1.1.1");

        assertTrue(rang.isIPAddressInRange(new IPAddress("1.1.1.1")));
    }
    
    public void test_ipRange_6() throws Exception {
        IPRange rang = new IPRange("128.242.127.3");
        
        assertTrue(rang.isIPAddressInRange(new IPAddress("128.242.127.3")));
        assertFalse(rang.isIPAddressInRange(new IPAddress("128.242.127.4")));
    }
}
