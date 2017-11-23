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

public enum SQLObjectType {
    TABLE("TABLE"), // 
    FUNCTION("FUNCTION"), // 
    PROCEDURE("PROCEDURE"), // 
    USER("USER"), //
    DATABASE("DATABASE"), //
    ROLE("ROLE"), // 
    PROJECT("PROJECT"), // 
    PACKAGE("PACKAGE"), // 
    RESOURCE("RESOURCE"), // 
    INSTANCE("INSTANCE"), // 
    JOB("JOB"), // 
    VOLUME("VOLUME"), // 
    OfflineModel("OFFLINEMODEL"), // 
    XFLOW("XFLOW") // for odps
    ;
    
    public final String name;
    public final String name_lcase;
    
    SQLObjectType(String name) {
        this.name = name;
        this.name_lcase = name.toLowerCase();
    }
}
