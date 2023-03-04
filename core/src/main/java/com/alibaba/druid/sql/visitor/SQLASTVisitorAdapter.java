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
package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.DbType;

public class SQLASTVisitorAdapter implements SQLASTVisitor {
    protected DbType dbType;
    protected int features;

    public final boolean isEnabled(VisitorFeature feature) {
        return VisitorFeature.isEnabled(this.features, feature);
    }

    public void config(VisitorFeature feature, boolean state) {
        features = VisitorFeature.config(features, feature, state);
    }

    public int getFeatures() {
        return features;
    }

    public void setFeatures(int features) {
        this.features = features;
    }
}
