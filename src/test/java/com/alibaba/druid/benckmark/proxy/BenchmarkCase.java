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
package com.alibaba.druid.benckmark.proxy;

public abstract class BenchmarkCase {

    private final String name;

    public BenchmarkCase(String name){
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUp(SQLExecutor sqlExec) throws Exception {

    }

    public abstract void execute(SQLExecutor sqlExec) throws Exception;

    public void tearDown(SQLExecutor sqlExec) throws Exception {

    }
}
