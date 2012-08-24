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
package com.alibaba.druid.mapping.spi;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingObject;
import com.alibaba.druid.mapping.Property;

public class PropertyValue extends MappingObject {

    private Property property;
    private Entity   entity;
    private Object   value;

    public PropertyValue(Entity entity, Property property, Object value){
        this.property = property;
        this.entity = entity;
        this.value = value;
    }

    public PropertyValue(){
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
