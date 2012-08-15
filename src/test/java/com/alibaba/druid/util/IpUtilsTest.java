package com.alibaba.druid.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.Assert;


public class IpUtilsTest {


    @Test
    public void testBuildIpCheckPattern() {

        Pattern pat = IpUtils.buildIpCheckPattern("192.168.0.*");
        String[] validIps = {"192.168.0.1", "192.168.0.2","192.168.0.15","192.168.0.250"};
        String[] inValidIps = {"192.168.15.1", "192.168.7.1","10.8.9.25","168.2.3.10"};
        checkIp(pat, validIps, inValidIps);

        pat = IpUtils.buildIpCheckPattern("192.168.0.1");
        validIps = new String[] {"192.168.0.1"};
        inValidIps = new String[] {"192.168.0.2", "192.168.1.0","10.15.255.3"};
        checkIp(pat, validIps, inValidIps);

        pat = IpUtils.buildIpCheckPattern("192.168.0.1,192.168.0.2,192.168.1.*");
        validIps = new String[] {"192.168.0.1","192.168.0.2","192.168.1.15"};
        inValidIps = new String[] {"192.168.0.3", "192.168.10.2","10.15.255.3","16.8.155.3"};
        checkIp(pat, validIps, inValidIps);

        
    }

    private void  checkIp(Pattern pat, String[] validIps, String[] inValidIps) {
        Matcher matcher = null;
        for (String validIp : validIps) {
            matcher = pat.matcher(validIp);
            Assert.assertTrue(matcher.matches());
        }

        for (String inValidIp : inValidIps) {
            matcher = pat.matcher(inValidIp);
            Assert.assertFalse(matcher.matches());
        }
    }

}
