/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
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

package com.alibaba.druid.util;

import static org.junit.*;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testSubStringToInteger() {
        assertNull(StringUtils.subStringToInteger("foobar", "1", "3"));

        assertEquals(new Integer(2),
                StringUtils.subStringToInteger("1234", "1", "3"));
    }

    @Test
    public void testSubString() {
        assertNull(StringUtils.subString(",", "foo", ","));
        assertNull(
                StringUtils.subString("foo", "foo", "a\'b\'c", false));
        assertNull(
                StringUtils.subString("foo", "foo", "a\'b\'c", true));

        assertEquals("a 2b ",
                StringUtils.subString("1a 2b 3c", "1", "3"));
        assertEquals("c",
                StringUtils.subString("abcdef", "b", "d", true));
    }

    @Test
    public void testStringToInteger() {
        assertNull(StringUtils.stringToInteger(""));
        assertNull(StringUtils.stringToInteger(null));
        assertNull(StringUtils.stringToInteger("a"));

        assertEquals(new Integer(3), StringUtils.stringToInteger("3"));
    }

    @Test
    public void testEquals() {
        assertFalse(StringUtils.equals(null, ""));
        assertFalse(StringUtils.equals("foo", "bar"));
        assertFalse(StringUtils.equals("foo", "FOO"));

        assertTrue(StringUtils.equals(null, null));
        assertTrue(StringUtils.equals("foo", "foo"));
    }

    @Test
    public void testEqualsIgnoreCase() {
        assertFalse(StringUtils.equalsIgnoreCase(null, ""));
        assertFalse(StringUtils.equalsIgnoreCase("foo", "bar"));

        assertTrue(StringUtils.equalsIgnoreCase(null, null));
        assertTrue(StringUtils.equalsIgnoreCase("foo", "foo"));
        assertTrue(StringUtils.equalsIgnoreCase("foo", "FOO"));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));

        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty("foo"));
    }

    @Test
    public void testLowerHashCode() {
        assertEquals(0, StringUtils.lowerHashCode(""));
        assertEquals(0, StringUtils.lowerHashCode(null));
        assertEquals(97299, StringUtils.lowerHashCode("Bar"));
    }

    @Test
    public void testIsNumberStringInput() {
        assertTrue(StringUtils.isNumber("-0x3"));
        assertTrue(StringUtils.isNumber("2"));
        assertTrue(StringUtils.isNumber("5L"));
        assertTrue(StringUtils.isNumber("0e+3"));

        assertFalse(StringUtils.isNumber(""));
        assertFalse(StringUtils.isNumber("-0x"));
        assertFalse(StringUtils.isNumber("0xh"));
        assertFalse(StringUtils.isNumber("0x\u0018"));
        assertFalse(StringUtils.isNumber("0xAc\u8040????????"));
        assertFalse(
                StringUtils.isNumber("0x3c\\u8040????????????????????????"));
        assertFalse(StringUtils.isNumber("..."));
        assertFalse(StringUtils.isNumber("-0eE#"));
        assertFalse(StringUtils.isNumber("E.."));
        assertFalse(StringUtils.isNumber("2.+L"));
        assertFalse(StringUtils.isNumber("a/b/c"));
        assertFalse(StringUtils.isNumber("E"));
        assertFalse(StringUtils.isNumber("e"));
        assertFalse(StringUtils.isNumber("-d"));
        assertFalse(StringUtils.isNumber("-D"));
        assertFalse(StringUtils.isNumber("-f"));
        assertFalse(StringUtils.isNumber("-F"));
        assertFalse(StringUtils.isNumber("-l"));
        assertFalse(StringUtils.isNumber("\'"));
        assertFalse(StringUtils.isNumber("-"));
    }

    @Test
    public void testIsNumberCharArrayInput() {
        assertTrue(
                StringUtils.isNumber(new char[]{'-', '0', 'x', 'a'}));
        assertTrue(StringUtils.isNumber(new char[]{'-', '.', '1'}));
        assertTrue(StringUtils.isNumber(new char[]{'6', 'l'}));
        assertTrue(
                StringUtils.isNumber(new char[]{'0', 'e', '+', '3'}));

        assertFalse(StringUtils.isNumber(new char[0]));
        assertFalse(StringUtils.isNumber(new char[]{'0', 'x'}));
        assertFalse(
                StringUtils.isNumber(new char[]{'-', '0', 'x', '9', ' '}));
        assertFalse(
                StringUtils.isNumber(new char[]{'-', '0', 'x', '9', 'i'}));
        assertFalse(
                StringUtils.isNumber(new char[]{'-', '.', '.', 'a'}));
        assertFalse(StringUtils.isNumber(
                new char[]{'-', '1', 'e', 'E', '\u0000', '\u0000', '\u0000'}));
        assertFalse(
                StringUtils.isNumber(new char[]{'-', '.', 'E', 'a'}));
        assertFalse(StringUtils.isNumber(new char[]{'+', '\u0016'}));
        assertFalse(StringUtils.isNumber(new char[]{';', '\u0016'}));
        assertFalse(StringUtils.isNumber(new char[]{'-', '9', 'e'}));
        assertFalse(StringUtils.isNumber(new char[]{'-', '.', 'f'}));
        assertFalse(StringUtils.isNumber(new char[]{'-', '.', 'F'}));
        assertFalse(StringUtils.isNumber(new char[]{'-', '.', 'd'}));
        assertFalse(StringUtils.isNumber(new char[]{'-', '.', 'D'}));
        assertFalse(StringUtils.isNumber(new char[]{'-', '.', 'l'}));
        assertFalse(StringUtils.isNumber(new char[]{'5', 't'}));
        assertFalse(StringUtils.isNumber(new char[]{'-'}));
    }
}
