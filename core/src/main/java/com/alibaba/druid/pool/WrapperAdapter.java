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
package com.alibaba.druid.pool;

import java.sql.Wrapper;

public class WrapperAdapter implements Wrapper {
    /**
     * 构造函数.
     */
    public WrapperAdapter() {
    }

    /**
     * 检查是否是指定接口的包装器.
     * @param iface 接口类
     * @return 是否是指定接口的实例
     */
    @Override
    public boolean isWrapperFor(final Class<?> iface) {
        return iface != null && iface.isInstance(this);

    }

    /**
     * 解包为指定接口类型.
     * @param iface 接口类
     * @param <T> 类型参数
     * @return 解包后的对象
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> iface) {
        if (iface == null) {
            return null;
        }

        if (iface.isInstance(this)) {
            return (T) this;
        }

        return null;
    }

}
