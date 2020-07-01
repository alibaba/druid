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
package com.alibaba.druid.sql.builder;

public interface SQLUpdateBuilder {

    SQLUpdateBuilder from(String table);

    SQLUpdateBuilder from(String table, String alias);

    SQLUpdateBuilder limit(int rowCount);

    SQLUpdateBuilder limit(int rowCount, int offset);

    SQLUpdateBuilder where(String sql);

    SQLUpdateBuilder whereAnd(String sql);

    SQLUpdateBuilder whereOr(String sql);

    SQLUpdateBuilder set(String... items);
}
