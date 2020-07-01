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

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.support.http.stat.WebAppStat;

public class UserAgentTest extends TestCase {

    public void test_agent_ie10() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE10Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie9() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE9Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie9_1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; 360SE)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE9Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie9_x86() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE9Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie8() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; chromeframe; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; MAXTHON 2.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE8Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie8_x1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE8Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie8_x2() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/4.0 (compatible; GoogleToolbar 7.2.2427.2330; Windows XP 5.1; MSIE 8.0.6001.18702)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE8Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie8_x3() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/4.0 (compatible; GoogleToolbar 7.3.2710.138; Windows 6.1; MSIE 8.0.7601.17514)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE8Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie7() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Tablet PC 2.0; MAXTHON 2.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE7Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie7_1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; InfoPath.3; SE 2.X MetaSr 1.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE7Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie6() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 6.0; MAXTHON 2.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE6Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie6_1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE6Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie6_2() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("User-Agent: Mozilla/5.0 (compatible; MSIE 6.0;Windows XP)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE6Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ie5() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 5.00; Windows 98)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserIE5Count());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_ipad() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (iPad; U; CPU OS 4_2_1 like Mac OS X; zh-cn) AppleWebKit/533.17.9 (KHTML, like Gecko) ");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getOSMacOSXCount());
        Assert.assertEquals(1, stat.getDeviceIpadCount());
    }

    public void test_agent_firefox() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(1, stat.getOSLinuxCount());
    }

    public void test_agent_chrome() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.133 Safari/534.16");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getOSWindowsCount());
    }

    public void test_agent_opera() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Opera/9.64 (Windows NT 5.1; U; en) Presto/2.1.1");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getOSWindowsCount());
        Assert.assertEquals(1, stat.getBrowserOperaCount());
    }

    public void test_agent_android22() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Linux; U; Android 2.2.1; zh-cn; HTC_Wildfire_A3333 Build/FRG83D) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getOSLinuxCount());
        Assert.assertEquals(1, stat.getOSAndroidCount());
        Assert.assertEquals(1, stat.getDeviceAndroidCount());
    }

    public void test_agent_other() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("SEC-schx199 UP.Browser/4.1.26l UP.Link/5.1.2.9");
        Assert.assertEquals(0, stat.getBrowserIECount());
    }

    public void test_agent_other_1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mitsu/1.2.B (MT560) MMP/1.1");
        Assert.assertEquals(0, stat.getBrowserIECount());
    }

    public void test_0() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Science Traveller International 1X/1.0");
        stat.computeUserAgent("Mozilla/3.0 (compatible)");
        stat.computeUserAgent("amaya/9.52 libwww/5.4.0");
        stat.computeUserAgent("amaya/9.51 libwww/5.4.0");
        stat.computeUserAgent("amaya/9.1 libwww/5.4.0");
        stat.computeUserAgent("amaya/6.2 libwww/5.3.1");
        stat.computeUserAgent("AmigaVoyager/3.4.4 (MorphOS/PPC native)");
        stat.computeUserAgent("xChaos_Arachne/5.1.89;GPL,386+");

        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
    }

    public void test_1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Ubuntu APT-HTTP/1.3 (0.7.23.1ubuntu2)");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(1, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
    }

    public void test_2() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Ubuntu APT-HTTP/1.3");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(1, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
    }

    public void test_3() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/523.15 (KHTML, like Gecko, Safari/419.3) Arora/0.3 (Change: 287 c9dfb30)");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(1, stat.getBrowserSafariCount());

        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(1, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
    }

    public void test_firefox_1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en; rv:1.8.1.14) Gecko/20080409 Camino/1.6 (like Firefox/2.0.0.14)");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(1, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
    }

    public void test_firefox_2() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (X11; U; Linux i686; en; rv:1.9.0.11) Gecko/20080528 Epiphany/2.22 Firefox/3.0");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(1, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
    }

    public void test_firefox_3() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (X11; U; OpenBSD i386; en-US; rv:1.8.1.14) Gecko/20080821 Firefox/2.0.0.14");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(1, stat.getOSOpenBSDCount());
    }

    public void test_firefox_4() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.3) Gecko/20041002 Firefox/0.10.1");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(1, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
    }

    public void test_firefox_5() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Windows NT 5.1; rv:5.0) Gecko/20100101 Firefox/5.0");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(1, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
    }
    
    public void test_firefox_6() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());
        
        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());
        
        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(1, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
    }

    public void test_android_1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Linux; U; Android 2.2.1; fr-ch; A43 Build/FROYO) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(1, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(1, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(1, stat.getOSAndroidCount());
        Assert.assertEquals(1, stat.getOSAndroid22Count());
    }

    public void test_android_15() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Linux; U; Android 1.5; zh-cn; ME600 Build/CUPCAKE) AppleWebKit/528.5+ (KHTML, like Gecko) Version/3.1.2 Mobile Safari/525.20.1");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(1, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(1, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(1, stat.getOSAndroidCount());
        Assert.assertEquals(1, stat.getOSAndroid15Count());
    }
    
    public void test_android_16() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Linux; U; Android 1.6; en-gb; Dell Streak Build/Donut AppleWebKit/528.5+ (KHTML, like Gecko) Version/3.1.2 Mobile Safari/ 525.20.1");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());
        
        Assert.assertEquals(1, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());
        
        Assert.assertEquals(1, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(1, stat.getOSAndroidCount());
        Assert.assertEquals(1, stat.getOSAndroid16Count());
    }

    public void test_android_21() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Linux; U; Android 2.1-update1; de-de; HTC Desire 1.19.161.5 Build/ERE27) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(1, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(1, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(1, stat.getOSAndroidCount());
        Assert.assertEquals(1, stat.getOSAndroid21Count());
    }
    
    public void test_android_23() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Linux; U; Android 2.3.3; zh-cn; HTC Incredible S Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());
        
        Assert.assertEquals(1, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());
        
        Assert.assertEquals(1, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(1, stat.getOSAndroidCount());
        Assert.assertEquals(1, stat.getOSAndroid23Count());
    }
    
    public void test_android_4() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Linux; U; Android 4.0.4; zh-cn; Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());
        
        Assert.assertEquals(1, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());
        
        Assert.assertEquals(1, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(1, stat.getOSAndroidCount());
        Assert.assertEquals(1, stat.getOSAndroid40Count());
    }

    public void test_safari() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());

        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());

        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(1, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(0, stat.getOSAndroidCount());
        Assert.assertEquals(0, stat.getOSAndroid21Count());
    }
    
    public void test_iphone() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_1 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8B117 Safari/6531.22.7 (compatible; Googlebot-Mobile/2.1; +http://www.google.com/bot.html)");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(1, stat.getBrowserSafariCount());
        
        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(1, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());
        
        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(1, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(0, stat.getOSAndroidCount());
        Assert.assertEquals(0, stat.getOSAndroid21Count());
    }
    
    public void test_ipad() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (iPad; CPU OS 5_0_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A405 Safari/7534.48.3");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(0, stat.getBrowserOperaCount());
        Assert.assertEquals(1, stat.getBrowserSafariCount());
        
        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(1, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());
        
        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(1, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(0, stat.getOSAndroidCount());
        Assert.assertEquals(0, stat.getOSAndroid21Count());
    }
    
    public void test_opera() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Opera/9.00 (Windows NT 4.0; U; en)");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(1, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());
        
        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());
        
        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(0, stat.getOSMacOSXCount());
        Assert.assertEquals(1, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(0, stat.getOSAndroidCount());
        Assert.assertEquals(0, stat.getOSAndroid21Count());
    }
    
    public void test_opera_1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Opera/9.80 (Macintosh; Intel Mac OS X; U; en) Presto/2.2.15 Version/10.00");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(0, stat.getBrowserIE9Count());
        Assert.assertEquals(1, stat.getBrowserOperaCount());
        Assert.assertEquals(0, stat.getBrowserSafariCount());
        
        Assert.assertEquals(0, stat.getDeviceAndroidCount());
        Assert.assertEquals(0, stat.getDeviceIpadCount());
        Assert.assertEquals(0, stat.getDeviceIphoneCount());
        Assert.assertEquals(0, stat.getDeviceWindowsPhoneCount());
        
        Assert.assertEquals(0, stat.getOSLinuxCount());
        Assert.assertEquals(0, stat.getOSLinuxUbuntuCount());
        Assert.assertEquals(1, stat.getOSMacOSXCount());
        Assert.assertEquals(0, stat.getOSWindowsCount());
        Assert.assertEquals(0, stat.getOSSymbianCount());
        Assert.assertEquals(0, stat.getOSFreeBSDCount());
        Assert.assertEquals(0, stat.getOSOpenBSDCount());
        Assert.assertEquals(0, stat.getOSAndroidCount());
        Assert.assertEquals(0, stat.getOSAndroid21Count());
    }
}
