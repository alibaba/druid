package com.alibaba.druid.util;

public class ServletPathMatcher implements PatternMatcher {

    /**
     * <p>
     * three type: endsWithMatch(eg. /xxx*=/xxx/xyz), startsWithMatch(eg. *.xxx=abc.xxx), equals(eg. /xxx=/xxx)。
     * </p>
     * <b>Notice</b>: *xxx* will match *xxxyyyy. endsWithMatch first.
     */
    @Override
    public boolean matches(String pattern, String source) {
        if (pattern == null || source == null) {
            return false;
        }
        pattern = pattern.trim();
        source = source.trim();
        if (pattern.endsWith("*")) {
            // pattern: /druid* source:/druid/index.html
            int length = pattern.length() - 1;
            if (source.length() >= length) {
                if (pattern.substring(0, length).equals(source.substring(0, length))) {
                    return true;
                }
            }
        } else if (pattern.startsWith("*")) {
            // pattern: *.html source:/xx/xx.html
            int length = pattern.length() - 1;
            if (source.length() >= length && source.endsWith(pattern.substring(1))) {
                return true;
            }
        } else {
            // pattern: /druid/index.html source:/druid/index.html
            if (pattern.equals(source)) {
                return true;
            }
        }
        return false;
    }

}
