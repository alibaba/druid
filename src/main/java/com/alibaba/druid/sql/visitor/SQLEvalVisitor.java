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

import java.util.List;

import com.alibaba.druid.sql.visitor.functions.Function;

public interface SQLEvalVisitor extends SQLASTVisitor {

    public static final String EVAL_VALUE       = "eval.value";
    public static final String EVAL_EXPR        = "eval.expr";
    public static final Object EVAL_ERROR       = new Object();
    public static final Object EVAL_VALUE_COUNT = new Object();
    public static final Object EVAL_VALUE_NULL  = new Object();

    Function getFunction(String funcName);

    void registerFunction(String funcName, Function function);

    void unregisterFunction(String funcName);

    List<Object> getParameters();

    void setParameters(List<Object> parameters);

    int incrementAndGetVariantIndex();

    boolean isMarkVariantIndex();

    void setMarkVariantIndex(boolean markVariantIndex);
}
