package com.alibaba.druid.util;

import java.util.regex.Pattern;

public class IpUtils {

    public static Pattern buildIpCheckPattern(String permittedIP) {
        String[] addrs = permittedIP.split(",");
        StringBuilder sb = new StringBuilder();
        for (String addr : addrs ) {
            addr = addr.trim();
            addr = addr.replace("*", "\\d{1,3}");
            sb.append(addr.trim()).append("|");
        }
        return Pattern.compile(sb.toString());
    }

}
