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

import java.util.ArrayList;
import java.util.List;

public class MappingContext {

    private List<Object> parameters;

    private boolean      generateAlias          = false;
    private boolean      explainAllColumnToList = false;

    private Entity       defaultEntity;

    public MappingContext(){
        this(new ArrayList<Object>());
    }

    public MappingContext(List<Object> parameters){
        this.parameters = parameters;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public boolean isGenerateAlias() {
        return generateAlias;
    }

    public void setGenerateAlias(boolean generateAlias) {
        this.generateAlias = generateAlias;
    }

    public boolean isExplainAllColumnToList() {
        return explainAllColumnToList;
    }

    public void setExplainAllColumnToList(boolean explainAllColumnToList) {
        this.explainAllColumnToList = explainAllColumnToList;
    }

    public Entity getDefaultEntity() {
        return defaultEntity;
    }

    public void setDefaultEntity(Entity defaultEntity) {
        this.defaultEntity = defaultEntity;
    }

}
