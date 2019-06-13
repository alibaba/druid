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

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testSubStringToInteger() {
        Assert.assertNull(StringUtils.subStringToInteger("foobar", "1", "3"));

        Assert.assertEquals(new Integer(2),
                StringUtils.subStringToInteger("1234", "1", "3"));
    }

    @Test
    public void testSubString() {
        Assert.assertNull(StringUtils.subString(",", "foo", ","));
        Assert.assertNull(
                StringUtils.subString("foo", "foo", "a\'b\'c", false));
        Assert.assertNull(
                StringUtils.subString("foo", "foo", "a\'b\'c", true));

        Assert.assertEquals("a 2b ",
                StringUtils.subString("1a 2b 3c", "1", "3"));
        Assert.assertEquals("c",
                StringUtils.subString("abcdef", "b", "d", true));
    }

    @Test
    public void testStringToInteger() {
        Assert.assertNull(StringUtils.stringToInteger(""));
        Assert.assertNull(StringUtils.stringToInteger(null));
        Assert.assertNull(StringUtils.stringToInteger("a"));

        Assert.assertEquals(new Integer(3), StringUtils.stringToInteger("3"));
    }

    @Test
    public void testEquals() {
        Assert.assertFalse(StringUtils.equals(null, ""));
        Assert.assertFalse(StringUtils.equals("foo", "bar"));
        Assert.assertFalse(StringUtils.equals("foo", "FOO"));

        Assert.assertTrue(StringUtils.equals(null, null));
        Assert.assertTrue(StringUtils.equals("foo", "foo"));
    }

    @Test
    public void testEqualsIgnoreCase() {
        Assert.assertFalse(StringUtils.equalsIgnoreCase(null, ""));
        Assert.assertFalse(StringUtils.equalsIgnoreCase("foo", "bar"));

        Assert.assertTrue(StringUtils.equalsIgnoreCase(null, null));
        Assert.assertTrue(StringUtils.equalsIgnoreCase("foo", "foo"));
        Assert.assertTrue(StringUtils.equalsIgnoreCase("foo", "FOO"));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(StringUtils.isEmpty(null));
        Assert.assertTrue(StringUtils.isEmpty(""));

        Assert.assertFalse(StringUtils.isEmpty(" "));
        Assert.assertFalse(StringUtils.isEmpty("foo"));
    }

    @Test
    public void testLowerHashCode() {
        Assert.assertEquals(0, StringUtils.lowerHashCode(""));
        Assert.assertEquals(0, StringUtils.lowerHashCode(null));
        Assert.assertEquals(97299, StringUtils.lowerHashCode("Bar"));
    }

    @Test
    public void testIsNumberStringInput() {
        Assert.assertTrue(StringUtils.isNumber("-0x3"));
        Assert.assertTrue(StringUtils.isNumber("2"));
        Assert.assertTrue(StringUtils.isNumber("5L"));
        Assert.assertTrue(StringUtils.isNumber("0e+3"));

        Assert.assertFalse(StringUtils.isNumber(""));
        Assert.assertFalse(StringUtils.isNumber("-0x"));
        Assert.assertFalse(StringUtils.isNumber("0xh"));
        Assert.assertFalse(StringUtils.isNumber("0x\u0018"));
        Assert.assertFalse(StringUtils.isNumber("0xAc\u8040????????"));
        Assert.assertFalse(
                StringUtils.isNumber("0x3c\\u8040????????????????????????"));
        Assert.assertFalse(StringUtils.isNumber("..."));
        Assert.assertFalse(StringUtils.isNumber("-0eE#"));
        Assert.assertFalse(StringUtils.isNumber("E.."));
        Assert.assertFalse(StringUtils.isNumber("2.+L"));
        Assert.assertFalse(StringUtils.isNumber("a/b/c"));
        Assert.assertFalse(StringUtils.isNumber("E"));
        Assert.assertFalse(StringUtils.isNumber("e"));
        Assert.assertFalse(StringUtils.isNumber("-d"));
        Assert.assertFalse(StringUtils.isNumber("-D"));
        Assert.assertFalse(StringUtils.isNumber("-f"));
        Assert.assertFalse(StringUtils.isNumber("-F"));
        Assert.assertFalse(StringUtils.isNumber("-l"));
        Assert.assertFalse(StringUtils.isNumber("\'"));
        Assert.assertFalse(StringUtils.isNumber("-"));
    }

    @Test
    public void testIsNumberCharArrayInput() {
        Assert.assertTrue(
                StringUtils.isNumber(new char[]{'-', '0', 'x', 'a'}));
        Assert.assertTrue(StringUtils.isNumber(new char[]{'-', '.', '1'}));
        Assert.assertTrue(StringUtils.isNumber(new char[]{'6', 'l'}));
        Assert.assertTrue(
                StringUtils.isNumber(new char[]{'0', 'e', '+', '3'}));

        Assert.assertFalse(StringUtils.isNumber(new char[0]));
        Assert.assertFalse(StringUtils.isNumber(new char[]{'0', 'x'}));
        Assert.assertFalse(
                StringUtils.isNumber(new char[]{'-', '0', 'x', '9', ' '}));
        Assert.assertFalse(
                StringUtils.isNumber(new char[]{'-', '0', 'x', '9', 'i'}));
        Assert.assertFalse(
                StringUtils.isNumber(new char[]{'-', '.', '.', 'a'}));
        Assert.assertFalse(StringUtils.isNumber(
                new char[]{'-', '1', 'e', 'E', '\u0000', '\u0000', '\u0000'}));
        Assert.assertFalse(
                StringUtils.isNumber(new char[]{'-', '.', 'E', 'a'}));
        Assert.assertFalse(StringUtils.isNumber(new char[]{'+', '\u0016'}));
        Assert.assertFalse(StringUtils.isNumber(new char[]{';', '\u0016'}));
        Assert.assertFalse(StringUtils.isNumber(new char[]{'-', '9', 'e'}));
        Assert.assertFalse(StringUtils.isNumber(new char[]{'-', '.', 'f'}));
        Assert.assertFalse(StringUtils.isNumber(new char[]{'-', '.', 'F'}));
        Assert.assertFalse(StringUtils.isNumber(new char[]{'-', '.', 'd'}));
        Assert.assertFalse(StringUtils.isNumber(new char[]{'-', '.', 'D'}));
        Assert.assertFalse(StringUtils.isNumber(new char[]{'-', '.', 'l'}));
        Assert.assertFalse(StringUtils.isNumber(new char[]{'5', 't'}));
        Assert.assertFalse(StringUtils.isNumber(new char[]{'-'}));
    }
}
