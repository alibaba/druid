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
package com.alibaba.druid.bvt.support.http;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;

import com.alibaba.druid.support.http.stat.WebAppStat;


public class UserAgentWin8IETest extends TestCase {
    public void test_mac_firefox() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0)");
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(1, stat.getBrowserIE10Count());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(1, stat.getOSWindows8Count());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());
    }
}
