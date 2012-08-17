package com.alibaba.druid.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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

    /**
     * Tokenize the given String into a String array via a StringTokenizer. Trims tokens and omits empty tokens.
     * <p>
     * The given delimiters string is supposed to consist of any number of delimiter characters. Each of those
     * characters can be used to separate tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using <code>delimitedListToStringArray</code>
     * <p/>
     * <p>
     * Copied from the Spring Framework while retaining all license, copyright and author information.
     * 
     * @param str the String to tokenize
     * @param delimiters the delimiter characters, assembled as String (each of those characters is individually
     * considered as delimiter).
     * @return an array of the tokens
     * @see java.util.StringTokenizer
     * @see java.lang.String#trim()
     */
    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    /**
     * Tokenize the given String into a String array via a StringTokenizer.
     * <p>
     * The given delimiters string is supposed to consist of any number of delimiter characters. Each of those
     * characters can be used to separate tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using <code>delimitedListToStringArray</code>
     * <p/>
     * <p>
     * Copied from the Spring Framework while retaining all license, copyright and author information.
     * 
     * @param str the String to tokenize
     * @param delimiters the delimiter characters, assembled as String (each of those characters is individually
     * considered as delimiter)
     * @param trimTokens trim the tokens via String's <code>trim</code>
     * @param ignoreEmptyTokens omit empty tokens from the result array (only applies to tokens that are empty after
     * trimming; StringTokenizer will not consider subsequent delimiters as token in the first place).
     * @return an array of the tokens (<code>null</code> if the input String was <code>null</code>)
     * @see java.util.StringTokenizer
     * @see java.lang.String#trim()
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
                                                 boolean ignoreEmptyTokens) {

        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    /**
     * Copy the given Collection into a String array. The Collection must contain String elements only.
     * <p/>
     * <p>
     * Copied from the Spring Framework while retaining all license, copyright and author information.
     * 
     * @param collection the Collection to copy
     * @return the String array (<code>null</code> if the passed-in Collection was <code>null</code>)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String[] toStringArray(Collection collection) {
        if (collection == null) {
            return null;
        }
        return (String[]) collection.toArray(new String[collection.size()]);
    }

    public static void main(String args[]) {
        System.out.println(JSON.toJSONString(getParameters("test?t=1&f=").get("t")));
    }
    
    public static boolean equals(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }
}
