/*
 * Copyright 2011 Alibaba Group.
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

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;

@SuppressWarnings("serial")
public class OracleCreateTableStatement extends SQLCreateTableStatement {

    public OracleCreateTableStatement() {

    }

    @Override
    public void output(StringBuffer buf) {
        if (Type.GLOBAL_TEMPORARY.equals(this.type)) {
            buf.append("CREATE GLOBAL TEMPORARY TABLE ");
        } else if (Type.LOCAL_TEMPORARY.equals(this.type)) {
            buf.append("CREATE LOCAL TEMPORARY TABLE ");
        } else {
            buf.append("CREATE TABLE ");
        }

        this.name.output(buf);
        buf.append(" ");
        buf.append("(");
        for (int i = 0, size = tableElementList.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            tableElementList.get(i).output(buf);
        }
        buf.append(")");
    }
}
