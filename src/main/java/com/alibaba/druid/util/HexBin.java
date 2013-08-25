/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

/**
 * format validation This class encodes/decodes hexadecimal data
 * 
 * @xerces.internal
 * @author Jeffrey Rodriguez
 * @version $Id: HexBin.java,v 1.4 2007/07/19 04:38:32 ofung Exp $
 */
public final class HexBin {

    static private final int    BASELENGTH         = 128;
    static private final int    LOOKUPLENGTH       = 16;
    static final private byte[] hexNumberTable     = new byte[BASELENGTH];
    static final private char[] upperChars  = new char[LOOKUPLENGTH];
    static final private char[] lowerChars = new char[LOOKUPLENGTH];

    static {
        for (int i = 0; i < BASELENGTH; i++) {
            hexNumberTable[i] = -1;
        }
        for (int i = '9'; i >= '0'; i--) {
            hexNumberTable[i] = (byte) (i - '0');
        }
        for (int i = 'F'; i >= 'A'; i--) {
            hexNumberTable[i] = (byte) (i - 'A' + 10);
        }
        for (int i = 'f'; i >= 'a'; i--) {
            hexNumberTable[i] = (byte) (i - 'a' + 10);
        }

        for (int i = 0; i < 10; i++) {
            upperChars[i] = (char) ('0' + i);
            lowerChars[i] = (char) ('0' + i);
        }
        for (int i = 10; i <= 15; i++) {
            upperChars[i] = (char) ('A' + i - 10);
            lowerChars[i] = (char) ('a' + i - 10);
        }
    }
    
    public static String encode(byte[] bytes) {
        return encode(bytes, true);
    }

    public static String encode(byte[] bytes, boolean upperCase) {
        if (bytes == null) {
            return null;
        }

        final char[] chars = upperCase ? upperChars : lowerChars;

        char[] hex = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xFF;
            hex[i * 2] = chars[b >> 4];
            hex[i * 2 + 1] = chars[b & 0xf];
        }
        return new String(hex);
    }

    /**
     * Decode hex string to a byte array
     * 
     * @param encoded encoded string
     * @return return array of byte to encode
     */
    static public byte[] decode(String encoded) {
        if (encoded == null) {
            return null;
        }

        int lengthData = encoded.length();
        if (lengthData % 2 != 0) {
            return null;
        }

        char[] binaryData = encoded.toCharArray();
        int lengthDecode = lengthData / 2;
        byte[] decodedData = new byte[lengthDecode];
        byte temp1, temp2;
        char tempChar;
        for (int i = 0; i < lengthDecode; i++) {
            tempChar = binaryData[i * 2];
            temp1 = (tempChar < BASELENGTH) ? hexNumberTable[tempChar] : -1;
            if (temp1 == -1) {
                return null;
            }
            tempChar = binaryData[i * 2 + 1];
            temp2 = (tempChar < BASELENGTH) ? hexNumberTable[tempChar] : -1;
            if (temp2 == -1) {
                return null;
            }
            decodedData[i] = (byte) ((temp1 << 4) | temp2);
        }
        return decodedData;
    }
}
