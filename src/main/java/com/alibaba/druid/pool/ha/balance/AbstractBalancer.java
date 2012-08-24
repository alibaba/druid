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
package com.alibaba.druid.pool.ha.balance;

import com.alibaba.druid.pool.ha.MultiDataSource;

public abstract class AbstractBalancer implements Balancer {

    private MultiDataSource multiDataSource;

    private boolean         inited = false;

    @Override
    public synchronized void init(MultiDataSource multiDataSource) {
        if (this.inited) {
            return;
        }
        
        if (multiDataSource == null) {
            throw new IllegalStateException();
        }

        this.multiDataSource = multiDataSource;

        inited = true;
    }
    
    public boolean isInited() {
        return inited;
    }

    public MultiDataSource getMultiDataSource() {
        return multiDataSource;
    }

}
