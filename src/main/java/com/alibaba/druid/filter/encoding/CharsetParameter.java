/**
 * Project: druid
 * 
 * File Created at 2010-12-2
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
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
