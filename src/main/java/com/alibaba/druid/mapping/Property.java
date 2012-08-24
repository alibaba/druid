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
package com.alibaba.druid.mapping;

public class Property extends MappingObject {
    private String name;
    private String desciption;
    private String dbColumnName;

    public Property(){
    }

    public Property(String name, String desciption, String dbColumnName){
        this.name = name;
        this.desciption = desciption;
        this.dbColumnName = dbColumnName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public void setDbColumnName(String dbColumnName) {
        this.dbColumnName = dbColumnName;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{name=");
        buf.append(name);
        
        buf.append(", dbColumnName=");
        buf.append(dbColumnName);
        
        buf.append("}");

        return buf.toString();
    }
}
