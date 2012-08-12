package com.alibaba.druid.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class ServletPathMathcherTest {

    /**
     * PatternMatcher used in determining which paths to react to for a given request.
     */
    protected PatternMatcher pathMatcher = new ServletPathMatcher();

    @Test
    public void testStartsWithPattern() {
        String[] bogusPatterns = { "/druid*", "/druid*/what*", "*/druid*" };
        String[] bogusSources = { "/druid", "/druid/index.html", "/druid*/what/xyu" };
        boolean result = false;
        for (int i = 0; i < bogusSources.length; i++) {
            for (int j = 0; j < bogusPatterns.length; j++) {
                String bogusSource = bogusSources[i];
                String bogusPattern = bogusPatterns[j];
                if (pathMatcher.matches(bogusPattern, bogusSource)) {
                    result = true;
                }
                if (result == true) {
                    break;
                }
            }
            assertThat(true, equalTo(result));
            result = false;
        }
    }
    
    @Test
    public void testEndsWithPattern() {
        String[] bogusPatterns = { "*.html", "*.ico", "*.css" };
        String[] bogusSources = { "/index.html", "/favicon.ico", "/druid.css" };
        boolean result = false;
        for (int i = 0; i < bogusSources.length; i++) {
            for (int j = 0; j < bogusPatterns.length; j++) {
                String bogusSource = bogusSources[i];
                String bogusPattern = bogusPatterns[j];
                if (pathMatcher.matches(bogusPattern, bogusSource)) {
                    result = true;
                }
                if (result == true) {
                    break;
                }
            }
            assertThat(true, equalTo(result));
            result = false;
        }
    }
    
    @Test
    public void testEqualsPattern() {
        String[] bogusPatterns = { "/index.html", "/favicon.ico", "/xyz" };
        String[] bogusSources = { "/index.html", "/favicon.ico", "/xyz" };
        boolean result = false;
        for (int i = 0; i < bogusSources.length; i++) {
            for (int j = 0; j < bogusPatterns.length; j++) {
                String bogusSource = bogusSources[i];
                String bogusPattern = bogusPatterns[j];
                if (pathMatcher.matches(bogusPattern, bogusSource)) {
                    result = true;
                }
                if (result == true) {
                    break;
                }
            }
            assertThat(true, equalTo(result));
            result = false;
        }
    }
    
    @Test
    public void testPatternPriority() {
        String[] bogusPatterns = { "*html*", "/favicon.ico*", "*html" };
        String[] bogusSources = { "*html/ok?", "/favicon.ico/ok?", "/index.html" };
        boolean result = false;
        for (int i = 0; i < bogusSources.length; i++) {
            for (int j = 0; j < bogusPatterns.length; j++) {
                String bogusSource = bogusSources[i];
                String bogusPattern = bogusPatterns[j];
                if (pathMatcher.matches(bogusPattern, bogusSource)) {
                    result = true;
                }
                if (result == true) {
                    break;
                }
            }
            assertThat(true, equalTo(result));
            result = false;
        }
    }
}
