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
package com.alibaba.druid.support.http;

import javax.servlet.http.HttpServletRequest;

/**
 * Druid web console安全性控制SPI.
 *
 * 扩展此接口以匹配项目内的认证机制, 实现SSO.
 * 需要将实现类配置在META-INF/services/com.alibaba.druid.support.http.DruidWebSecurityProvider.
 */
public interface DruidWebSecurityProvider {
    /**
     * 检查用户是否未授权访问.
     *
     * @param request 请求
     * @return 如果用户未授权访问true, 否则返回false。
     */
    boolean isNotPermit(HttpServletRequest request);
}
