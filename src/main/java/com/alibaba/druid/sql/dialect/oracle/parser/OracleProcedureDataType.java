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
package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLParameter;
import com.alibaba.druid.sql.ast.SQLStatement;

import java.util.ArrayList;
import java.util.List;

public class OracleProcedureDataType extends SQLDataTypeImpl {
    private boolean isStatic = false;
    private final List<SQLParameter> parameters = new ArrayList<SQLParameter>();

    private SQLStatement block;

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public List<SQLParameter> getParameters() {
        return parameters;
    }

    public OracleProcedureDataType clone() {
        OracleProcedureDataType x = new OracleProcedureDataType();
        cloneTo(x);

        x.isStatic = isStatic;
        for (SQLParameter parameter : parameters) {
            SQLParameter p2 = parameter.clone();
            p2.setParent(x);
            x.parameters.add(p2);
        }

        return x;
    }

    public SQLStatement getBlock() {
        return block;
    }

    public void setBlock(SQLStatement block) {
        if (block != null) {
            block.setParent(this);
        }
        this.block = block;
    }
}
