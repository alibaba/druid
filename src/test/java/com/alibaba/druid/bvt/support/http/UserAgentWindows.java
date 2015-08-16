/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

public class UserAgentWindows extends TestCase {

    public void test_windows98() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 5.00; Windows 98)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(1, stat.getBrowserIE5Count());
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
        
        Assert.assertEquals(1, stat.getOSWindows98Count());
    }
    
    public void test_windows98_1() throws Exception {
        WebAppStat stat = new WebAppStat("");

        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Win 9x 4.90; AT&T CSM6.0; FunWebProducts)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(1, stat.getBrowserIE6Count());
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
        
        Assert.assertEquals(1, stat.getOSWindows98Count());
    }
    
    public void test_windowsXP() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows XP)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(1, stat.getBrowserIE6Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindowsXPCount());
    }
    
    public void test_windowsXP_1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.1; Windows XP)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(1, stat.getBrowserIE6Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindowsXPCount());
    }
    
    public void test_windowsXP_IE6() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("User-Agent: Mozilla/5.0 (compatible; MSIE 6.0;Windows XP)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(1, stat.getBrowserIE6Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindowsXPCount());
    }
    
    public void test_windowsXP_IE6_1() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(1, stat.getBrowserIE6Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindowsXPCount());
    }

    public void test_windowsXP_IE7() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; User-agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; http://bsalsa.com) ; User-agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; http://bsalsa.com) (none))");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(1, stat.getBrowserIE7Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindowsXPCount());
    }
    
    public void test_windowsXP_IE8() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(1, stat.getBrowserIE8Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindowsXPCount());
    }
    
    public void test_windowsXP_IE9() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 5.1; WOW64; Trident/4.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(1, stat.getBrowserIE9Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindowsXPCount());
    }
    
    public void test_windowsXP_firefox() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.8) Gecko/2009032609 Firefox/3.0.8");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindowsXPCount());
    }
    
    public void test_windows2000_IE5() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(1, stat.getBrowserIE5Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows2000Count());
    }
    
    public void test_windows2000_IE6() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 2.0.50727)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(1, stat.getBrowserIE6Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows2000Count());
    }
    
    public void test_windows2000_IE7() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(1, stat.getBrowserIE7Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows2000Count());
    }
    
    public void test_windows2000_IE8() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.0; Trident/4.0; 360SE)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(1, stat.getBrowserIE8Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows2000Count());
    }
    
    public void test_windows2000_IE9() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 5.0; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; 360SE)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(0, stat.getBrowserIE8Count());
        Assert.assertEquals(1, stat.getBrowserIE9Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows2000Count());
    }
    
    public void test_windows2000_firefox() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.0; zh-CN; rv:1.9.0.8) Gecko/2009032609 Firefox/3.0.8 (.NET CLR 3.5.30729)");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows2000Count());
    }
    
    public void test_windows2000_firefox11() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/5.0 (Windows NT 5.0; rv:11.0) Gecko/20100101 Firefox/11.0");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows2000Count());
    }
    
    public void test_windows2000_chrome() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US) AppleWebKit/525.19 (KHTML, like Gecko) Chrome/1.0.154.53 Safari/525.19");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows2000Count());
    }
    
    public void test_windowsVista() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; .NET4.0C; .NET4.0E)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(0, stat.getBrowserIE7Count());
        Assert.assertEquals(1, stat.getBrowserIE8Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindowsVistaCount());
    }
    
    public void test_windows7() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; InfoPath.3; SE 2.X MetaSr 1.0)");
        Assert.assertEquals(1, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
        Assert.assertEquals(0, stat.getBrowserIE6Count());
        Assert.assertEquals(1, stat.getBrowserIE7Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows7Count());
    }
    
    public void test_windows7_firefox() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/5.0 (Windows NT 6.1; rv:11.0) Gecko/20100101 Firefox/11.0");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows7Count());
    }
    
    public void test_windows7_chrome() throws Exception {
        WebAppStat stat = new WebAppStat("");
        
        stat.computeUserAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
        Assert.assertEquals(0, stat.getBrowserIECount());
        Assert.assertEquals(1, stat.getBrowserChromeCount());
        Assert.assertEquals(0, stat.getBrowserFirefoxCount());
        Assert.assertEquals(0, stat.getBrowserIE10Count());
        Assert.assertEquals(0, stat.getBrowserIE5Count());
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
        
        Assert.assertEquals(0, stat.getOSWindows98Count());
        Assert.assertEquals(1, stat.getOSWindows7Count());
    }
}
