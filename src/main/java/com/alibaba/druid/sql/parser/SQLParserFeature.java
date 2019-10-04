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
package com.alibaba.druid.sql.parser;

public enum SQLParserFeature {
    KeepInsertValueClauseOriginalString,
    KeepSelectListOriginalString, // for improved sql parameterized performance
    UseInsertColumnsCache,
    EnableSQLBinaryOpExprGroup,
    OptimizedForParameterized,
    OptimizedForForParameterizedSkipValue,
    KeepComments,
    SkipComments,
    StrictForWall,
    EnableMultiUnion,
    IgnoreNameQuotes,

    PipesAsConcat, // for mysql
    ;

    private SQLParserFeature(){
        mask = (1 << ordinal());
    }

    public final int mask;


    public static boolean isEnabled(int features, SQLParserFeature feature) {
        return (features & feature.mask) != 0;
    }

    public static int config(int features, SQLParserFeature feature, boolean state) {
        if (state) {
            features |= feature.mask;
        } else {
            features &= ~feature.mask;
        }

        return features;
    }

    public static int of(SQLParserFeature... features) {
        if (features == null) {
            return 0;
        }

        int value = 0;

        for (SQLParserFeature feature: features) {
            value |= feature.mask;
        }

        return value;
    }
}
