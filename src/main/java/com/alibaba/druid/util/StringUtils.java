package com.alibaba.druid.util;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class StringUtils {

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
     * @param start
     * @param to
     * @return
     */
    public static String subString(String src, String start, String to) {
        int indexFrom = src.indexOf(start);
        int indexTo = src.indexOf(to);
        if (indexFrom < 0 || indexTo < 0 || indexFrom < indexTo) {
            return null;
        }
        indexFrom += start.length();
        indexTo += to.length();
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
            return null;
        }
    }

}
