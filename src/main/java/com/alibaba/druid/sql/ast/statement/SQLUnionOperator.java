/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.ast.statement;

public enum SQLUnionOperator {
    UNION("UNION"),
    UNION_ALL("UNION ALL"),
    MINUS("MINUS"),
    MINUS_DISTINCT("MINUS DISTINCT"),
    MINUS_ALL("MINUS ALL"),
    EXCEPT("EXCEPT"),
    EXCEPT_ALL("EXCEPT ALL"),
    EXCEPT_DISTINCT("EXCEPT DISTINCT"),
    INTERSECT("INTERSECT"),
    INTERSECT_ALL("INTERSECT ALL"),
    INTERSECT_DISTINCT("INTERSECT DISTINCT"),
    DISTINCT("UNION DISTINCT");

    public final String name;
    public final String name_lcase;

    private SQLUnionOperator(String name){
        this.name = name;
        this.name_lcase = name.toLowerCase();
    }

    public String toString() {
        return name;
    }
}
