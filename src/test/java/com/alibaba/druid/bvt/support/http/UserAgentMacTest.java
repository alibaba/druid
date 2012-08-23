package com.alibaba.druid.bvt.support.http;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.support.http.stat.WebAppStat;


public class UserAgentMacTest extends TestCase {
    
    public void test_mac_firefox() throws Exception {
        WebAppStat stat = new WebAppStat("");
        stat.computeUserAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6");
        Assert.assertEquals(0, stat.getBrowserChromeCount());
        Assert.assertEquals(1, stat.getBrowserFirefoxCount());
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
}
