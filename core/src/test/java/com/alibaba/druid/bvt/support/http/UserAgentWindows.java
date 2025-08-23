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

public class UserAgentWindows extends TestCase {
    public void test_windows98() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 5.00; Windows 98)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(1, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(1, stat.getOSWindows98Count());
    }

    public void test_windows98_1() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Win 9x 4.90; AT&T CSM6.0; FunWebProducts)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(1, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(1, stat.getOSWindows98Count());
    }

    public void test_windowsXP() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows XP)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(1, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindowsXPCount());
    }

    public void test_windowsXP_1() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.1; Windows XP)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(1, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindowsXPCount());
    }

    public void test_windowsXP_IE6() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("User-Agent: Mozilla/5.0 (compatible; MSIE 6.0;Windows XP)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(1, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindowsXPCount());
    }

    public void test_windowsXP_IE6_1() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(1, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindowsXPCount());
    }

    public void test_windowsXP_IE7() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; User-agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; http://bsalsa.com) ; User-agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; http://bsalsa.com) (none))");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(1, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindowsXPCount());
    }

    public void test_windowsXP_IE8() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(1, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindowsXPCount());
    }

    public void test_windowsXP_IE9() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 5.1; WOW64; Trident/4.0)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(1, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindowsXPCount());
    }

    public void test_windowsXP_firefox() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.8) Gecko/2009032609 Firefox/3.0.8");
        assertEquals(0, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(1, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindowsXPCount());
    }

    public void test_windows2000_IE5() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(1, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows2000Count());
    }

    public void test_windows2000_IE6() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 2.0.50727)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(1, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows2000Count());
    }

    public void test_windows2000_IE7() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.0)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(1, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows2000Count());
    }

    public void test_windows2000_IE8() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.0; Trident/4.0; 360SE)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(1, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows2000Count());
    }

    public void test_windows2000_IE9() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 5.0; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; 360SE)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(1, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows2000Count());
    }

    public void test_windows2000_firefox() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.0; zh-CN; rv:1.9.0.8) Gecko/2009032609 Firefox/3.0.8 (.NET CLR 3.5.30729)");
        assertEquals(0, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(1, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows2000Count());
    }

    public void test_windows2000_firefox11() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/5.0 (Windows NT 5.0; rv:11.0) Gecko/20100101 Firefox/11.0");
        assertEquals(0, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(1, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows2000Count());
    }

    public void test_windows2000_chrome() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US) AppleWebKit/525.19 (KHTML, like Gecko) Chrome/1.0.154.53 Safari/525.19");
        assertEquals(0, stat.getBrowserIECount());
        assertEquals(1, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows2000Count());
    }

    public void test_windowsVista() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; .NET4.0C; .NET4.0E)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(1, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindowsVistaCount());
    }

    public void test_windows7() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; InfoPath.3; SE 2.X MetaSr 1.0)");
        assertEquals(1, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(1, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows7Count());
    }

    public void test_windows7_firefox() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/5.0 (Windows NT 6.1; rv:11.0) Gecko/20100101 Firefox/11.0");
        assertEquals(0, stat.getBrowserIECount());
        assertEquals(0, stat.getBrowserChromeCount());
        assertEquals(1, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows7Count());
    }

    public void test_windows7_chrome() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
        assertEquals(0, stat.getBrowserIECount());
        assertEquals(1, stat.getBrowserChromeCount());
        assertEquals(0, stat.getBrowserFirefoxCount());
        assertEquals(0, stat.getBrowserIE10Count());
        assertEquals(0, stat.getBrowserIE5Count());
        assertEquals(0, stat.getBrowserIE6Count());
        assertEquals(0, stat.getBrowserIE7Count());
        assertEquals(0, stat.getBrowserIE8Count());
        assertEquals(0, stat.getBrowserIE9Count());
        assertEquals(0, stat.getBrowserOperaCount());
        assertEquals(0, stat.getBrowserSafariCount());

        assertEquals(0, stat.getDeviceAndroidCount());
        assertEquals(0, stat.getDeviceIpadCount());
        assertEquals(0, stat.getDeviceIphoneCount());
        assertEquals(0, stat.getDeviceWindowsPhoneCount());

        assertEquals(0, stat.getOSLinuxCount());
        assertEquals(0, stat.getOSLinuxUbuntuCount());
        assertEquals(0, stat.getOSMacOSXCount());
        assertEquals(1, stat.getOSWindowsCount());
        assertEquals(0, stat.getOSSymbianCount());
        assertEquals(0, stat.getOSFreeBSDCount());
        assertEquals(0, stat.getOSOpenBSDCount());

        assertEquals(0, stat.getOSWindows98Count());
        assertEquals(1, stat.getOSWindows7Count());
    }
}
