/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

/**
 * 类URLParameter.java的实现描述：JDBC 参数类
 * 
 * @author hexianmao 2007-5-24 上午11:21:59
 */
public class CharsetParameter {

    public static final String CLIENTENCODINGKEY = "clientEncoding";

    public static final String SERVERENCODINGKEY = "serverEncoding";

    // 数据库客户端编码
    private String             clientEncoding;

    // 数据库服务器端编码
    private String             serverEncoding;

    public String getClientEncoding() {
        return clientEncoding;
    }

    public void setClientEncoding(String clientEncoding) {
        this.clientEncoding = clientEncoding;
    }

    public String getServerEncoding() {
        return serverEncoding;
    }

    public void setServerEncoding(String serverEncoding) {
        this.serverEncoding = serverEncoding;
    }

}
