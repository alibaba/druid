/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        
        rang.toString();
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
