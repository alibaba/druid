/*
 * Copyright 2011 Alibaba Group.
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

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;

@SuppressWarnings("serial")
public class SQLCharactorDataType extends SQLDataTypeImpl {

    private String charSetName;
    private String collate;

    public SQLCharactorDataType(String name){
        super(name);
    }

    public String getCharSetName() {
        return charSetName;
    }

    public void setCharSetName(String charSetName) {
        this.charSetName = charSetName;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }

}
