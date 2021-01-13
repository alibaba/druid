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
package com.alibaba.druid.sql.ast;

import java.util.List;

public abstract class SQLExprImpl extends SQLObjectImpl implements SQLExpr {

    public SQLExprImpl(){

    }

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    public abstract SQLExpr clone();

    public SQLDataType computeDataType() {
        return null;
    }

    @Override
    public List<SQLObject> getChildren() {
        return null;
    }

}
