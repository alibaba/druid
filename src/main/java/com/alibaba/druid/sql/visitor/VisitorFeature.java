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
package com.alibaba.druid.sql.visitor;

public enum  VisitorFeature {
    OutputUCase,
    OutputPrettyFormat,
    OutputParameterized,
    OutputDesensitize,
    OutputUseInsertValueClauseOriginalString,
    OutputSkipSelectListCacheString,
    OutputSkipInsertColumnsString,
    OutputParameterizedQuesUnMergeInList,
    OutputParameterizedQuesUnMergeOr,

    /**
     * @deprecated
     */
    OutputKeepParenthesisWhenNotExpr
    ;

    private VisitorFeature(){
        mask = (1 << ordinal());
    }

    public final int mask;


    public static boolean isEnabled(int features, VisitorFeature feature) {
        return (features & feature.mask) != 0;
    }

    public static int config(int features, VisitorFeature feature, boolean state) {
        if (state) {
            features |= feature.mask;
        } else {
            features &= ~feature.mask;
        }

        return features;
    }

    public static int of(VisitorFeature... features) {
        if (features == null) {
            return 0;
        }

        int value = 0;

        for (VisitorFeature feature: features) {
            value |= feature.mask;
        }

        return value;
    }
}
