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

import com.alibaba.druid.proxy.jdbc.WrapperProxy;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class PoolableWrapper implements Wrapper {

    private final Wrapper wrapper;

    public PoolableWrapper(Wrapper wraaper){
        this.wrapper = wraaper;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {

        if (null == wrapper) {
            //Best to log error.
            return false;
        }

        if (iface == null) {
            return false;
        }

        if (iface == wrapper.getClass()) {
            return true;
        }

        if (iface == this.getClass()) {
            return true;
        }
        
        if (!(wrapper instanceof WrapperProxy)) {
            if (iface.isInstance(wrapper)) {
                return true;
            }
        }

        return wrapper.isWrapperFor(iface);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {

        if (null == wrapper) {
            //Best to log error.
            return null;
        }

        if (iface == null) {
            return null;
        }

        if (iface == wrapper.getClass()) {
            return (T) wrapper;
        }

        if (iface == this.getClass()) {
            return (T) this;
        }
        
        if (!(wrapper instanceof WrapperProxy)) {
            if (iface.isInstance(wrapper)) {
                return (T) wrapper;
            }
        }


        return wrapper.unwrap(iface);
    }

}
