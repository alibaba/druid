package com.alibaba.druid.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.fastjson.JSON;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class StringUtils {

    private final static Log LOG = LogFactory.getLog(StringUtils.class);

    /**
     * Example: subString("12345","1","4")=23
     * 
     * @param src
     * @param start
     * @param to
     * @return
     */
    public static Integer subStringToInteger(String src, String start, String to) {
        return stringToInteger(subString(src, start, to));
    }

    /**
     * Example: subString("abcd","a","c")="b"
     * 
     * @param src
     * @param start null while start from index=0
     * @param to null while to index=src.length
     * @return
     */
    public static String subString(String src, String start, String to) {
        int indexFrom = start == null ? 0 : src.indexOf(start);
        int indexTo = to == null ? src.length() : src.indexOf(to);
        if (indexFrom < 0 || indexTo < 0 || indexFrom > indexTo) {
            return null;
        }
        indexFrom += start.length();
        return src.substring(indexFrom, indexTo);

    }

    /**
     * @param in
     * @return
     */
    public static Integer stringToInteger(String in) {
        if (in == null) return null;
        in = in.trim();
        if (in.length() == 0) return null;
        try {
            return Integer.parseInt(in);
        } catch (NumberFormatException e) {
            LOG.warn("stringToInteger fail,string=" + in, e);
            return null;
        }
    }

    /**
     * @param in
     * @return
     */
    public static Long stringToLong(String in) {
        if (in == null) return null;
        in = in.trim();
        if (in.length() == 0) return null;
        try {
            return Long.parseLong(in);
        } catch (NumberFormatException e) {
            LOG.warn("stringToLong fail,string=" + in, e);
            return null;
        }
    }

    public static Map<String, String> getParameters(String url) {
        if (url == null || (url = url.trim()).length() == 0) return null;

        String parametersStr = subString(url, "?", null);
        if (parametersStr == null || parametersStr.length() == 0) return null;

        String[] parametersArray = parametersStr.split("&");
        Map<String, String> parameters = new LinkedHashMap<String, String>();

        for (String parameterStr : parametersArray) {
            int index = parameterStr.indexOf("=");
            if (index <= 0) continue;

            String name = parameterStr.substring(0, index);
            String value = parameterStr.substring(index + 1);
            parameters.put(name, value);
        }
        return parameters;
    }

    public static void main(String args[]) {
        System.out.println(JSON.toJSONString(getParameters("test?t=1&f=").get("t")));
    }
}
