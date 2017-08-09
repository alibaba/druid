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

import java.util.ArrayList;
import java.util.List;

public class ExportParameterizedOutputVisitor extends SQLASTOutputVisitor implements ExportParameterVisitor {

    /**
     * true= if require parameterized sql output
     */
    private final boolean requireParameterizedOutput;

    public ExportParameterizedOutputVisitor(final List<Object> parameters,final Appendable appender,final boolean wantParameterizedOutput){
        super(appender, true);
        this.parameters = parameters;
        this.requireParameterizedOutput = wantParameterizedOutput;
    }

    public ExportParameterizedOutputVisitor() {
        this(new ArrayList<Object>());
    }

    public ExportParameterizedOutputVisitor(final List<Object> parameters){
        this(parameters,new StringBuilder(),false);
    }

    public ExportParameterizedOutputVisitor(final Appendable appender) {
        this(new ArrayList<Object>(), appender, true);
    }

    
    public List<Object> getParameters() {
        return parameters;
    }
}
