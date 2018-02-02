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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;

public interface OracleConstraint extends OracleSQLObject, SQLConstraint, SQLTableElement {

    SQLName getExceptionsInto();

    void setExceptionsInto(SQLName exceptionsInto);

    Boolean getDeferrable();

    void setDeferrable(Boolean enable);

    Boolean getEnable();

    void setEnable(Boolean enable);

    Boolean getValidate();
    void setValidate(Boolean validate);

    Initially getInitially();

    void setInitially(Initially value);

    OracleUsingIndexClause getUsing();

    void setUsing(OracleUsingIndexClause using);

    public static enum Initially {
        DEFERRED, IMMEDIATE
    }

    OracleConstraint clone();
}
