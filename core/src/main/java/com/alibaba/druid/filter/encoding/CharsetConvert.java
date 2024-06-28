/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.filter.encoding;

import java.io.UnsupportedEncodingException;

/**
 * 字符编码转换器
 *
 * @author xianmao.hexm 2007-3-5 09:51:33
 */
public class CharsetConvert {
    private final String clientEncoding;

    private final String serverEncoding;

    private final boolean enable;

    public CharsetConvert(String clientEncoding, String serverEncoding) {
        this.clientEncoding = clientEncoding;
        this.serverEncoding = serverEncoding;
        this.enable = clientEncoding != null && serverEncoding != null && !clientEncoding.equalsIgnoreCase(serverEncoding);
    }

    /**
     * Encodes the specified string using the specified client and server encodings, if enabled.
     * If encoding is not enabled or the input string is empty, the original string is returned unchanged.
     *
     * @param s the string to be encoded
     * @return the encoded string, or the original string if encoding is not enabled or the input string is empty
     * @throws UnsupportedEncodingException if the specified encoding is not supported
     */
    public String encode(String s) throws UnsupportedEncodingException {
        if (enable && !isEmpty(s)) {
            s = new String(s.getBytes(clientEncoding), serverEncoding);
        }
        return s;
    }

    /**
     * Decodes the specified string using the specified server and client encodings, if enabled.
     * If decoding is not enabled or the input string is empty, the original string is returned unchanged.
     *
     * @param s the string to be decoded
     * @return the decoded string, or the original string if decoding is not enabled or the input string is empty
     * @throws UnsupportedEncodingException if the specified encoding is not supported
     */
    public String decode(String s) throws UnsupportedEncodingException {
        if (enable && !isEmpty(s)) {
            s = new String(s.getBytes(serverEncoding), clientEncoding);
        }
        return s;
    }

    /**
     * Determines whether the specified string is null or empty.
     *
     * @param s the string to be checked
     * @return true if the specified string is null or empty, false otherwise
     */
    public boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
