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

    private String  clientEncoding = null;

    private String  serverEncoding = null;

    private boolean enable         = false;

    public CharsetConvert(String clientEncoding, String serverEncoding){
        this.clientEncoding = clientEncoding;
        this.serverEncoding = serverEncoding;
        if (clientEncoding != null && serverEncoding != null && !clientEncoding.equalsIgnoreCase(serverEncoding)) {
            enable = true;
        }
    }

    /**
     * 字符串编码
     * 
     * @param s String
     * @return String
     * @throws UnsupportedEncodingException
     */
    public String encode(String s) throws UnsupportedEncodingException {
        if (enable && !isEmpty(s)) {
            s = new String(s.getBytes(clientEncoding), serverEncoding);
        }
        return s;
    }

    /**
     * 字符串解码
     * 
     * @param s String
     * @return String
     * @throws UnsupportedEncodingException
     */
    public String decode(String s) throws UnsupportedEncodingException {
        if (enable && !isEmpty(s)) {
            s = new String(s.getBytes(serverEncoding), clientEncoding);
        }
        return s;
    }

    /**
     * 判断空字符串
     * 
     * @param s String
     * @return boolean
     */
    public boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

}
