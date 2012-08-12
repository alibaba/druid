package com.alibaba.druid.util;

/**
 * Interface for components that can match source strings against a specified pattern string.
 * <p/>
 * Different implementations can support different pattern types, for example, Ant style path expressions, or regular
 * expressions, or other types of text based patterns.
 * 
 * @see com.alibaba.druid.util.AntPathMatcher AntPathMatcher
 * @since 0.2.6
 */
public interface PatternMatcher {

    /**
     * Returns <code>true</code> if the given <code>source</code> matches the specified <code>pattern</code>,
     * <code>false</code> otherwise.
     * 
     * @param pattern the pattern to match against
     * @param source the source to match
     * @return <code>true</code> if the given <code>source</code> matches the specified <code>pattern</code>,
     * <code>false</code> otherwise.
     */
    boolean matches(String pattern, String source);
}
