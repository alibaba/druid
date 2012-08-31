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
package com.alibaba.druid.filter.config.security.decrypter;

/**
 * <pre>
 * 存放， 数据库的关键参数：URL， 用户名， 密码
 * 如果存放的是明文， 请尽可能缩短实例的作用域
 * </pre>
 *
 * @author Jonas Yang
 */
public class SensitiveParameters {
    private transient final String url;

    private transient final String username;

    private transient final String password;

    public SensitiveParameters(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
