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
package com.alibaba.druid.filter.stat;

public class MergeStatFilter extends StatFilter {

    public MergeStatFilter(){
        super.setMergeSql(true);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface == MergeStatFilter.class || iface == StatFilter.class;
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) {
        if (iface == MergeStatFilter.class || iface == StatFilter.class) {
            return (T) this;
        }
        return null;
    }
}
